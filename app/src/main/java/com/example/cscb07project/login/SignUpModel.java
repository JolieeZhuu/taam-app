package com.example.cscb07project.login;

import android.util.Log;

import com.example.cscb07project.entities.Collection;
import com.example.cscb07project.repositories.CollectionRepository;
import com.example.cscb07project.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.FirebaseDatabase;
import java.util.regex.Pattern;


import java.util.Objects;

public class SignUpModel implements MVPInterface.model {
    /*
    Model half of the MVP signup flow. Unlike LoginModel, does not implement
    AdminCheckable, as new users are never admins. Owns both a UserRepository
    and a CollectionRepository, since signup needs to create a user profile
    AND their default Collection as one logical unit. See authenticateUser
    for the actual signup steps.

    NOTE TO FUTURE: REQUIRE READ ACCESS TO "usernameLower" IN DB
     */
    private final Pattern p = Pattern.compile("^[a-zA-Z0-9]+$");
    private final UserRepository userRepo;
    private final CollectionRepository collectionRepository;

    public SignUpModel() {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://cscb07-project-e0581-default-rtdb.firebaseio.com/");
        this.userRepo = new UserRepository(db, FirebaseAuth.getInstance());
        this.collectionRepository = new CollectionRepository(db);
    }

    public SignUpModel(UserRepository userRepo, CollectionRepository collectionRepository) {
        this.userRepo = userRepo;
        this.collectionRepository = collectionRepository;
    }

    /**
     * Validates username format, checks username uniqueness, creates the Firebase Auth user,
     * writes their profile, and creates their default collection, all before
     * reporting success.
     */
    @Override
    public void authenticateUser(String email, String password, String username, callback callback) {
        /*
        This function handles signing up new users
         */
        // Race condition? If multiple people sign up with same username at same time there could be
        // collisions. Acceptable trade off because its very unlikely.
        if (!p.matcher(username).matches()) {
            callback.onError("Username must be alphanumeric characters only.");
            return;
        }
        userRepo.usernameExists(username)
                        .addOnSuccessListener(exist ->{
                            if(exist){
                                callback.onError("Username is already taken.");
                            }
                            else {
                                userRepo.createUser(username, email, password)
                                        .continueWithTask(task -> {
                                            if (task.isSuccessful()) {
                                                return collectionRepository.createNewCollection(new Collection(task.getResult().getUserId(), "My Default Collection"))
                                                        .continueWith(task2 -> {
                                                            if (task2.isSuccessful())
                                                                return task.getResult();
                                                            throw Objects.requireNonNull(task2.getException());
                                                        });
                                            }
                                            throw Objects.requireNonNull(task.getException());
                                        })
                                        .addOnSuccessListener(callback::onSuccess)
                                        .addOnFailureListener(e -> callback.onError(getError(e)));
                            }

                        })
                .addOnFailureListener(e -> {
                    callback.onError(getError(e));
                });

    }

    /**
     * Maps Firebase Auth exceptions to user-friendly error strings.
     */
    private String getError(Exception e) {
        if (e instanceof FirebaseAuthWeakPasswordException) {
            return "Password must be at least 6 characters.";
        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
            return "The email address entered is invalid.";
        } else if (e instanceof FirebaseAuthUserCollisionException) {
            return "A user with that email already exists.";
        } else if (e instanceof FirebaseAuthException) {
            String code = ((FirebaseAuthException) e).getErrorCode();

            switch (code) {
                case "ERROR_TOO_MANY_ATTEMPTS_TRY_LATER":
                    return "Too many requests.";
                case "ERROR_OPERATION_NOT_ALLOWED":
                    return "Signup is not available at the moment.";
            }
        }
        return "Something went wrong.";
    }
}