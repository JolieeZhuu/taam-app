package com.example.cscb07project.repositories;

import android.util.Log;

import com.example.cscb07project.entities.User;
import com.example.cscb07project.interfaces.UserInterface;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class UserRepository implements UserInterface {
    private final DatabaseReference dbRefUs;
    private final DatabaseReference dbRefAd;
    private final FirebaseAuth dbAuth;

    public UserRepository(FirebaseDatabase rootRef, FirebaseAuth dbAuth) {
        this.dbRefUs = rootRef.getReference("users");
        this.dbRefAd = rootRef.getReference("admins");
        this.dbAuth = dbAuth;
    }

    @Override
    public Task<User> createUser(final String username, String email, String password) {
        return dbAuth.createUserWithEmailAndPassword(email, password).continueWithTask(task -> {
            if (!task.isSuccessful()) throw Objects.requireNonNull(task.getException());

            String userId = Objects.requireNonNull(task.getResult().getUser()).getUid();
            User user = new User(userId, username, email);

            return addUserWithId(user, userId).continueWith(dbTask -> {
                if (!dbTask.isSuccessful()) throw Objects.requireNonNull(dbTask.getException());
                return user;
            });
        });
    }

    @Override
    public Task<User> signIn(String email, String password) {
        return dbAuth.signInWithEmailAndPassword(email, password).continueWithTask(task -> {
            if (!task.isSuccessful()) throw Objects.requireNonNull(task.getException());

            String userId = Objects.requireNonNull(task.getResult().getUser()).getUid();

            return getUserById(userId);
        });
    }

    @Override
    public Task<Void> updateUsername(User user, String username) {
        user.setUsername(username);
        return dbRefUs.child(user.getUserId()).updateChildren(user.toMap());
    }

    public Task<Void> addUserWithId(User user, String userId) {
        return dbRefUs.child(userId).setValue(user);
    }

    // check if user is an admin
    public Task<Boolean> isAdmin(String userId) {
        return dbRefAd.child(userId).get().continueWith(snapshot -> {
            if (!snapshot.isSuccessful() || snapshot.getResult() == null)
                return snapshot.getResult() != null;
            return snapshot.getResult().child(userId).getValue(Boolean.class) != null;
        });
    }

    public Task<Void> updateUser(User user) {
        return dbRefUs.child(user.getUserId()).updateChildren(user.toMap());
    }

    private Task<Void> deleteUserById(String userId) {
        return dbRefUs.child(userId).removeValue().addOnSuccessListener(snapshot -> {
            Log.d("delete from firebase", "successfully deleted user with id: " + userId);
        }).addOnFailureListener(e -> {
            Log.e("firebase error", "error from deleting user with id: " + userId);
        });
    }

    // only deletes user (could be an admin)
    @Override
    public Task<Void> deleteUser() {
        FirebaseUser user = dbAuth.getCurrentUser();
        if (user == null) return Tasks.forResult(null);
        final String userId = user.getUid();

        return user.delete().continueWithTask(task -> {
            if (!task.isSuccessful()) throw Objects.requireNonNull(task.getException());
            return deleteUserById(userId);
        });
    }

    public void signOut() {
        dbAuth.signOut();
    }

    private Task<User> getUserById(String userId) {
        return dbRefUs.child(userId).get().continueWith(snapshot -> {
            if (snapshot.getResult() != null) {
                return snapshot.getResult().getValue(User.class);
            }
            return null;
        });
    }

    @Override
    public Task<User> getUserByEmail(String email) {
        return dbRefUs.orderByChild("email").equalTo(email).get().continueWith(snapshot -> {
            if (!snapshot.isSuccessful() || snapshot.getResult() == null) {
                return null;
            }
            if (snapshot.getResult().hasChildren()) {
                for (DataSnapshot userSnapshot : snapshot.getResult().getChildren()) {
                    return userSnapshot.getValue(User.class);
                }
            }
            return null;
        });
    }

    @Override
    public Task<User> getUserByUsername(String username) {
        return dbRefUs.orderByChild("username").equalTo(username).get().continueWith(snapshot -> {
            if (!snapshot.isSuccessful() || snapshot.getResult() == null) {
                return null;
            }
            if (snapshot.getResult().hasChildren()) {
                for (DataSnapshot userSnapshot : snapshot.getResult().getChildren()) {
                    return userSnapshot.getValue(User.class);
                }
            }
            return null;
        });
    }
}