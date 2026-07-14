package com.example.cscb07project.entities;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Artifact {
    private String lotNumber;
    private String name;
    private String description;
    private String category;
    private String material;
    private String period;
    private String origin;
    private String dimensions;
    private String conditionReport;
    private String currentLocation;
    private String acquiredMethod;
    private String provenance;
    private String accessionNumber;
    private String notes;
    private String image; // URL from Supabase
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Artifact() {}

    public Artifact(String lotNumber, String name, String description, String category, String material, String period, String origin, String dimensions, String conditionReport, String currentLocation, String acquiredMethod, String provenance, String accessionNumber, String notes, String image) {
        this.lotNumber = lotNumber;
        this.name = name;
        this.description = description;
        this.category = category;
        this.material = material;
        this.period = period;
        this.origin = origin;
        this.dimensions = dimensions;
        this.conditionReport = conditionReport;
        this.currentLocation = currentLocation;
        this.acquiredMethod = acquiredMethod;
        this.provenance = provenance;
        this.accessionNumber = accessionNumber;
        this.notes = notes;
        this.image = image;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public String getConditionReport() {
        return conditionReport;
    }

    public void setConditionReport(String conditionReport) {
        this.conditionReport = conditionReport;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getAcquiredMethod() {
        return acquiredMethod;
    }

    public void setAcquiredMethod(String acquiredMethod) {
        this.acquiredMethod = acquiredMethod;
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public String getAccessionNumber() {
        return accessionNumber;
    }

    public void setAccessionNumber(String accessionNumber) {
        this.accessionNumber = accessionNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
