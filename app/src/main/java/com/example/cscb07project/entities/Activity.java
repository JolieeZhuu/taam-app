package com.example.cscb07project.entities;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("activityId", activityId);
        result.put("userId", userId);
        result.put("actionType", actionType);
        result.put("description", description);

        return result;
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
