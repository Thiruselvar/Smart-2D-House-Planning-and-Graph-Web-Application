package com.houseplan.model.room;

public class StaircaseRoom extends AbstractRoom {

    public StaircaseRoom(double width, double height) {
        super("Staircase", width, height);
        setHasWindow(false);
    }

    @Override
    public double getMinimumArea() {
        return 40;
    }
}
