package com.example.cscb07project.entities;

public class ExpandedView {
    private String lotNumber; // note to self: might be very redundant
    private Integer likeNumber;

    public ExpandedView() {}
    public ExpandedView(String lotNumber, Integer likeNumber) {
        this.lotNumber = lotNumber;
        this.likeNumber = likeNumber;
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
