package com.example.cscb07project;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cscb07project.entities.User;
import com.example.cscb07project.repositories.UserRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EmailPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    private Task<User> createAccount(final String username, String email, String password, UserRepository userRepository) {
        return mAuth.createUserWithEmailAndPassword(email, password)
            .continueWithTask(task -> {
                if (!task.isSuccessful()) throw task.getException();

                String userId = task.getResult().getUser().getUid();
                User user = new User(userId, username);

                return userRepository.addUser(user)
                        .continueWith(dbTask -> {
                            if (!dbTask.isSuccessful()) throw dbTask.getException();
                            return user;
                        });
            });
    }
}