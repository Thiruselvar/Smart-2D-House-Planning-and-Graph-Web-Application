package com.houseplan.model.room;

public class BedroomRoom extends AbstractRoom {

    private final int index;

    public BedroomRoom(int index, double width, double height) {
        super("Bedroom " + index, width, height);
        this.index = index;
    }

    @Override
    public double getMinimumArea() {
        return 100;
    }

    public int getIndex() {
        return index;
    }
}
