package com.houseplan.service;

import com.houseplan.model.dto.PlanRequestDto;
import com.houseplan.model.room.AbstractRoom;
import com.houseplan.utils.PlanLayoutGenerator;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PlanGeneratorService {

    private final PlanLayoutGenerator layoutGenerator = new PlanLayoutGenerator();
    private final AreaCalculationService areaCalculationService;

    public PlanGeneratorService(AreaCalculationService areaCalculationService) {
        this.areaCalculationService = areaCalculationService;
    }

    public GenerationResult generate(PlanRequestDto request) {
        List<AbstractRoom> rooms = layoutGenerator.allocate(request);
        String json = PlanLayoutGenerator.roomsToJson(rooms, request.getPlotWidth(), request.getPlotHeight());
        var summary = areaCalculationService.calculateFromAbstractRooms(
                request.getPlotWidth(), request.getPlotHeight(), rooms);
        return new GenerationResult(rooms, json, summary);
    }

    public record GenerationResult(
            List<AbstractRoom> rooms,
            String planJson,
            com.houseplan.model.dto.AreaSummaryDto areaSummary) {}
}
