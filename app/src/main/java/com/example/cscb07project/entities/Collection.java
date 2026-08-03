package com.example.cscb07project.entities;

import java.util.HashMap;
import java.util.Map;

public class Collection {
    private String userId;
    private String collectionId;
    private String name;
    private Map<String, Boolean> artifacts; // identifies which artifact ids belong to collection

    public Collection() {}

    public Collection(String userId, String name) {
        this.userId = userId;
        this.collectionId = null;
        this.name = name;
        this.artifacts = null;
    }

    public Collection(String userId, String name, Map<String, Boolean> artifacts) {
        this.userId = userId;
        this.collectionId = null;
        this.name = name;
        this.artifacts = artifacts;
    }
    public Collection(String userId, String collectionId, String name, Map<String, Boolean> artifacts) {
        this.userId = userId;
        this.collectionId = collectionId;
        this.name = name;
        this.artifacts = artifacts;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("collectionId", collectionId);
        result.put("name", name);
        result.put("artifacts", artifacts);

        return result;
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
