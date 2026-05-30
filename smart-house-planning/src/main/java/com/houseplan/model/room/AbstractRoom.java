package com.houseplan.model.room;

/**
 * Base class for all room types — encapsulates position and dimensions.
 */
public abstract class AbstractRoom implements Renderable {

    private final String label;
    private double x;
    private double y;
    private double width;
    private double height;
    private boolean hasDoor = true;
    private boolean hasWindow = true;

    protected AbstractRoom(String label, double width, double height) {
        this.label = label;
        this.width = width;
        this.height = height;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setDimensions(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public double getArea() {
        return width * height;
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

    /** Minimum area required for this room type. */
    public abstract double getMinimumArea();

    @Override
    public String toJson() {
        return String.format(
                "{\"label\":\"%s\",\"x\":%.2f,\"y\":%.2f,\"w\":%.2f,\"h\":%.2f,\"door\":%b,\"window\":%b}",
                label, x, y, width, height, hasDoor, hasWindow);
    }
}
