package com.example.cscb07project.entities;

import java.time.LocalDateTime;

public class Activity {
    private String activityId;
    private String userId;
    private String actionType;
    private String description;
    private LocalDateTime createdAt;

    public Activity() {}

    public Activity(String activityId, String userId, String actionType, String description) {
        this.activityId = activityId;
        this.userId = userId;
        this.actionType = actionType;
        this.description = description;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
