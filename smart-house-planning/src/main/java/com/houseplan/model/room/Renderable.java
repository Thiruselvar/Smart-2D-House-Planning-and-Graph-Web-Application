package com.houseplan.model.room;

/**
 * Interface for objects that can be rendered on the 2D canvas.
 */
public interface Renderable {
    String getLabel();
    double getX();
    double getY();
    double getWidth();
    double getHeight();
    double getArea();
    String toJson();
}
