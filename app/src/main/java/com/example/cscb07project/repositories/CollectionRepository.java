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
        return dbRef.child(collection.getUserId()).child(collectionId).setValue(collection);
    }
//ADDED BY EL
    //take the list of artrifacts and save them into db
    public Task<Void> saveToCollection(String userId, Map<String, Boolean> newArtifacts) {
        DatabaseReference userRef = dbRef.child(userId);
        return userRef.get().continueWithTask(task -> {
            DataSnapshot snapshot = task.getResult();
            if (snapshot != null && snapshot.exists()) {
                // update existing col
                return userRef.child("artifacts")
                        .updateChildren(new HashMap<>(newArtifacts));
            } else {
//shoud alreayd be made so error ?
                //msut do eror chekcing
            }
            return null;
        });
    }
    // by E
    // for retrieving the collection pertaining to the user
    public Task<Collection> getCollection(String userId) {
        return dbRef.child(userId).get().continueWith(snapshot -> {
            DataSnapshot a = snapshot.getResult();
            if (a != null && a.exists()) {
                String colID = a.child("collectionId").getValue(String.class);
                Map<String, Boolean> artifacts = (Map<String, Boolean>) a
                        .child("artifacts").getValue();
                return new Collection(userId, colID, "default_collection", artifacts);
            }
            // error gettign col
            return null;
        });
    }

    //unneeded?
    public Task<List<Collection>> getCollections(String userId) {
        return dbRef.child(userId).get().continueWith(snapshot -> {
            if (!snapshot.isSuccessful() || snapshot.getResult() == null) return null;
            List<Collection> collectionList = new ArrayList<>();
            if (snapshot.getResult().hasChildren()) {
                for (DataSnapshot collectionSnapshot : snapshot.getResult().getChildren()) {
                    collectionList.add(collectionSnapshot.getValue(Collection.class));
                }
                return collectionList;
            }
            return null;
        });
    }

    public Task<Collection> getCollectionById(String userId, String collectionId) {
        return dbRef.child(userId).child(collectionId).get().continueWith(snapshot -> {
            if (snapshot.getResult() != null) {
                return snapshot.getResult().getValue(Collection.class);
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
            return dbRef.child(collection.getUserId()).child(collection.getCollectionId()).updateChildren(collection.toMap());
        }
        return Tasks.forResult(null);
    }

    // edit collection name
    public Task<Void> updateCollectionName(String name, Collection collection) {
        collection.setName(name);
        return dbRef.child(collection.getUserId()).child(collection.getCollectionId()).updateChildren(collection.toMap());
    }

    // remove artifact from collection
    public Task<Void> removeArtifactFromCollection(String lotNumber, Collection collection) {
        Map<String, Boolean> artifacts = collection.getArtifacts();
        if (artifacts.get(lotNumber) != null) {
            artifacts.remove(lotNumber);
            collection.setArtifacts(artifacts);
            return dbRef.child(collection.getUserId()).child(collection.getCollectionId()).updateChildren(collection.toMap());
        }
        return Tasks.forResult(null);
    }

    // delete collection
    // unneeded? unless we can have deleting users
    public Task<Void> deleteCollection(String userId, String collectionId) {
        return dbRef.child(userId).child(collectionId).removeValue().addOnSuccessListener(snapshot -> {
            Log.d("delete from firebase", "successfully deleted collection with id: " + collectionId);
        }).addOnFailureListener(e -> {
            Log.e("firebase error", "error from deleting collection with id: " + collectionId);
        });
    }

    //elina
    // add the selected artifacts to the coll and then fetches the updated from db and returns it
    public Task<List<String>> addArtifactsToCollectionAndGetFullList(String userId,
                                                                     List<String> lotNumbers){
        Map<String,Boolean> map = new HashMap<>();
        for(String id:lotNumbers) map.put(id, true);

        return saveToCollection(userId, map).continueWithTask(task ->{
            if(!task.isSuccessful())throw Objects.requireNonNull(task.getException());
            return getCollection(userId);
        }).continueWith(task -> {
            Collection collection = task.getResult();
            if(collection!=null && collection.getArtifacts()!=null) {
                return new ArrayList<>(collection.getArtifacts().keySet());
            }
            return new ArrayList<>(); //incase of null
        });
    }

    public Task<List<String>> removeArtifactsFromCollectionAndGetFullList(String userId, List<String> lotNumbers) {

        Map<String, Object> map = new HashMap<>();
        for(String id:lotNumbers) map.put("artifacts/" + id, null); //

        return dbRef.child(userId).updateChildren(map).continueWithTask(task ->{
            if(!task.isSuccessful())throw Objects.requireNonNull(task.getException());
            return getCollection(userId);
        }).continueWith(task -> {
            Collection collection = task.getResult();
            if(collection!=null && collection.getArtifacts()!=null) {
                return new ArrayList<>(collection.getArtifacts().keySet());
            }
            return new ArrayList<>(); //incase of null
        });
    }
}
