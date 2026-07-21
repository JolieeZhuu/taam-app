package com.example.cscb07project.interfaces;

import com.example.cscb07project.entities.Collection;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

public interface CollectionInterface {
    void addCollection(String userId, Collection collection);

    Task<DataSnapshot> getCollectionById(String collectionId);
}
