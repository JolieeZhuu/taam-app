package com.example.cscb07project;

import com.example.cscb07project.entities.Comment;
import com.example.cscb07project.entities.ExpandedView;
import com.example.cscb07project.entities.User;
import com.example.cscb07project.interfaces.ExpandedViewInterface;
import com.example.cscb07project.repositories.ExpandedViewRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.*;

import android.util.Log;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExpandedViewRepositoryTest {
    private ExpandedViewRepository expandedViewRepository;
    private FirebaseDatabase dbRef;
    private static String lotNumber;
    private static String commentId;

    @Before
    public void setup() {
        dbRef = FirebaseDatabase.getInstance("https://cscb07-project-e0581-default-rtdb.firebaseio.com/");
        expandedViewRepository = new ExpandedViewRepository(dbRef);
        lotNumber = "lotNumber0123";
    }

    @Test
    public void test1AddExpandedView() throws Exception {
        ExpandedView expandedView = new ExpandedView(lotNumber);
        Task<Void> task = expandedViewRepository.addExpandedView(expandedView);
        Tasks.await(task);

        assertTrue(task.isSuccessful());
    }

    @Test
    public void test2AddComment() throws Exception {
        Comment comment = new Comment(lotNumber, "hardcodeuserId", "hello this is my comment");
        Task<Void> task = expandedViewRepository.addComment(comment);
        Tasks.await(task);

        assertTrue(task.isSuccessful());
        commentId = comment.getCommentId();
    }

    @Test
    public void test3UpdateComment() throws Exception {
        Comment comment = new Comment(lotNumber, commentId, "hardcodeuserId", "changed comment");
        Task<Void> task = expandedViewRepository.updateComment(comment);
        Tasks.await(task);

        assertTrue(task.isSuccessful());
    }

    @Test
    public void test4GetExpandedViewByLotNumber() throws Exception {
        Task<ExpandedView> task = expandedViewRepository.getExpandedViewByLotNumber(lotNumber);
        Tasks.await(task);

        assertEquals(lotNumber, task.getResult().getLotNumber());
        Log.d("test test", task.getResult().toString());
        assertNotNull(task.getResult());
        assertEquals(Integer.valueOf(0), task.getResult().getLikeNumber());
    }

    @Test
    public void test51IncreaseLike() throws Exception {
        Task<ExpandedView> task1 = expandedViewRepository.getExpandedViewByLotNumber(lotNumber);
        Tasks.await(task1);
        assertEquals(lotNumber, task1.getResult().getLotNumber());

        ExpandedView expandedView = task1.getResult();
//        Log.d("firebase likes", expandedView.toString());
        Task<Void> task2 = expandedViewRepository.like("hardcodeuserId223", expandedView);
        Tasks.await(task2);

        assertTrue(task2.isSuccessful());
    }

    @Test
    public void test52DecreaseLike() throws Exception {
        Task<ExpandedView> task1 = expandedViewRepository.getExpandedViewByLotNumber(lotNumber);
        Tasks.await(task1);
        assertEquals(lotNumber, task1.getResult().getLotNumber());

        ExpandedView expandedView = task1.getResult();
        Task<Void> task2 = expandedViewRepository.unlike("hardcodeuserId223", expandedView);
        Tasks.await(task2);

        assertTrue(task2.isSuccessful());
    }

    @Test
    public void test6GetCommentById() throws Exception {
        Task<Comment> task = expandedViewRepository.getCommentById(lotNumber, commentId);
        Tasks.await(task);

        assertEquals(lotNumber, task.getResult().getLotNumber());
        assertEquals("hardcodeuserId", task.getResult().getUserId());
        assertEquals("changed comment", task.getResult().getComment());
    }

    @Test
    public void test7GetCommentsByLotNumber() throws Exception {
        Task<List<Comment>> task = expandedViewRepository.getCommentsByLotNumber(lotNumber);
        Tasks.await(task);

        for (Comment comment : task.getResult()) {
            assertEquals("hardcodeuserId", comment.getUserId());
            assertEquals("changed comment", comment.getComment());
        }
    }

    @Test
    public void test8DeleteCommentById() throws Exception {
        Task<Void> task = expandedViewRepository.deleteCommentById(commentId);
        Tasks.await(task);
        assertTrue(task.isSuccessful());
    }

    @Test
    public void test9DeleteExpandedViewByLotNumber() throws Exception {
        Task<Void> task = expandedViewRepository.deleteExpandedViewByLotNumber(lotNumber);
        Tasks.await(task);
        assertTrue(task.isSuccessful());
    }
}
