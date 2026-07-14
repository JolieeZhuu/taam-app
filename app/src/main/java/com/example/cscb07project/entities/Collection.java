package com.example.cscb07project.entities;

import java.time.LocalDateTime;
import java.util.Map;

public class Collection {
    private String userId;
    private String collectionId;
    private String name;
    private Map<String, Boolean> artifacts; // identifies which artifact ids belong to collection
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Collection() {}

    public Collection(String userId, String collectionId, String name, Map<String, Boolean> artifacts) {
        this.userId = userId;
        this.collectionId = collectionId;
        this.name = name;
        this.artifacts = artifacts;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Boolean> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(Map<String, Boolean> artifacts) {
        this.artifacts = artifacts;
    }
}
