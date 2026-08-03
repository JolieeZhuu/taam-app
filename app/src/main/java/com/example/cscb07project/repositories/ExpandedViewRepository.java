package com.example.cscb07project.repositories;

import android.util.Log;

import com.example.cscb07project.entities.Comment;
import com.example.cscb07project.entities.ExpandedView;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpandedViewRepository {
    // also for comment handling
    private final DatabaseReference dbRefEx;
    private final DatabaseReference dbRefCo;

    public ExpandedViewRepository(FirebaseDatabase rootRef) {
        this.dbRefEx = rootRef.getReference("expandedViews");
        this.dbRefCo = rootRef.getReference("comments");
    }

    /**
     * Creation functions for expanded view and comments
     */

    public Task<Void> addExpandedView(String lotNumber, ExpandedView expandedView) {
        return dbRefEx.child(lotNumber).setValue(expandedView);
    }

    public Task<Void> addComment(String lotNumber, Comment comment) {
        String commentId = dbRefCo.push().getKey();
        if (commentId == null) throw new IllegalStateException();
        comment.setCommentId(commentId);
        return dbRefCo.child(lotNumber).child(commentId).setValue(comment);
    } // tested


    /**
     * Fetch functions for expanded view and comments
     */

    public Task<ExpandedView> getExpandedViewByLotNumber(String lotNumber) {
        return dbRefEx.child(lotNumber).get().continueWith(snapshot -> {
            if (snapshot.getResult() != null) {
                return snapshot.getResult().getValue(ExpandedView.class);
            }
            return null;
        });
    } // tested

    public Task<Comment> getCommentById(String lotNumber, String commentId) {
        return dbRefCo.child(lotNumber).child(commentId).get().continueWith(snapshot -> {
            if (snapshot.getResult() != null) {
                return snapshot.getResult().getValue(Comment.class);
            }
            return null;
        });
    } // tested

    public Task<List<Comment>> getCommentsByLotNumber(String lotNumber) { // for displaying purposes in fragment
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
    } // tested


    /**
     * Updating functions for expanded view and comments
     */

    public Task<Void> updateComment(Comment comment) {
        return dbRefCo.child(comment.getLotNumber()).child(comment.getCommentId()).updateChildren(comment.toMap());
    } // tested

    public Task<Void> like(String userId, ExpandedView expandedView) {
        if (!isArtifactLikedByUser(userId, expandedView)) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("likeNumber", ServerValue.increment(1)); // increment handles data calculations atomically
            updates.put("likes/" + userId, true); // make a new path in lotNumber/likes/userId

            return dbRefEx.child(expandedView.getLotNumber()).updateChildren(updates).onSuccessTask(task -> {
                expandedView.setLikeNumber(expandedView.getLikeNumber() + 1);
                if (expandedView.getLikes() == null) {
                    expandedView.setLikes(new HashMap<>());
                }
                expandedView.getLikes().put(userId, true); // must update the object passed in too
                return Tasks.forResult(null);
            });
        }
        return Tasks.forResult(null);
    } // tested

    public Task<Void> unlike(String userId, ExpandedView expandedView) {
        if (isArtifactLikedByUser(userId, expandedView)) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("likeNumber", ServerValue.increment(-1)); // increment handles data calculations atomically
            updates.put("likes/" + userId, null); // remove path lotNumber/likes/userId

            return dbRefEx.child(expandedView.getLotNumber()).updateChildren(updates).onSuccessTask(task -> {
                expandedView.setLikeNumber(expandedView.getLikeNumber() - 1);
                if (expandedView.getLikes() != null) {
                    expandedView.getLikes().remove(userId); // must update the object passed in too
                }
                return Tasks.forResult(null);
            });
        }
        return Tasks.forResult(null);
    } // tested

    public boolean isArtifactLikedByUser(String userId, ExpandedView expandedView) {
        return expandedView.getLikes() != null && expandedView.getLikes().containsKey(userId);
    } // tested

    /**
     * Deletion functions for expanded view and comments
     */

    public Task<Void> deleteExpandedViewByLotNumber(String lotNumber) {
        return dbRefEx.child(lotNumber).removeValue().addOnSuccessListener(snapshot -> {
            Log.d("delete from firebase", "successfully deleted expanded view with id: " + lotNumber);
        }).addOnFailureListener(e -> {
            Log.e("firebase error", "error from deleting expanded view with id: " + lotNumber);
        });
    } // tested

    public Task<Void> deleteCommentById(String lotNumber, String commentId) {
        return dbRefCo.child(lotNumber).child(commentId).removeValue().addOnSuccessListener(snapshot -> {
            Log.d("delete from firebase", "successfully deleted comment with id: " + commentId);
        }).addOnFailureListener(e -> {
            Log.e("firebase error", "error from deleting comment with id: " + commentId);
        });
    } // tested
}