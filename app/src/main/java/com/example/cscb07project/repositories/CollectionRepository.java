package com.example.cscb07project.repositories;

import android.util.Log;

import com.example.cscb07project.entities.Collection;
import com.example.cscb07project.entities.Comment;
import com.example.cscb07project.interfaces.CollectionInterface;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CollectionRepository implements CollectionInterface {
    private final DatabaseReference dbRef;
    public CollectionRepository(FirebaseDatabase rootRef) {
        this.dbRef = rootRef.getReference("collections");
    }
    public Task<Void> createNewCollection(Collection collection) {
        String collectionId = dbRef.child(collection.getUserId()).push().getKey();
        if (collectionId == null) throw new IllegalStateException();
        collection.setCollectionId(collectionId);
        return dbRef.child(collection.getUserId()).setValue(collection);
    }

    public Task<Collection> getCollectionByUserId(String userId) {
        return dbRef.child(userId).get().continueWith(snapshot -> {
            if (snapshot.getResult() != null) {
                return snapshot.getResult().getValue(Collection.class);
            }
            return null;
        });
    }

    // should do it by userId
    public Task<Collection> getCollectionById(String collectionId) {
        return dbRef.orderByChild("collectionId").equalTo(collectionId).get().continueWith(snapshot -> {
            if (!snapshot.isSuccessful() || snapshot.getResult() == null) {
                return null;
            }
            if (snapshot.getResult().hasChildren()) {
                for (DataSnapshot collectionSnapshot : snapshot.getResult().getChildren()) {
                    return collectionSnapshot.getValue(Collection.class);
                }
            }
            return null;
        });
    }

    public Task<Void> addArtifactToCollection(String lotNumber, Collection collection) {
        Map<String, Boolean> artifacts = collection.getArtifacts();
        if (artifacts == null) {
            artifacts = new HashMap<>();
        }
        if (artifacts.get(lotNumber) == null) { // new artifact!
            artifacts.put(lotNumber, true);
            collection.setArtifacts(artifacts);
            return dbRef.child(collection.getUserId()).updateChildren(collection.toMap());
        }
        return Tasks.forResult(null);
    }

    public boolean isArtifactInCollection(String artifactId, Collection collection) {
        return collection.getArtifacts().containsKey(artifactId);
    }

    // edit collection name
    public Task<Void> updateCollectionName(String name, Collection collection) {
        collection.setName(name);
        return dbRef.child(collection.getUserId()).updateChildren(collection.toMap());
    }

    // remove artifact from collection
    public Task<Void> removeArtifactFromCollection(String lotNumber, Collection collection) {
        Map<String, Boolean> artifacts = collection.getArtifacts();
        if (artifacts.get(lotNumber) != null) {
            artifacts.remove(lotNumber);
            collection.setArtifacts(artifacts);
            return dbRef.child(collection.getUserId()).updateChildren(collection.toMap());
        }
        return Tasks.forResult(null);
    }

    public Task<Void> saveToCollection(String userId, Map<String, Boolean> newArtifacts) {
        DatabaseReference userRef = dbRef.child(userId);
        return userRef.get().continueWithTask(task -> {
            DataSnapshot snapshot = task.getResult();
            if (snapshot != null && snapshot.exists()) {
                // update existing col
                return userRef.child("artifacts").updateChildren(new HashMap<>(newArtifacts));
            } else {
                //shoud alreayd be made so error
            }
            return null;
        });
    }

    // delete collection
    public Task<Void> deleteCollection(String userId, String collectionId) {
        return dbRef.child(userId).removeValue().addOnSuccessListener(snapshot -> {
            Log.d("delete from firebase", "successfully deleted collection with id: " + collectionId);
        }).addOnFailureListener(e -> {
            Log.e("firebase error", "error from deleting collection with id: " + collectionId);
        });
    }
}
