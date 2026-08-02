package com.example.cscb07project.entities;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Carousel {

    private String carouselId;
    private String title;
    private Map<String, Boolean> artifacts;

    public Carousel() {}

    public Carousel(String title) {
        this.title = title;
        this.carouselId = null;
        this.artifacts = null;
    }

    public Carousel(String title, Map<String, Boolean> artifacts) {
        this.title = title;
        this.carouselId = null;
        this.artifacts = artifacts;
    }
    public Carousel(String title, String carouselId, Map<String, Boolean> artifacts) {
        this.title = title;
        this.carouselId = carouselId;
        this.artifacts = artifacts;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("carouselId", carouselId);
        result.put("artifacts", artifacts);

        return result;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCarouselId() {
        return carouselId;
    }

    public void setCarouselId(String carouselId) {
        this.carouselId = carouselId;
    }

    public Map<String, Boolean> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(Map<String, Boolean> artifacts) {
        this.artifacts = artifacts;
    }
}
