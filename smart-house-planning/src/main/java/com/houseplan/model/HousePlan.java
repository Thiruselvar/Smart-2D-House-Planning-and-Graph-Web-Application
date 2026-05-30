package com.houseplan.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "house_plans")
public class HousePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long planId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "plan_name", length = 100)
    private String planName;

    @Column(name = "plot_width", nullable = false)
    private double plotWidth;

    @Column(name = "plot_height", nullable = false)
    private double plotHeight;

    private int floors = 1;

    private int bedrooms;

    private int bathrooms;

    private boolean kitchen = true;

    @Column(name = "hall_required")
    private boolean hallRequired = true;

    private boolean parking;

    private boolean staircase;

    private boolean balcony;

    @Column(name = "preferred_room_dim", length = 50)
    private String preferredRoomDim;

    @Column(name = "generated_plan_data", columnDefinition = "TEXT")
    private String generatedPlanData;

    @Column(name = "total_built_up_area")
    private Double totalBuiltUpArea;

    @Column(name = "free_area")
    private Double freeArea;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "housePlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> rooms = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public double getPlotWidth() {
        return plotWidth;
    }

    public void setPlotWidth(double plotWidth) {
        this.plotWidth = plotWidth;
    }

    public double getPlotHeight() {
        return plotHeight;
    }

    public void setPlotHeight(double plotHeight) {
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

    public Double getTotalBuiltUpArea() {
        return totalBuiltUpArea;
    }

    public void setTotalBuiltUpArea(Double totalBuiltUpArea) {
        this.totalBuiltUpArea = totalBuiltUpArea;
    }

    public Double getFreeArea() {
        return freeArea;
    }

    public void setFreeArea(Double freeArea) {
        this.freeArea = freeArea;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public double getPlotArea() {
        return plotWidth * plotHeight;
    }
}
