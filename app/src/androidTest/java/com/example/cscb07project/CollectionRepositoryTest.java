package com.example.cscb07project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.cscb07project.entities.Collection;
import com.example.cscb07project.repositories.CollectionRepository;
import com.example.cscb07project.repositories.ExpandedViewRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.Collections;
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
        userId = "myuser123"; // hard coded, should be from a logged-in user
    }

    @Test
    public void test1CreateNewCollection() throws Exception {
        String name = "My Default Collection";
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

    @Test
    public void test2GetCollectionByUserId() throws Exception { // test for edge case
        Task<Collection> task = collectionRepository.getCollectionByUserId(userId);
        Tasks.await(task);
        assertEquals(userId, task.getResult().getUserId());
        assertNull(task.getResult().getArtifacts());
    }

    @Test
    public void test30AddArtifactToCollection() throws Exception {
        String lotNumber = "a2"; // hard coded, should be from the artifact they selected
        collection = new Collection(userId, collectionId, "My Default Collection",  null);
        Task<Void> task = collectionRepository.addArtifactToCollection(lotNumber, userId);
        Tasks.await(task);
        assertTrue(task.isSuccessful());
    }

    @Test
    public void test31AddArtifactsToCollectionAndGetFullList() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("aaa");
        list.add("1a11111");
        list.add("abracadabra");
        list.add("meowmeow");

        Task<List<String>> task = collectionRepository.addArtifactsToCollectionAndGetFullList(userId, list);
        Tasks.await(task);

        List<String> expected = new ArrayList<>();
        expected.add("aaa");
        expected.add("1a11111");
        expected.add("a2");
        expected.add("abracadabra");
        expected.add("meowmeow");

        assertEquals(expected, task.getResult());
    }


    @Test
    public void test4UpdateCollectionName() throws Exception {
        Task<Collection> task = collectionRepository.getCollectionByUserId(userId);
        Tasks.await(task);
        collection = task.getResult();

        Task<Void> task2 = collectionRepository.updateCollectionName("Not a first collection", collection);
        Tasks.await(task2);

        assertEquals("Not a first collection", collection.getName());
    }

    @Test
    public void test5RemoveArtifactFromCollection() throws Exception {
        Task<Void> task = collectionRepository.removeArtifactFromCollection("1a11111", userId);
        Tasks.await(task);

        assertTrue(task.isSuccessful());
    }

    @Test
    public void test60RemoveArtifactFromAllCollections() throws Exception {
        Task<Void> task = collectionRepository.removeArtifactFromAllCollections("aaa");
        Tasks.await(task);

        assertTrue(task.isSuccessful());
    }

    @Test
    public void test61RemoveArtifactsFromCollectionAndGetFullList() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("abracadabra");
        list.add("meowmeow");

        Task<List<String>> task = collectionRepository.removeArtifactsFromCollectionAndGetFullList(userId, list);
        Tasks.await(task);

        List<String> expected = new ArrayList<>();
        expected.add("a2");

        assertEquals(expected, task.getResult());
    }

    @Test
    public void test7IsArtifactInCollection() throws Exception {
        String lotNumber = "a2";
        Task<Boolean> task = collectionRepository.isArtifactInCollection(lotNumber, userId);
        Tasks.await(task);
        assertTrue(task.getResult());
    }

    @Test
    public void test8DeleteCollection() throws Exception {
        Task<Void> task = collectionRepository.deleteCollection(userId);
        Tasks.await(task);

        assertTrue(task.isSuccessful());
    }
}
