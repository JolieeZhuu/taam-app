package com.example.cscb07project.interfaces;

import com.example.cscb07project.entities.User;
import com.google.android.gms.tasks.Task;

public interface UserInterface {
    Task<User> createUser(String username, String email, String password);

    Task<User> signIn(String email, String password);

    Task<Void> updateUsername(User user, String username);

    Task<Boolean> isAdmin(String userId);

    // only deletes user (could be an admin)
    Task<Void> deleteUser();
    void signOut();
}
