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

    public Task<Void> createAdmin(String userId) {
        Map<String, Object> map = new HashMap<>();
        map.put(userId, true);
        return dbRefAd.updateChildren(map);
    }

    // may want to include a getALlAdmins if we choose to display that in the admin interface

    // only deletes the userId in the admin table
    public Task<Void> deleteAdminById(String userId) {
        return dbRefAd.child(userId).removeValue().continueWithTask(snapshot -> {
            if (!snapshot.isSuccessful()) throw Objects.requireNonNull(snapshot.getException());
            return dbRefAd.child(userId).removeValue();
        }).addOnSuccessListener(snapshot -> {
            Log.d("delete from firebase", "successfully deleted user with id: " + userId);
        }).addOnFailureListener(e -> {
            Log.e("firebase error", "error from deleting user with id: " + userId);
        });
    }
}
