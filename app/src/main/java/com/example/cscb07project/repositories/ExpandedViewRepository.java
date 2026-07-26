package com.example.cscb07project.repositories;

import android.util.Log;

import com.example.cscb07project.entities.Comment;
import com.example.cscb07project.entities.ExpandedView;
import com.example.cscb07project.interfaces.ExpandedViewInterface;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExpandedViewRepository implements ExpandedViewInterface {
    // also for Comment repository
    private final DatabaseReference dbRefEx;
    private final DatabaseReference dbRefCo;

    public ExpandedViewRepository(FirebaseDatabase rootRef) {
        this.dbRefEx = rootRef.getReference("expandedViews");
        this.dbRefCo = rootRef.getReference("comments");
    }



    @Override
    public Task<Void> addExpandedView(String lotNumber, ExpandedView expandedView) {
        return dbRefEx.child(lotNumber).setValue(expandedView);
    }

    @Override
    public Task<Void> addComment(String lotNumber, Comment comment) {
        String commentId = dbRefCo.push().getKey();
        if (commentId == null) throw new IllegalStateException();
        comment.setCommentId(commentId);
        return dbRefCo.child(lotNumber).child(commentId).setValue(comment);
    }

    @Override
    public Task<Void> updateComment(Comment comment) {
        return dbRefCo.child(comment.getLotNumber()).child(comment.getCommentId()).updateChildren(comment.toMap());
    }

//    public Task<Void> increaseLike(String lotNumber) { // this may cause a lot of issues
//        return getExpandedViewByLotNumber(lotNumber).continueWithTask(snapshot -> {
//            if (!snapshot.isSuccessful()) throw Objects.requireNonNull(snapshot.getException());
//            return snapshot;
//        }).continueWithTask(snapshot -> {
//            ExpandedView eV = snapshot.getResult();
//            eV.setLikeNumber(eV.getLikeNumber() + 1);
//            return dbRefEx.child(lotNumber).updateChildren(eV.toMap());
//        });
//    }

    @Override
    public Task<ExpandedView> getExpandedViewByLotNumber(String lotNumber) {
        return dbRefEx.child(lotNumber).get().continueWith(snapshot -> {
            if (snapshot.getResult() != null) {
                return snapshot.getResult().getValue(ExpandedView.class);
            }
            return null;
        });
    }

    @Override
    public Task<Comment> getCommentById(String lotNumber, String commentId) {
        return dbRefCo.child(lotNumber).child(commentId).get().continueWith(snapshot -> {
            if (snapshot.getResult() != null) {
                return snapshot.getResult().getValue(Comment.class);
            }
            return null;
        });
    }

    @Override
    public Task<List<Comment>> getCommentsByLotNumber(String lotNumber) {
        return dbRefCo.child(lotNumber).get().continueWith(snapshot -> {
            if (!snapshot.isSuccessful() || snapshot.getResult() == null) return null;
            List<Comment> commentList = new ArrayList<>();
            if (snapshot.getResult().hasChildren()) {
                for (DataSnapshot commentSnapshot : snapshot.getResult().getChildren()) {
                    commentList.add(commentSnapshot.getValue(Comment.class));
                }
                return commentList;
            }
            return null;
        });
    }

    @Override
    public Task<Void> deleteCommentById(String lotNumber, String commentId) {
        return dbRefCo.child(lotNumber).child(commentId).removeValue().addOnSuccessListener(snapshot -> {
            Log.d("delete from firebase", "successfully deleted comment with id: " + commentId);
        }).addOnFailureListener(e -> {
            Log.e("firebase error", "error from deleting comment with id: " + commentId);
        });
    }

    @Override
    public Task<Void> deleteExpandedViewByLotNumber(String lotNumber) {
        return dbRefEx.child(lotNumber).removeValue().addOnSuccessListener(snapshot -> {
            Log.d("delete from firebase", "successfully deleted expanded view with id: " + lotNumber);
        }).addOnFailureListener(e -> {
            Log.e("firebase error", "error from deleting expanded view with id: " + lotNumber);
        });
    }
}
