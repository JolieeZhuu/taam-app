package com.example.cscb07project.entities;

import java.util.HashMap;
import java.util.Map;

public class ExpandedView {
    private String lotNumber; // note to self: might be very redundant
    private Integer likeNumber;

    public ExpandedView() {}
    public ExpandedView(String lotNumber, Integer likeNumber) {
        this.lotNumber = lotNumber;
        this.likeNumber = likeNumber;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("lotNumber", lotNumber);
        result.put("likeNumber", likeNumber);

        return result;
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
}