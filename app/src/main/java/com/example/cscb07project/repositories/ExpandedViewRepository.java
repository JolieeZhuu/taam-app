package com.example.cscb07project.repositories;

import android.util.Log;

import com.example.cscb07project.entities.Artifact;
import com.example.cscb07project.entities.Comment;
import com.example.cscb07project.entities.ExpandedView;
import com.example.cscb07project.entities.User;
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

    public Task<Void> addExpandedView(String lotNumber, ExpandedView expandedView) {
        return dbRefEx.child(lotNumber).setValue(expandedView);
    }

    public Task<Void> addComment(String lotNumber, Comment comment) {
        String commentId = dbRefCo.push().getKey();
        if (commentId == null) throw new IllegalStateException();
        comment.setCommentId(commentId);

        // may need to verify if lotNumber exists

        return dbRefCo.child(lotNumber).child(commentId).setValue(comment);
    }

    public Task<ExpandedView> getExpandedViewByLotNumber(String lotNumber) {
        return dbRefEx.child(lotNumber).get().continueWith(snapshot -> {
            if (snapshot.getResult() != null) {
                return snapshot.getResult().getValue(ExpandedView.class);
            }
            return null;
        });
    }

    public Task<Comment> getCommentById(String lotNumber, String commentId) {
        return dbRefCo.child(lotNumber).child(commentId).get().continueWith(snapshot -> {
            if (snapshot.getResult() != null) {
                return snapshot.getResult().getValue(Comment.class);
            }
            return null;
        });
    }

//    public Task<DataSnapshot> getCommentsByLotNumber(String lotNumber) {
//        return dbRefCo.child(lotNumber).get();
//    }

    public Task<Void> deleteCommentById(String lotNumber, String commentId) {
        return dbRefCo.child(lotNumber).child(commentId).removeValue().addOnSuccessListener(snapshot -> {
            Log.d("delete from firebase", "successfully deleted comment with id: " + commentId);
        }).addOnFailureListener(e -> {
            Log.e("firebase error", "error from deleting comment with id: " + commentId);
        });
    }

    public Task<Void> deleteExpandedViewByLotNumber(String lotNumber) {
        return dbRefEx.child(lotNumber).removeValue().addOnSuccessListener(snapshot -> {
            Log.d("delete from firebase", "successfully deleted expanded view with id: " + lotNumber);
        }).addOnFailureListener(e -> {
            Log.e("firebase error", "error from deleting expanded view with id: " + lotNumber);
        });
    }
}
