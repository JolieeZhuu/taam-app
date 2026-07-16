package com.example.cscb07project.entities;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Comment {
    private String lotNumber;
    private String commentId;
    private String userId;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Comment() {}
    public Comment(String lotNumber, String commentId, String userId, String comment) {
        this.lotNumber = lotNumber;
        this.commentId = commentId;
        this.userId = userId;
        this.comment = comment;
    }

    public Comment(String lotNumber, String userId, String comment) {
        this.lotNumber = lotNumber;
        this.commentId = null;
        this.userId = userId;
        this.comment = comment;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("lotNumber", lotNumber);
        result.put("commentId", commentId);
        result.put("userId", userId);
        result.put("comment", comment);

        return result;
    }
    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
