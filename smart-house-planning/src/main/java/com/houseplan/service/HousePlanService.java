package com.houseplan.service;

import com.houseplan.model.HousePlan;
import com.houseplan.model.Room;
import com.houseplan.model.User;
import com.houseplan.model.dto.PlanRequestDto;
import com.houseplan.model.room.AbstractRoom;
import com.houseplan.repository.HousePlanRepository;
import com.houseplan.repository.RoomRepository;
import com.houseplan.utils.PlanJsonParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class HousePlanService {

    private final HousePlanRepository planRepository;
    private final RoomRepository roomRepository;
    private final PlanGeneratorService planGeneratorService;
    private final AreaCalculationService areaCalculationService;

    public HousePlanService(HousePlanRepository planRepository,
                            RoomRepository roomRepository,
                            PlanGeneratorService planGeneratorService,
                            AreaCalculationService areaCalculationService) {
        this.planRepository = planRepository;
        this.roomRepository = roomRepository;
        this.planGeneratorService = planGeneratorService;
        this.areaCalculationService = areaCalculationService;
    }

    public List<HousePlan> findByUser(User user) {
        return planRepository.findByUserOrderByUpdatedAtDesc(user);
    }

    public Optional<HousePlan> findById(Long id) {
        return planRepository.findById(id);
    }

    @Transactional
    public HousePlan createOrUpdate(User user, PlanRequestDto request) {
        HousePlan plan = request.getPlanId() != null
                ? planRepository.findById(request.getPlanId()).orElse(new HousePlan())
                : new HousePlan();

        if (plan.getPlanId() != null && !plan.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalStateException("Not authorized to edit this plan.");
        }

        applyRequest(plan, request);
        plan.setUser(user);

        List<AbstractRoom> rooms;
        String planJson;
        com.houseplan.model.dto.AreaSummaryDto areaSummary;

        if (request.getGeneratedPlanData() != null && !request.getGeneratedPlanData().isBlank()) {
            rooms = PlanJsonParser.parseRooms(request.getGeneratedPlanData());
            planJson = request.getGeneratedPlanData();
            if (!rooms.isEmpty()) {
                areaSummary = areaCalculationService.calculateFromAbstractRooms(
                        request.getPlotWidth(), request.getPlotHeight(), rooms);
            } else {
                var result = planGeneratorService.generate(request);
                rooms = result.rooms();
                planJson = result.planJson();
                areaSummary = result.areaSummary();
            }
        } else {
            var result = planGeneratorService.generate(request);
            rooms = result.rooms();
            planJson = result.planJson();
            areaSummary = result.areaSummary();
        }

        plan.setGeneratedPlanData(planJson);
        plan.setTotalBuiltUpArea(areaSummary.getTotalBuiltUpArea());
        plan.setFreeArea(areaSummary.getFreeArea());

        plan.getRooms().clear();
        for (AbstractRoom ar : rooms) {
            Room room = new Room();
            room.setHousePlan(plan);
            room.setRoomName(ar.getLabel());
            room.setWidth(ar.getWidth());
            room.setHeight(ar.getHeight());
            room.setXPosition(ar.getX());
            room.setYPosition(ar.getY());
            room.setHasDoor(ar.isHasDoor());
            room.setHasWindow(ar.isHasWindow());
            plan.getRooms().add(room);
        }

        return planRepository.save(plan);
    }

    @Transactional
    public void updateRoomsFromJson(HousePlan plan, String roomsJson, double builtUp, double free) {
        plan.setGeneratedPlanData(roomsJson);
        plan.setTotalBuiltUpArea(builtUp);
        plan.setFreeArea(free);
        planRepository.save(plan);
    }

    @Transactional
    public void delete(HousePlan plan) {
        planRepository.delete(plan);
    }

    private void applyRequest(HousePlan plan, PlanRequestDto request) {
        plan.setPlanName(request.getPlanName() != null && !request.getPlanName().isBlank()
                ? request.getPlanName() : "House Plan");
        plan.setPlotWidth(request.getPlotWidth());
        plan.setPlotHeight(request.getPlotHeight());
        plan.setFloors(request.getFloors());
        plan.setBedrooms(request.getBedrooms());
        plan.setBathrooms(request.getBathrooms());
        plan.setKitchen(request.isKitchen());
        plan.setHallRequired(request.isHallRequired());
        plan.setParking(request.isParking());
        plan.setStaircase(request.isStaircase());
        plan.setBalcony(request.isBalcony());
        plan.setPreferredRoomDim(request.getPreferredRoomDim());
    }
}
