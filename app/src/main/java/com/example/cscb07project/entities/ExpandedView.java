package com.example.cscb07project.entities;

import java.util.HashMap;
import java.util.Map;

public class ExpandedView {
    private String expandedViewId;
    private String lotNumber; // note to self: might be very redundant
    private Integer likeNumber;
    private Map<String, Boolean> likes;


    public ExpandedView() {}
//    public ExpandedView(String lotNumber, Integer likeNumber) {
//        this.lotNumber = lotNumber;
//        this.likeNumber = likeNumber;
//        this.usersThatLiked = null;
//    }

    public ExpandedView(String lotNumber) {
        this.expandedViewId = null;
        this.lotNumber = lotNumber;
        this.likeNumber = 0;
        this.likes = null;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("expandedViewId", expandedViewId);
        result.put("lotNumber", lotNumber);
        result.put("likeNumber", likeNumber);
        result.put("likes", likes);

        return result;
    }

    @Override
    public String toString() {
        if (likes == null) {
            return "{ " + expandedViewId + ", " + lotNumber + ", " + likeNumber + " }";
        }
        return "{ " + expandedViewId + ", " + lotNumber + ", " + likeNumber + ", " + likes.toString() + " }";
    }

    public String getExpandedViewId() {
        return expandedViewId;
    }

    public void setExpandedViewId(String expandedViewId) {
        this.expandedViewId = expandedViewId;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public Integer getLikeNumber() {
        return likeNumber;
    }

    public void setLikeNumber(Integer likeNumber) {
        this.likeNumber = likeNumber;
    }

    public Map<String, Boolean> getLikes() {
        return likes;
    }

    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes;
    }
}
