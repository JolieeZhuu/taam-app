package com.example.cscb07project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.cscb07project.entities.Collection;
import com.example.cscb07project.interfaces.CollectionInterface;
import com.example.cscb07project.repositories.CollectionRepository;
import com.example.cscb07project.repositories.ExpandedViewRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CollectionRepositoryTest {
    private CollectionRepository collectionRepository;
    private FirebaseDatabase dbRef;
    private static String userId;
    private static String collectionId;
    private static Collection collection;

    @Before
    public void setup() {
        dbRef = FirebaseDatabase.getInstance("https://cscb07-project-e0581-default-rtdb.firebaseio.com/");
        collectionRepository = new CollectionRepository(dbRef);
        userId = "TESTUSERTESTUSER"; // hard coded, should be from a logged-in user
    }

    @Test
    public void test1CreateNewCollection() throws Exception {
        String name = "My First Collection!";
        collection = new Collection(userId, name);
        Task<Void> task = collectionRepository.createNewCollection(collection);
        Tasks.await(task);

        assertTrue(task.isSuccessful());
        collectionId = collection.getCollectionId();
//
//        task = collectionRepository.createNewCollection(new Collection("randomuserid", "My Default Collection"));
//        Tasks.await(task);
//        task = collectionRepository.createNewCollection(new Collection("randomuserid1", "My Default Collection"));
//        Tasks.await(task);
//        task = collectionRepository.createNewCollection(new Collection("randomuserid2", "My Default Collection"));
//        Tasks.await(task);
    }

//    @Test
//    public void test2AddArtifactToCollection() throws Exception {
//        String lotNumber = "2"; // hard coded, should be from the artifact they selected
//        collection = new Collection(userId, collectionId, "My First Collection!",  null);
//        Task<Void> task = collectionRepository.addArtifactToCollection(lotNumber, userId);
//        Tasks.await(task);
//        assertTrue(task.isSuccessful());
////        assertNotNull(collection.getArtifacts().get(lotNumber));
//
//        lotNumber = "aaa";
//        task = collectionRepository.addArtifactToCollection(lotNumber, userId);
//        Tasks.await(task);
//
//        lotNumber = "1a11111";
//        task = collectionRepository.addArtifactToCollection(lotNumber, userId);
//        Tasks.await(task);
//    }

    @Test
    public void test23IsArtifactInCollection() throws Exception {
        String lotNumber = "2";
        Task<Boolean> task = collectionRepository.isArtifactInCollection(lotNumber, userId);
        Tasks.await(task);
        assertTrue(task.getResult());
    }
//
//    @Test
//    public void test3GetCollectionById() throws Exception {
//        Task<Collection> task = collectionRepository.getCollectionById(collectionId);
//        Tasks.await(task);
//
//        assertEquals(collectionId, task.getResult().getCollectionId());
//        assertEquals("My First Collection!", task.getResult().getName());
//        assertEquals(userId, task.getResult().getUserId());
//    }
//
//    @Test
//    public void test4GetCollectionByUserId() throws Exception {
//        Task<Collection> task = collectionRepository.getCollectionByUserId(userId);
//        Tasks.await(task);
//        assertEquals(task.getResult().getCollectionId(), collectionId); // i only tested this assuming there's only one collection
//    }
//
//    @Test
//    public void test5UpdateCollectionName() throws Exception {
//        Task<Void> task = collectionRepository.updateCollectionName("Not a first collection", collection);
//        Tasks.await(task);
//
//        assertEquals("Not a first collection", collection.getName());
//    }
//
//    @Test
//    public void test6RemoveArtifactFromCollection() throws Exception {
//        Task<Void> task = collectionRepository.removeArtifactFromCollection("1a11111", collection);
//        Tasks.await(task);
//
//        assertNull(collection.getArtifacts().get("1a11111"));
//        assertNotNull(collection.getArtifacts().get("2"));
//    }
//
//    @Test
//    public void test67RemoveArtifactFromAllCollections() throws Exception {
//        Task<Void> task = collectionRepository.removeArtifactFromAllCollections("aaa");
//        Tasks.await(task);
//
//        assertTrue(task.isSuccessful());
//    }
//
//    @Test
//    public void test7DeleteCollection() throws Exception {
//        Task<Void> task = collectionRepository.deleteCollection(userId, collectionId);
//        Tasks.await(task);
//
//        assertTrue(task.isSuccessful());
//    }
}
