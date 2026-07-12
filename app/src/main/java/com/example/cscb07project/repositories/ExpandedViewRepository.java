package com.example.cscb07project.repositories;

import com.example.cscb07project.entities.Artifact;
import com.example.cscb07project.entities.Comment;
import com.example.cscb07project.entities.ExpandedView;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ExpandedViewRepository {
    // also for Comment repository
    private final DatabaseReference dbRefEx;
    private final DatabaseReference dbRefCo;

    public ExpandedViewRepository(FirebaseDatabase rootRef) {
        this.dbRefEx = rootRef.getReference("expandedViews");
        this.dbRefCo = rootRef.getReference("comments");
    }

    public void addExpandedView(String lotNumber, ExpandedView expandedView) {
        dbRefEx.child(lotNumber).setValue(expandedView);
    }

    public void addComment(String lotNumber, Comment comment) {
        String commentId = dbRefCo.push().getKey();
        if (commentId == null) throw new IllegalStateException();
        comment.setCommentId(commentId);

        // may need to verify if lotNumber exists

        dbRefCo.child(lotNumber).child(commentId).setValue(comment);
    }

    public Task<DataSnapshot> getExpandedViewByLotNumber(String lotNumber) {
        return dbRefEx.child(lotNumber).get();
    }

    public Task<DataSnapshot> getCommentById(String lotNumber, String commentId) {
        return dbRefCo.child(lotNumber).child(commentId).get(); // will need to test this
    }

    public Task<DataSnapshot> getCommentsByLotNumber(String lotNumber) {
        return dbRefCo.child(lotNumber).get();
    }
}
