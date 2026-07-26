package com.example.cscb07project.interfaces;

import com.example.cscb07project.entities.Comment;
import com.example.cscb07project.entities.ExpandedView;
import com.google.android.gms.tasks.Task;

import java.util.List;

public interface ExpandedViewInterface {
    Task<Void> addExpandedView(String lotNumber, ExpandedView expandedView);

    Task<Void> addComment(String lotNumber, Comment comment);

    Task<Void> updateComment(Comment comment);

    Task<Void> like(String userId, ExpandedView expandedView);
    Task<Void> unlike(String userId, ExpandedView expandedView);
    boolean isArtifactLikedByUser(String userId, ExpandedView expandedView);

    Task<ExpandedView> getExpandedViewByLotNumber(String lotNumber);

    Task<Comment> getCommentById(String lotNumber, String commentId);

    Task<List<Comment>> getCommentsByLotNumber(String lotNumber);

    Task<Void> deleteCommentById(String lotNumber, String commentId);

    Task<Void> deleteExpandedViewByLotNumber(String lotNumber);
}
