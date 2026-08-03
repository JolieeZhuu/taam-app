package com.example.cscb07project;

import com.example.cscb07project.entities.Artifact;
import com.example.cscb07project.repositories.ArtifactRepository;
import com.example.cscb07project.repositories.ExpandedViewRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.*;

import android.util.Log;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ArtifactRepositoryTest {
    private ArtifactRepository artifactRepository;
    private FirebaseDatabase dbRef;
    private static String artifactLotNumber;

    @Before
    public void setup() {
        dbRef = FirebaseDatabase.getInstance("https://cscb07-project-e0581-default-rtdb.firebaseio.com/");
        artifactRepository = new ArtifactRepository(dbRef, new ExpandedViewRepository(dbRef));
    }

    @Test
    public void test1AddArtifact() throws Exception {
        Artifact artifact = new Artifact("abc123", "Artifact A", "Artifact description", "Furniture", "Wood", "Shang Dynasty", null, null, null, null, null, null, null, null, null);
        Task<Void> task = artifactRepository.addArtifact(artifact);
        Tasks.await(task);

        assertTrue(task.isSuccessful());
        artifactLotNumber = artifact.getLotNumber();
    }

    @Test
    public void test2GetArtifactByLotNumber() throws Exception {
        Task<Artifact> task = artifactRepository.getArtifactByLotNumber(artifactLotNumber);
        Tasks.await(task);

        assertEquals("Artifact A", task.getResult().getName());
        assertEquals("Furniture", task.getResult().getCategory());
        assertEquals("Wood", task.getResult().getMaterial());
    }

    @Test
    public void test3UpdateArtifact() throws Exception {
        Artifact artifact = new Artifact("abc123", "Artifact A", "Artifact description", "Furniture", "Wood", "Shang Dynasty", null, null, null, "idk", null, null, null, null, "https://firebasestorage.googleapis.com/v0/b/cscb07-project-e0581.firebasestorage.app/o/artifactImages%2F29.jpg?alt=media&token=79c80310-5cea-4480-8e7d-7f83430bd7a3");
        Task<Void> task = artifactRepository.updateArtifact(artifact);
        Tasks.await(task);

        assertTrue(task.isSuccessful());
    }

    @Test
    public void test4DeleteArtifactByLotNumber() throws Exception {
        Task<Void> task = artifactRepository.deleteArtifactByLotNumber(artifactLotNumber);
        Tasks.await(task);

        assertTrue(task.isSuccessful());
    }
}
