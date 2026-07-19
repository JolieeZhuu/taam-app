package com.example.cscb07project.repositories;

import com.example.cscb07project.entities.Collection;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CollectionRepository {
    private final DatabaseReference dbRef;
    public CollectionRepository(FirebaseDatabase rootRef) {
        this.dbRef = rootRef.getReference("collections");
    }

    public void addCollection(String userId, Collection collection) {
        String collectionId = dbRef.child(userId).push().getKey();
        if (collectionId == null) throw new IllegalStateException();
        collection.setCollectionId(collectionId);
        dbRef.child(userId).setValue(collection);
    }

    public Task<DataSnapshot> getCollectionById(String collectionId) {
        return dbRef.child(collectionId).get();
    }
}
