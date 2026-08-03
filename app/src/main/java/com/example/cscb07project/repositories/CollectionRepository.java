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

    public Task<Void> createNewCollection(Collection collection) {
        String collectionId = dbRef.child(collection.getUserId()).push().getKey();
        if (collectionId == null) throw new IllegalStateException();
        collection.setCollectionId(collectionId);
        return dbRef.child(collection.getUserId()).setValue(collection);
    } // tested

//ADDED BY EL
    //take the list of artrifacts and save them into db
    public Task<Void> saveToCollection(String userId, Map<String, Boolean> newArtifacts) {
        DatabaseReference userRef = dbRef.child(userId);
        return userRef.get().continueWithTask(task -> {
            Map<String, Object> updates = new HashMap<>();
            DataSnapshot snapshot = task.getResult();
            if (snapshot != null && snapshot.exists()) {
                for (String lotNumber : newArtifacts.keySet()) {
                    updates.put("collections/" + userId + "/artifacts/" + lotNumber, true);
                    updates.put("artifactCollections/" + lotNumber + "/" + userId, true);
                }
//                // update existing col
//                return userRef.child("artifacts")
//                        .updateChildren(new HashMap<>(newArtifacts));
            } else {
                //make new  collection with keys if DNE
                String collectionId = userRef.push().getKey();
                if (collectionId == null) throw new IllegalStateException();
                updates.put("collections/" + userId + "/userId/", userId);
                updates.put("collections/" + userId + "/collectionId/", collectionId);
                updates.put("collections/" + userId + "/name/", "My Default Collection");

                for (String lotNumber : newArtifacts.keySet()) {
                    updates.put("collections/" + userId + "/artifacts/" + lotNumber, true);
                    updates.put("artifactCollections/" + lotNumber + "/" + userId, true);
                }

//                Map<String, Object> data = new HashMap<>();
//                data.put("userId", userId);
//                data.put("collectionId", collectionId);
//                data.put("name", "My Default Collection");
//                data.put("artifacts", newArtifacts);
//                return userRef.setValue(data);
            }
            return rootRef.updateChildren(updates);
        });
    }

    // by E
    // for retrieving the collection pertaining to the user
//    public Task<Collection> getCollection(String userId) {
//        return dbRef.child(userId).get().continueWith(snapshot -> {
//            DataSnapshot a = snapshot.getResult();
//            if (a != null && a.exists()) {
//                String colID = a.child("collectionId").getValue(String.class);
//                Map<String, Boolean> artifacts = (Map<String, Boolean>) a.child("artifacts").getValue();
//                return new Collection(userId, colID, "My Default Collection", artifacts);
//            }
//            // error gettign col
//            return null;
//        });
//    }

    public Task<Collection> getCollectionByUserId(String userId) {
        return dbRef.child(userId).get().continueWith(snapshot -> {
            DataSnapshot a = snapshot.getResult();
            if (a != null && a.exists()) {
                String colID = a.child("collectionId").getValue(String.class);
                Map<String, Boolean> artifacts = (Map<String, Boolean>) a.child("artifacts").getValue();
                return new Collection(userId, colID, "My Default Collection", artifacts);
            }
            // error gettign col
            return null;
        });
    }

//    // should do it by userId
//    public Task<Collection> getCollectionById(String collectionId) {
//        return dbRef.orderByChild("userId").equalTo(collectionId).get().continueWith(snapshot -> {
//            if (!snapshot.isSuccessful() || snapshot.getResult() == null) {
//                return null;
//            }
//            if (snapshot.getResult().hasChildren()) {
//                for (DataSnapshot collectionSnapshot : snapshot.getResult().getChildren()) {
//                    return collectionSnapshot.getValue(Collection.class);
//                }
//            }
//            return null;
//        });
//    }

    // adds to both collection, and a separate structure in a different nested format for easier retrieval
    // by lotnumber
    public Task<Void> addArtifactToCollection(String lotNumber, String userId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("collections/" + userId + "/artifacts/" + lotNumber, true);
        updates.put("artifactCollections/" + lotNumber + "/" + userId, true);
        return rootRef.updateChildren(updates);
    } // tested

//    public Task<Void> addArtifactToCollection(String lotNumber, Collection collection) {
//        Map<String, Boolean> artifacts = collection.getArtifacts();
//        if (artifacts == null) {
//            artifacts = new HashMap<>();
//        }
//        if (artifacts.get(lotNumber) == null) { // new artifact!
//            artifacts.put(lotNumber, true);
//            collection.setArtifacts(artifacts);
//            return dbRef.child(collection.getUserId()).updateChildren(collection.toMap()).continueWithTask(task -> {
//                Map<String, Object> updates = new HashMap<>();
//                updates.put(collection.getUserId(), true);
//                return dbRefArtColl.child(lotNumber).updateChildren(updates);
//            });
//        }
//        return Tasks.forResult(null);
//    } // tested

    //elina
    // add the selected artifacts to the coll and then fetches the updated from db and returns it
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
            return new ArrayList<>(); //incase of null
        });
    }

    public Task<Boolean> isArtifactInCollection(String lotNumber, String userId) {
        return dbRefArtColl.child(lotNumber).child(userId).get().continueWith(task -> {
            return task.isSuccessful() && task.getResult().exists();
        });
    }

    // edit collection name
    public Task<Void> updateCollectionName(String name, Collection collection) {
        collection.setName(name);
        return dbRef.child(collection.getUserId()).updateChildren(collection.toMap());
    }

    // remove artifact from collection
