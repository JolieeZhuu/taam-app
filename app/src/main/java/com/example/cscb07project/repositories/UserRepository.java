package com.example.cscb07project.repositories;

import android.util.Log;

import com.example.cscb07project.entities.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class UserRepository {
    private final DatabaseReference dbRefUs;
    private final DatabaseReference dbRefAd;
    private final FirebaseAuth dbAuth;

    public UserRepository(FirebaseDatabase rootRef, FirebaseAuth dbAuth) {
        this.dbRefUs = rootRef.getReference("users");
        this.dbRefAd = rootRef.getReference("admins");
        this.dbAuth = dbAuth;
    }


    /**
     * Signs up a user through Firebase Authentication, then adds the user's
     * non-sensitive data to Firebase Realtime
     * @param username
     * @param email
     * @param password
     * @return asynchronous task with return type User
     */
    public Task<User> createUser(final String username, String email, String password) {
        return dbAuth.createUserWithEmailAndPassword(email, password).continueWithTask(task -> {
            if (!task.isSuccessful()) throw Objects.requireNonNull(task.getException());

            String userId = Objects.requireNonNull(task.getResult().getUser()).getUid();
            User user = new User(userId, username, email);

            return addUserWithId(user, userId).continueWith(dbTask -> {
                if (!dbTask.isSuccessful()) throw Objects.requireNonNull(dbTask.getException());
                return user;
            });
        }); // by default, somewhere in the Login fragments, a collection will be created
    } // tested

    // upon creating a user in Auth, their user id will be stored in Realtime
    private Task<Void> addUserWithId(User user, String userId) {
        return dbRefUs.child(userId).setValue(user);
    }


    /**
     * Verifies user login through Firebase Authentication
     * @param email
     * @param password
     * @return asynchronous task with return type User
     */
    public Task<User> signIn(String email, String password) {
        return dbAuth.signInWithEmailAndPassword(email, password).continueWithTask(task -> {
            if (!task.isSuccessful()) throw Objects.requireNonNull(task.getException());

            String userId = Objects.requireNonNull(task.getResult().getUser()).getUid();

            return getUserById(userId);
        });
    } // tested

    private Task<User> getUserById(String userId) {
        return dbRefUs.child(userId).get().continueWith(snapshot -> {
            if (snapshot.getResult() != null) {
                return snapshot.getResult().getValue(User.class);
            }
            return null;
        });
    } // tested

    public Task<Void> updateUsername(User user, String username) {
        user.setUsername(username);
        return dbRefUs.child(user.getUserId()).updateChildren(user.toMap());
    } // tested

    public Task<Boolean> isAdmin(String userId) { // checks to see if user is an admin from admin table
        return dbRefAd.child(userId).get().continueWith(snapshot -> {
            if (!snapshot.isSuccessful() || snapshot.getResult() == null)
                return false;
            return snapshot.getResult().exists();
        });
    } // tested

    private Task<Void> deleteUserById(String userId) {
        return dbRefUs.child(userId).removeValue().addOnSuccessListener(snapshot -> {
            Log.d("delete from firebase", "successfully deleted user with id: " + userId);
        }).addOnFailureListener(e -> {
            Log.e("firebase error", "error from deleting user with id: " + userId);
        });
    }


    /**
     * Deletes a user from Firebase authentication and
     * Realtime database. Deleting admin specifically must
     * be handled separately.
     * @return asynchronous function with return type void
     */
    public Task<Void> deleteUser() {
        FirebaseUser user = dbAuth.getCurrentUser();
        if (user == null) return Tasks.forResult(null);
        final String userId = user.getUid();

        return user.delete().continueWithTask(task -> {
            if (!task.isSuccessful()) throw Objects.requireNonNull(task.getException());
            return deleteUserById(userId);
        });
    } // tested

    public void signOut() {
        dbAuth.signOut();
    }



    /* WILL DELETE THIS SOON!!! PLEASE DO NOT DELETE THIS I NEED IT (from Yile) */

    // -- functions related to username uniqueness checks -- //

    public FirebaseUser getCurrentUser() {
        return dbAuth.getCurrentUser();
    }


    /**
     * Gets a user by username.
     * @param username
     * @return asynchronous task with return type User.
     */
    private Task<User> getUserByUsername(String username) {
        return dbRefUs.orderByChild("usernameLower").equalTo(username.toLowerCase().strip()).get().continueWith(snapshot -> {
            if (!snapshot.isSuccessful() || snapshot.getResult() == null) {
                return null;
            }
            if (snapshot.getResult().hasChildren()) { // since get() returns a list, iterate through list to find matching
                for (DataSnapshot userSnapshot : snapshot.getResult().getChildren()) {
                    return userSnapshot.getValue(User.class);
                }
            }
            return null;
        });
    }


    /**
     * Checks if a username is already taken.
     * @param username
     * @return asynchronous task with return type boolean, returns true iff user with that username
     * already exists.
     */
    public Task<Boolean> usernameExists(String username){
        return getUserByUsername(username).continueWith(snapshot -> {
            if(!snapshot.isSuccessful()) throw Objects.requireNonNull(snapshot.getException());
            return snapshot.getResult() != null;
        });
    }
}
