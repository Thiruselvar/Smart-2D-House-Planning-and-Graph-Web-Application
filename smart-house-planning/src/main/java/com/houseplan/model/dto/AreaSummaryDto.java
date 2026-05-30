package com.houseplan.model.dto;

import java.util.List;
import java.util.Map;

public class AreaSummaryDto {

    private double plotArea;
    private double totalBuiltUpArea;
    private double freeArea;
    private List<Map<String, Object>> roomAreas;

    public AreaSummaryDto(double plotArea, double totalBuiltUpArea, double freeArea,
                          List<Map<String, Object>> roomAreas) {
        this.plotArea = plotArea;
        this.totalBuiltUpArea = totalBuiltUpArea;
        this.freeArea = freeArea;
        this.roomAreas = roomAreas;
    }

    public double getPlotArea() {
        return plotArea;
    }

    public double getTotalBuiltUpArea() {
        return totalBuiltUpArea;
    }

    public double getFreeArea() {
        return freeArea;
    }

    public List<Map<String, Object>> getRoomAreas() {
        return roomAreas;
    }
}
