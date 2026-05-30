package com.houseplan.utils;

import com.houseplan.model.dto.PlanRequestDto;
import com.houseplan.model.room.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Grid-based room allocator — places rooms without overlap with spacing.
 */
public class PlanLayoutGenerator implements RoomAllocator {

    private static final double WALL_GAP = 0.5;
    private static final double MARGIN = 1.0;

    @Override
    public List<AbstractRoom> allocate(PlanRequestDto request) {
        validatePlot(request);

        List<AbstractRoom> rooms = buildRoomList(request);
        double scale = resolveScale(request, rooms);
        applyPreferredDimensions(request, rooms, scale);
        placeRoomsGrid(request.getPlotWidth(), request.getPlotHeight(), rooms);
        return rooms;
    }

    private void validatePlot(PlanRequestDto request) {
        if (request.getPlotWidth() == null || request.getPlotHeight() == null) {
            throw new IllegalArgumentException("Plot dimensions are required.");
        }
        if (request.getPlotWidth() < 10 || request.getPlotHeight() < 10) {
            throw new IllegalArgumentException("Plot must be at least 10m x 10m.");
        }
    }

    private List<AbstractRoom> buildRoomList(PlanRequestDto request) {
        List<AbstractRoom> rooms = new ArrayList<>();
        double pref = parsePreferredSize(request.getPreferredRoomDim());

        if (request.isHallRequired()) {
            rooms.add(new HallRoom(pref * 1.4, pref));
        }
        if (request.isKitchen()) {
            rooms.add(new KitchenRoom(pref, pref * 0.8));
        }
        for (int i = 1; i <= request.getBedrooms(); i++) {
            rooms.add(new BedroomRoom(i, pref, pref * 0.9));
        }
        for (int i = 1; i <= request.getBathrooms(); i++) {
            rooms.add(new BathroomRoom(i, pref * 0.5, pref * 0.5));
        }
        if (request.isParking()) {
            rooms.add(new ParkingRoom(pref * 1.8, pref * 1.2));
        }
        if (request.isStaircase()) {
            rooms.add(new StaircaseRoom(pref * 0.6, pref * 0.8));
        }
        if (request.isBalcony()) {
            rooms.add(new BalconyRoom(pref * 0.8, pref * 0.4));
        }
        if (rooms.isEmpty()) {
            rooms.add(new HallRoom(4, 4));
        }
        return rooms;
    }

    private double parsePreferredSize(String pref) {
        if (pref == null || pref.isBlank()) {
            return 4.0;
        }
        try {
            return Math.max(2.5, Double.parseDouble(pref.trim()));
        } catch (NumberFormatException e) {
            return 4.0;
        }
    }

    private double resolveScale(PlanRequestDto request, List<AbstractRoom> rooms) {
        double totalMin = rooms.stream().mapToDouble(AbstractRoom::getMinimumArea).sum();
        double plotArea = request.getPlotArea();
        if (totalMin > plotArea * 0.95) {
            return Math.sqrt((plotArea * 0.85) / totalMin);
        }
        return 1.0;
    }

    private void applyPreferredDimensions(PlanRequestDto request, List<AbstractRoom> rooms, double scale) {
        if (scale < 1.0) {
            for (AbstractRoom room : rooms) {
                room.setDimensions(room.getWidth() * scale, room.getHeight() * scale);
            }
        }
    }

    /**
     * Row-packing placement with collision avoidance.
     */
    private void placeRoomsGrid(double plotW, double plotH, List<AbstractRoom> rooms) {
        double cursorX = MARGIN;
        double cursorY = MARGIN;
        double rowHeight = 0;

        for (AbstractRoom room : rooms) {
            double w = room.getWidth() + WALL_GAP;
            double h = room.getHeight() + WALL_GAP;

            if (cursorX + room.getWidth() + MARGIN > plotW) {
                cursorX = MARGIN;
                cursorY += rowHeight;
                rowHeight = 0;
            }
            if (cursorY + room.getHeight() + MARGIN > plotH) {
                shrinkToFit(room, plotW - MARGIN * 2, plotH - MARGIN * 2);
            }

            room.setPosition(cursorX, cursorY);
            cursorX += w;
            rowHeight = Math.max(rowHeight, h);
        }
    }

    private void shrinkToFit(AbstractRoom room, double maxW, double maxH) {
        if (room.getWidth() > maxW) {
            double ratio = maxW / room.getWidth();
            room.setDimensions(maxW, room.getHeight() * ratio);
        }
        if (room.getHeight() > maxH) {
            double ratio = maxH / room.getHeight();
            room.setDimensions(room.getWidth() * ratio, maxH);
        }
    }

    public static String roomsToJson(List<AbstractRoom> rooms, double plotW, double plotH) {
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        for (AbstractRoom room : rooms) {
            joiner.add(room.toJson());
        }
        return String.format("{\"plotWidth\":%.2f,\"plotHeight\":%.2f,\"rooms\":%s}",
                plotW, plotH, joiner);
    }
}
