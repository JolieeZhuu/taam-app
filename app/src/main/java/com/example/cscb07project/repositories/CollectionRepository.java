package com.example.cscb07project.repositories;

import android.util.Log;

import com.example.cscb07project.entities.Collection;
import com.example.cscb07project.entities.Comment;
import com.example.cscb07project.entities.ExpandedView;
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
    private final DatabaseReference dbRefArtColl; // for artifacts in collection
    public CollectionRepository(FirebaseDatabase rootRef) {
        this.dbRef = rootRef.getReference("collections");
        this.dbRefArtColl = rootRef.getReference("artifactCollections");
    }
    public Task<Void> createNewCollection(Collection collection) {
        String collectionId = dbRef.child(collection.getUserId()).push().getKey();
        if (collectionId == null) throw new IllegalStateException();
        collection.setCollectionId(collectionId);
        ///////
        return dbRef.child(collection.getUserId()).child(collectionId).setValue(collection);
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
        if (artifacts.get("lot_" + lotNumber) == null) { // new artifact!
            artifacts.put("lot_" + lotNumber, true);
            collection.setArtifacts(artifacts);
            return dbRef.child(collection.getUserId()).child(collection.getCollectionId()).updateChildren(collection.toMap()).continueWithTask(task -> {
                Map<String, Object> updates = new HashMap<>(); // will add a separate structure for easier deletion
                updates.put(collection.getCollectionId(), true);
                return dbRefArtColl.child(lotNumber).updateChildren(updates); // should i be throwing stuff lmao?
            });
        }
        return Tasks.forResult(null);
    }

    public Task<List<String>> addArtifactsToCollectionAndGetFullList(String userId,
                                                                     List<String> lotNumbers) {
        Map<String,Boolean> map = new HashMap<>();
        for(String id:lotNumbers) map.put("lot_"+id, true);

        return saveToCollection(userId, map).continueWithTask(task ->{
            if(!task.isSuccessful())throw Objects.requireNonNull(task.getException());
            return getCollectionByUserId(userId);
        }).continueWith(task -> {
            Collection collection = task.getResult();
            if (collection != null
                    && collection.getArtifacts() != null) {

                List<String> result = new ArrayList<>();

                for (String artifactKey
                        : collection.getArtifacts().keySet()) {

                    if (artifactKey.startsWith("lot_")) {
                        result.add(artifactKey.substring(4));
                    } else {
                        result.add(artifactKey);
                    }
                }

                return result;
            }
            return new ArrayList<>();
        });
    }

    public boolean isArtifactInCollection(String artifactId, Collection collection) {
        return collection.getArtifacts().containsKey("lot_"+artifactId);
    }

    // edit collection name
    public Task<Void> updateCollectionName(String name, Collection collection) {
        collection.setName(name);
        return dbRef.child(collection.getUserId()).child(collection.getCollectionId()).updateChildren(collection.toMap());
    }

    // remove artifact from collection
    public Task<Void> removeArtifactFromCollection(String lotNumber, Collection collection) {
        Map<String, Boolean> artifacts = collection.getArtifacts();
        if (artifacts.get("lot_" +lotNumber) != null) {
            artifacts.remove("lot_" +lotNumber);
            collection.setArtifacts(artifacts);
            return dbRef.child(collection.getUserId()) .child(collection.getCollectionId()).updateChildren(collection.toMap()).continueWithTask(task -> {
                Map<String, Object> updates = new HashMap<>(); // will add a separate structure for easier deletion
                updates.put(collection.getCollectionId(), null);
                return dbRefArtColl.child(lotNumber).updateChildren(updates); // should i be throwing stuff lmao?
            });
        }
        return Tasks.forResult(null);
    }

    //remove artifact from ALL collections
    public Task<Void> removeArtifactFromAllCollections(String lotNumber) {
        return getAllCollectionsFromArtifact(lotNumber).continueWithTask(task -> {
            Map<String, Object> updates = new HashMap<>();
            if (!task.isSuccessful() || task.getResult() == null) throw new IllegalStateException();
            List<String> collectionIds = task.getResult();
            for (String id : collectionIds) {
                updates.put("collections/" + id + "/artifacts/" + lotNumber, null);
            }
            dbRef.updateChildren(updates);
            return Tasks.forResult(null);
        });
    } // will need to test this

    private Task<List<String>> getAllCollectionsFromArtifact(String lotNumber) {
        return dbRefArtColl.child(lotNumber).get().continueWith(task -> {
            if (!task.isSuccessful() || task.getResult() == null) return null;
            List<String> collectionIds = new ArrayList<>();
            if (task.getResult().hasChildren()) {
                for (DataSnapshot collSnapshot : task.getResult().getChildren()) {
                    collectionIds.add(collSnapshot.getKey());
                }
                return collectionIds;
            }
            return null;
        });
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

    public boolean isArtifactSavedByUser(String currentLotNumber, Collection collection){
        if (collection == null || collection.getArtifacts() == null) {
            return false;
        }
        Boolean saved = collection.getArtifacts().get("lot_"+currentLotNumber);
        if (saved != null && saved == true) {
            return true;
        }
        return false;
    }


    public boolean isArtifactLikedByUser(String userId, ExpandedView expandedView) {
        return expandedView.getLikes() != null && expandedView.getLikes().containsKey(userId);
    }
    public Task<Collection> getCollectionByName(String userId, String collectionName) {
        Task<Collection> task = dbRef.child(userId).get().continueWith(snapshot -> {
                    if (!snapshot.isSuccessful() || snapshot.getResult() == null) {
                        return null;
                    }
                    if (snapshot.getResult().hasChildren()) {
                        for (DataSnapshot collectionSnapshot : snapshot.getResult().getChildren()) {
                            Collection collection = collectionSnapshot.getValue(Collection.class);
                            if (collection != null && collectionName.equals(collection.getName())) {
                                return collection;
                            }
                        }
                    }
                    return null;
                });
        return task;

    }
}
