package com.houseplan.model.room;

public class BalconyRoom extends AbstractRoom {

    public BalconyRoom(double width, double height) {
        super("Balcony", width, height);
    }

    @Override
    public double getMinimumArea() {
        return 30;
    }
}
