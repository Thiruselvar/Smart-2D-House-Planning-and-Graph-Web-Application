package com.houseplan.model.room;

public class BathroomRoom extends AbstractRoom {

    private final int index;

    public BathroomRoom(int index, double width, double height) {
        super("Bathroom " + index, width, height);
        setHasWindow(false);
        this.index = index;
    }

    @Override
    public double getMinimumArea() {
        return 35;
    }
}
