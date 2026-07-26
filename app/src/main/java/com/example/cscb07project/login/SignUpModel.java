package com.example.cscb07project.login;

import com.example.cscb07project.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpModel implements MVPInterface.model {
    private final UserRepository userRepo;

    public SignUpModel() {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://cscb07-project-e0581-default-rtdb.firebaseio.com/");
        this.userRepo = new UserRepository(db, FirebaseAuth.getInstance());
    }

    public SignUpModel(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public void authenticateUser(String email, String password, String username, callback callback) {
        userRepo.createUser(username, email, password)
                .addOnSuccessListener(callback::onSuccess)
                .addOnFailureListener(e -> callback.onError(getError(e)));
        //MAJOR ISSUE: PEOPLE CAN HAVE SAME USERNAME.
    }

    private String getError(Exception e) {
        if (e instanceof FirebaseAuthWeakPasswordException) {
            return "Password must contain upper/lower case, special, and numeric characters.";
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