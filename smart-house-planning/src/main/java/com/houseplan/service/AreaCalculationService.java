package com.houseplan.service;

import com.houseplan.model.HousePlan;
import com.houseplan.model.Room;
import com.houseplan.model.dto.AreaSummaryDto;
import com.houseplan.model.room.AbstractRoom;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AreaCalculationService {

    public AreaSummaryDto calculate(HousePlan plan) {
        double plotArea = plan.getPlotArea();
        double builtUp = 0;
        List<Map<String, Object>> roomAreas = new ArrayList<>();

        for (Room room : plan.getRooms()) {
            double area = room.getArea();
            builtUp += area;
            Map<String, Object> entry = new HashMap<>();
            entry.put("name", room.getRoomName());
            entry.put("width", room.getWidth());
            entry.put("height", room.getHeight());
            entry.put("area", Math.round(area * 100) / 100.0);
            roomAreas.add(entry);
        }

        double free = Math.max(0, plotArea - builtUp);
        return new AreaSummaryDto(plotArea, builtUp, free, roomAreas);
    }

    public AreaSummaryDto calculateFromAbstractRooms(double plotW, double plotH, List<AbstractRoom> rooms) {
        double plotArea = plotW * plotH;
        double builtUp = 0;
        List<Map<String, Object>> roomAreas = new ArrayList<>();

        for (AbstractRoom room : rooms) {
            builtUp += room.getArea();
            Map<String, Object> entry = new HashMap<>();
            entry.put("name", room.getLabel());
            entry.put("width", room.getWidth());
            entry.put("height", room.getHeight());
            entry.put("area", Math.round(room.getArea() * 100) / 100.0);
            roomAreas.add(entry);
        }

        return new AreaSummaryDto(plotArea, builtUp, Math.max(0, plotArea - builtUp), roomAreas);
    }
}
