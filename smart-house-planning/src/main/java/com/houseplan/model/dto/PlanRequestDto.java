package com.houseplan.model.dto;

import jakarta.validation.constraints.*;

public class PlanRequestDto {

    private Long planId;

    private String planName;

    @NotNull @Positive
    private Double plotWidth;

    @NotNull @Positive
    private Double plotHeight;

    @Min(1) @Max(5)
    private int floors = 1;

    @Min(0) @Max(10)
    private int bedrooms;

    @Min(0) @Max(10)
    private int bathrooms;

    private boolean kitchen = true;
    private boolean hallRequired = true;
    private boolean parking;
    private boolean staircase;
    private boolean balcony;
    private String preferredRoomDim;

    /** Canvas JSON from editor — when set, layout is preserved on save. */
    private String generatedPlanData;

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public Double getPlotWidth() {
        return plotWidth;
    }

    public void setPlotWidth(Double plotWidth) {
        this.plotWidth = plotWidth;
    }

    public Double getPlotHeight() {
        return plotHeight;
    }

    public void setPlotHeight(Double plotHeight) {
        this.plotHeight = plotHeight;
    }

    public int getFloors() {
        return floors;
    }

    public void setFloors(int floors) {
        this.floors = floors;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
    }

    public int getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(int bathrooms) {
        this.bathrooms = bathrooms;
    }

    public boolean isKitchen() {
        return kitchen;
    }

    public void setKitchen(boolean kitchen) {
        this.kitchen = kitchen;
    }

    public boolean isHallRequired() {
        return hallRequired;
    }

    public void setHallRequired(boolean hallRequired) {
        this.hallRequired = hallRequired;
    }

    public boolean isParking() {
        return parking;
    }

    public void setParking(boolean parking) {
        this.parking = parking;
    }

    public boolean isStaircase() {
        return staircase;
    }

    public void setStaircase(boolean staircase) {
        this.staircase = staircase;
    }

    public boolean isBalcony() {
        return balcony;
    }

    public void setBalcony(boolean balcony) {
        this.balcony = balcony;
    }

    public String getPreferredRoomDim() {
        return preferredRoomDim;
    }

    public void setPreferredRoomDim(String preferredRoomDim) {
        this.preferredRoomDim = preferredRoomDim;
    }

    public String getGeneratedPlanData() {
        return generatedPlanData;
    }

    public void setGeneratedPlanData(String generatedPlanData) {
        this.generatedPlanData = generatedPlanData;
    }

    public double getPlotArea() {
        return plotWidth * plotHeight;
    }
}
