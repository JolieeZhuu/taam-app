package com.example.cscb07project.interfaces;

import com.example.cscb07project.entities.Artifact;
import com.google.android.gms.tasks.Task;

public interface ArtifactInterface {
    // add user
    Task<Void> addArtifact(Artifact artifact);

    Task<Artifact> getArtifactByLotNumber(String lotNumber);

    Task<Void> updateArtifact(Artifact artifact);

    Task<Void> deleteArtifactByLotNumber(String lotNumber);
}
