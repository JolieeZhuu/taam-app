package com.example.cscb07project.repositories;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AdminRepository {
    private final DatabaseReference dbRefAd;

    public AdminRepository(FirebaseDatabase rootRef) {
        this.dbRefAd = rootRef.getReference("admins");
    }

    /**
     * Creates an admin given its user id, adding it to admin table
     * in Firebase (NOT the user table)
     * @param userId
     * @return asynchronous task with return type void
     */
    public Task<Void> createAdmin(String userId) {
        Map<String, Object> map = new HashMap<>();
        map.put(userId, true);
        return dbRefAd.updateChildren(map);
    } // untested

    /**
     * Deletes an admin object given its user id from the admin table
     * in Firebase (NOT the user table)
     * @param userId
     * @return asynchronous task with return type void
     */
    public Task<Void> deleteAdminById(String userId) {
        return dbRefAd.child(userId).removeValue().addOnSuccessListener(snapshot -> {
            Log.d("delete from firebase", "successfully deleted user with id: " + userId);
        }).addOnFailureListener(e -> {
            Log.e("firebase error", "error from deleting user with id: " + userId);
        });
    } // untested
}