//    public Task<Void> removeArtifactFromCollection(String lotNumber, Collection collection) {
//        Map<String, Boolean> artifacts = collection.getArtifacts();
//        if (artifacts.get(lotNumber) != null) {
//            artifacts.remove(lotNumber);
//            collection.setArtifacts(artifacts);
//            return dbRef.child(collection.getUserId()).updateChildren(collection.toMap()).continueWithTask(task -> {
//                Map<String, Object> updates = new HashMap<>();
//                updates.put(collection.getUserId(), null);
//                return dbRefArtColl.child(lotNumber).updateChildren(updates);
//            });
//        }
//        return Tasks.forResult(null);
//    } // tested

    public Task<Void> removeArtifactFromCollection(String lotNumber, String userId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("collections/" + userId + "/artifacts/" + lotNumber, null);
        updates.put("artifactCollections/" + lotNumber + "/" + userId, null);
        return rootRef.updateChildren(updates);
    } // tested

    //remove artifact from ALL collections
    public Task<Void> removeArtifactFromAllCollections(String lotNumber) {
        return getAllCollectionsFromArtifact(lotNumber).continueWithTask(task -> {
            Map<String, Object> updates = new HashMap<>();
            if (!task.isSuccessful() || task.getResult() == null) throw new IllegalStateException();
            List<String> collectionIds = task.getResult();
            for (String id : collectionIds) {
                updates.put("collections/" + id + "/artifacts/" + lotNumber, null);
            }
            updates.put("artifactCollections/" + lotNumber, null);
            return rootRef.updateChildren(updates);
        });
    } // tested

    private Task<List<String>> getAllCollectionsFromArtifact(String lotNumber) {
        return dbRefArtColl.child(lotNumber).get().continueWith(task -> {
            if (!task.isSuccessful() || task.getResult() == null) return null;
            List<String> collectionIds = new ArrayList<>(); // technically userIds, but easier to understand it this way
            if (task.getResult().hasChildren()) {
                for (DataSnapshot collSnapshot : task.getResult().getChildren()) {
                    collectionIds.add(collSnapshot.getKey());
                }
                return collectionIds;
            }
            return null;
        });
    } // tested

    // delete collection
    // unneeded? unless we can have deleting users
    public Task<Void> deleteCollection(String userId, String collectionId) {
        return dbRef.child(userId).removeValue().addOnSuccessListener(snapshot -> {
            Log.d("delete from firebase", "successfully deleted collection with id: " + collectionId);
        }).addOnFailureListener(e -> {
            Log.e("firebase error", "error from deleting collection with id: " + collectionId);
        });
    } // i don't think we need, or should have, this function

//    public boolean isArtifactSavedByUser(String currentLotNumber, Collection collection){
//        if (collection == null || collection.getArtifacts() == null) {
//            return false;
//        }
//        Boolean saved = collection.getArtifacts().get("lot_"+currentLotNumber);
//        if (saved != null && saved == true) {
//            return true;
//        }
//        return false;
//    }


    public Task<List<String>> removeArtifactsFromCollectionAndGetFullList(String userId, List<String> lotNumbers) {

        Map<String, Object> updates = new HashMap<>();
        for(String id: lotNumbers) {

            updates.put("collections/" + userId + "/artifacts/" + id, null); //
            updates.put("artifactCollections/" + id, null);
        }
//        for (String id : collectionIds) {
//            updates.put("collections/" + id + "/artifacts/" + lotNumber, null);
//        }
//        updates.put("artifactCollections/" + lotNumber, null);

        return rootRef.updateChildren(updates).continueWithTask(task ->{
            if(!task.isSuccessful())throw Objects.requireNonNull(task.getException());
            return getCollectionByUserId(userId);
        }).continueWith(task -> {
            Collection collection = task.getResult();
            if(collection!=null && collection.getArtifacts()!=null) {
                return new ArrayList<>(collection.getArtifacts().keySet());
            }
            return new ArrayList<>(); //incase of null
        });
    }


//    public boolean isArtifactLikedByUser(String userId, ExpandedView expandedView) {
//        return expandedView.getLikes() != null && expandedView.getLikes().containsKey(userId);
//    }

//    public Task<Collection> getCollectionByName(String userId, String collectionName) {
//        Task<Collection> task = dbRef.child(userId).get().continueWith(snapshot -> {
//                    if (!snapshot.isSuccessful() || snapshot.getResult() == null) {
//                        return null;
//                    }
//                    if (snapshot.getResult().hasChildren()) {
//                        for (DataSnapshot collectionSnapshot : snapshot.getResult().getChildren()) {
//                            Collection collection = collectionSnapshot.getValue(Collection.class);
//                            if (collection != null && collectionName.equals(collection.getName())) {
//                                return collection;
//                            }
//                        }
//                    }
//                    return null;
//                });
//        return task;
//
//    }
}
