package com.example.cscb07project.login;

import com.example.cscb07project.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

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
                .addOnFailureListener(e -> callback.onError("PLACEHOLDER ERROR MESSAGE"));
        //TODO: return custom error messages
    }
}