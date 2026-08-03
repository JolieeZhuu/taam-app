package com.example.cscb07project.repositories;

import android.util.Log;

import com.example.cscb07project.entities.Collection;
import com.example.cscb07project.entities.Comment;
import com.example.cscb07project.entities.ExpandedView;
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

public class CollectionRepository {
    private final DatabaseReference rootRef;
    private final DatabaseReference dbRef;
    private final DatabaseReference dbRefArtColl; // for artifacts in collection
    public CollectionRepository(FirebaseDatabase rootRef) {
        this.rootRef = rootRef.getReference();
        this.dbRef = rootRef.getReference("collections");
        this.dbRefArtColl = rootRef.getReference("artifactCollections");
    }

    /**
     * Creation functions for collection
     */
    public Task<Void> createNewCollection(Collection collection) {
        String collectionId = dbRef.child(collection.getUserId()).push().getKey();
        if (collectionId == null) throw new IllegalStateException();
        collection.setCollectionId(collectionId);
        return dbRef.child(collection.getUserId()).setValue(collection);
    } // tested

    /**
     * Fetch functions for collections
     */
    public Task<Collection> getCollectionByUserId(String userId) {
        return dbRef.child(userId).get().continueWith(snapshot -> {
            if (snapshot.getResult() != null) {
                return snapshot.getResult().getValue(Collection.class);
            }
            return null;
        });
    } // tested

    private Task<List<String>> getAllCollectionsFromArtifact(String lotNumber) {
        return dbRefArtColl.child(lotNumber).get().continueWith(task -> {
            if (!task.isSuccessful() || task.getResult() == null) return null;
            List<String> userIds = new ArrayList<>(); // userIds, because each user gets only one collection
            if (task.getResult().hasChildren()) {
                for (DataSnapshot collSnapshot : task.getResult().getChildren()) {
                    userIds.add(collSnapshot.getKey());
                }
                return userIds;
            }
            return null;
        });
    } // tested


    /**
     * Save functions for collection; in
     * particular, one artifact or multiple
     * to a collection
     */

    // function specifically for expanded view
    public Task<Void> addArtifactToCollection(String lotNumber, String userId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("collections/" + userId + "/artifacts/" + lotNumber, true);
        updates.put("artifactCollections/" + lotNumber + "/" + userId, true); // used for easier querying
        return rootRef.updateChildren(updates);
    } // tested


    /**
     * Returns a list of lot numbers upon adding a selection of artifacts
     * to a collection from a specific user. Function specifically for
     * Collection/Catalogue screen
     * @param userId
     * @param lotNumbers
     * @return asynchronous task with return type List of strings
     */
    public Task<List<String>> addArtifactsToCollectionAndGetFullList(String userId, List<String> lotNumbers) {
        Map<String,Boolean> map = new HashMap<>();
        for(String id:lotNumbers) map.put(id, true);

        return saveToCollection(userId, map).continueWithTask(task ->{
            if(!task.isSuccessful())throw Objects.requireNonNull(task.getException());
            return getCollectionByUserId(userId);
        }).continueWith(task -> {
            Collection collection = task.getResult();
            if(collection!=null && collection.getArtifacts()!=null) {
                return new ArrayList<>(collection.getArtifacts().keySet());
            }
            return new ArrayList<>(); // in case of null
        });
    } // tested

    private Task<Void> saveToCollection(String userId, Map<String, Boolean> newArtifacts) {
        DatabaseReference userRef = dbRef.child(userId);
        return userRef.get().continueWithTask(task -> {
            Map<String, Object> updates = new HashMap<>();
            DataSnapshot snapshot = task.getResult();
            if (snapshot != null && snapshot.exists()) {
                for (String lotNumber : newArtifacts.keySet()) {
                    updates.put("collections/" + userId + "/artifacts/" + lotNumber, true);
                    updates.put("artifactCollections/" + lotNumber + "/" + userId, true);
                }
            } else {
                // make new  collection with keys if DNE
                String collectionId = userRef.push().getKey();
                if (collectionId == null) throw new IllegalStateException();
                updates.put("collections/" + userId + "/userId/", userId);
                updates.put("collections/" + userId + "/collectionId/", collectionId);
                updates.put("collections/" + userId + "/name/", "My Default Collection");

                for (String lotNumber : newArtifacts.keySet()) {
                    updates.put("collections/" + userId + "/artifacts/" + lotNumber, true);
                    updates.put("artifactCollections/" + lotNumber + "/" + userId, true);
                }
            }
            return rootRef.updateChildren(updates);
        });
    }

