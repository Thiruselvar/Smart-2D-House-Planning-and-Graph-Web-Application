package com.houseplan.model.room;

public class KitchenRoom extends AbstractRoom {

    public KitchenRoom(double width, double height) {
        super("Kitchen", width, height);
        setHasWindow(true);
    }

    @Override
    public double getMinimumArea() {
        return 60;
    }
}
