package com.houseplan.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private HousePlan housePlan;

    @Column(name = "room_name", nullable = false, length = 50)
    private String roomName;

    private double width;

    private double height;

    @Column(name = "x_position")
    private double xPosition;

    @Column(name = "y_position")
    private double yPosition;

    @Column(name = "has_door")
    private boolean hasDoor = true;

    @Column(name = "has_window")
    private boolean hasWindow = true;

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public HousePlan getHousePlan() {
        return housePlan;
    }

    public void setHousePlan(HousePlan housePlan) {
        this.housePlan = housePlan;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getXPosition() {
        return xPosition;
    }

    public void setXPosition(double xPosition) {
        this.xPosition = xPosition;
    }

    public double getYPosition() {
        return yPosition;
    }

    public void setYPosition(double yPosition) {
        this.yPosition = yPosition;
    }

    public boolean isHasDoor() {
        return hasDoor;
    }

    public void setHasDoor(boolean hasDoor) {
        this.hasDoor = hasDoor;
    }

    public boolean isHasWindow() {
        return hasWindow;
    }

    public void setHasWindow(boolean hasWindow) {
        this.hasWindow = hasWindow;
    }

    public double getArea() {
        return width * height;
    }
}
