package com.example.cscb07project.systems;

import com.example.cscb07project.entities.Artifact;
import com.google.firebase.database.DatabaseError;

import java.util.List;

/**
 * Any DB call which needs to handle lists of objects here. We assume that all filtering has been
 * done by this point.
 */
public interface BatchArtifactRetriever {
    void onResult(List<Artifact> artifactList);
    void onError(DatabaseError error);
}
