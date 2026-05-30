package com.houseplan.model.room;

public class HallRoom extends AbstractRoom {

    public HallRoom(double width, double height) {
        super("Hall / Living", width, height);
    }

    @Override
    public double getMinimumArea() {
        return 120;
    }
}
