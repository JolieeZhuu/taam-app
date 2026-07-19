package com.example.cscb07project.login;
import com.example.cscb07project.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.mindrot.jbcrypt.BCrypt;
public class LoginModel implements MVPInterface.model {
    private final UserRepository userRepo;

    public LoginModel(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public LoginModel() {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://cscb07-project-e0581-default-rtdb.firebaseio.com/");
        this.userRepo = new UserRepository(db, FirebaseAuth.getInstance());
    }

    @Override
    public void authenticateUser(String email, String password, String username, callback callback) {
        userRepo.signIn(email, password)
                .addOnSuccessListener(callback::onSuccess)
                .addOnFailureListener(e -> callback.onError("Incorrect username or password"));
    }
}
