package com.example.cscb07project.interfaces;

import com.example.cscb07project.entities.Collection;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.List;

public interface CollectionInterface {

    Task<Void> createNewCollection(Collection collection);

    Task<List<Collection>> getCollections(String userId);

    Task<Collection> getCollectionById(String userId, String collectionId);

    Task<Void> addArtifactToCollection(String lotNumber, Collection collection);

    // edit collection name
    Task<Void> updateCollectionName(String name, Collection collection);

    // remove artifact from collection
    Task<Void> removeArtifactFromCollection(String lotNumber, Collection collection);

    // delete collection
    Task<Void> deleteCollection(String userId, String collectionId);

    Task<List<String>> addArtifactsToCollectionAndGetFullList(String userId, List<String> lotNumbers);

    Task<List<String>> removeArtifactsFromCollectionAndGetFullList(String userId, List<String> lotNumbers);
}