    // checks if artifact already exists; function specifically for expanded view
    public Task<Boolean> isArtifactInCollection(String lotNumber, String userId) {
        return dbRefArtColl.child(lotNumber).child(userId).get().continueWith(task -> {
            return task.isSuccessful() && task.getResult().exists();
        });
    } // tested

    public Task<Void> updateCollectionName(String name, Collection collection) {
        collection.setName(name);
        return dbRef.child(collection.getUserId()).updateChildren(collection.toMap());
    } // tested

    /**
     * Unsave functions for collection; in
     * particular, one artifact or multiple
     * to a collection
     */

    /**
     * Unsaves an artifact from a collection. Function specifically
     * for expanded view
     * @param lotNumber
     * @param userId
     * @return
     */
    public Task<Void> removeArtifactFromCollection(String lotNumber, String userId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("collections/" + userId + "/artifacts/" + lotNumber, null);
        updates.put("artifactCollections/" + lotNumber + "/" + userId, null);
        return rootRef.updateChildren(updates);
    } // tested

    /**
     * Unsaves a selection of artifacts from a collection. Function
     * specifically for Collection/Catalogue screen
     * @param userId
     * @param lotNumbers
     * @return
     */
    public Task<List<String>> removeArtifactsFromCollectionAndGetFullList(String userId, List<String> lotNumbers) {
        Map<String, Object> updates = new HashMap<>();
        for(String id: lotNumbers) {
            updates.put("collections/" + userId + "/artifacts/" + id, null);
            updates.put("artifactCollections/" + id, null); // must update the querying table too
        }

        return rootRef.updateChildren(updates).continueWithTask(task ->{
            if(!task.isSuccessful())throw Objects.requireNonNull(task.getException());
            return getCollectionByUserId(userId);
        }).continueWith(task -> {
            Collection collection = task.getResult();
            if(collection!=null && collection.getArtifacts()!=null) {
                return new ArrayList<>(collection.getArtifacts().keySet());
            }
            return new ArrayList<>(); // in case of null
        });
    } // tested

    /**
     * Deletion functions for collection
     */

    /**
     * When an artifact is completed deleted, every collection from every user
     * that had this artifact must be updated to no longer include it
     * @param lotNumber
     * @return asynchronous task with return type void
     */
    public Task<Void> removeArtifactFromAllCollections(String lotNumber) {
        return getAllCollectionsFromArtifact(lotNumber).continueWithTask(task -> {
            Map<String, Object> updates = new HashMap<>();
            if (task.isSuccessful() && task.getResult() != null) {
                List<String> collectionIds = task.getResult();
                for (String id : collectionIds) {
                    updates.put("collections/" + id + "/artifacts/" + lotNumber, null);
                }
            }
            updates.put("artifactCollections/" + lotNumber, null); // must update the querying table too
            return rootRef.updateChildren(updates);
        });
    } // tested


    /* MAY DELETE LATER ON */

    // delete collection
    // unneeded? unless we can have deleting users
    public Task<Void> deleteCollection(String userId) {
        return getCollectionByUserId(userId).continueWithTask(task -> {
            if (!task.isSuccessful() || task.getResult() == null) throw Objects.requireNonNull(task.getException());
            Collection col = task.getResult();
            Map<String, Object> updates = new HashMap<>();
            if (col.getArtifacts() != null) {
                for (String lotNumber : col.getArtifacts().keySet()) {
                    updates.put("artifactCollections/" + lotNumber + "/" + userId, null);
                }
            }
            updates.put("collections/" + userId, null);
            return rootRef.updateChildren(updates);
        })
        .addOnSuccessListener(snapshot -> {
            Log.d("delete from firebase", "successfully deleted collection with id: " + userId);
        }).addOnFailureListener(e -> {
            Log.e("firebase error", "error from deleting collection with id: " + userId);
        });
    }  // tested

}
