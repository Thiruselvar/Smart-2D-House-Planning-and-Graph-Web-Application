package com.houseplan.model.room;

public class ParkingRoom extends AbstractRoom {

    public ParkingRoom(double width, double height) {
        super("Parking", width, height);
        setHasWindow(false);
    }

    @Override
    public double getMinimumArea() {
        return 150;
    }
}
