package com.houseplan.utils;

import com.houseplan.model.room.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses plan JSON from canvas into AbstractRoom instances.
 */
public final class PlanJsonParser {

    private static final Pattern ROOM_PATTERN = Pattern.compile(
            "\\{\"label\":\"([^\"]+)\",\"x\":([\\d.]+),\"y\":([\\d.]+),\"w\":([\\d.]+),\"h\":([\\d.]+),\"door\":(true|false),\"window\":(true|false)\\}"
    );

    private PlanJsonParser() {}

    public static List<AbstractRoom> parseRooms(String json) {
        List<AbstractRoom> rooms = new ArrayList<>();
        if (json == null || json.isBlank()) {
            return rooms;
        }
        Matcher m = ROOM_PATTERN.matcher(json);
        int bed = 0, bath = 0;
        while (m.find()) {
            String label = m.group(1);
            double x = Double.parseDouble(m.group(2));
            double y = Double.parseDouble(m.group(3));
            double w = Double.parseDouble(m.group(4));
            double h = Double.parseDouble(m.group(5));
            boolean door = Boolean.parseBoolean(m.group(6));
            boolean window = Boolean.parseBoolean(m.group(7));

            if (label.startsWith("Bedroom")) bed++;
            if (label.startsWith("Bathroom")) bath++;
            AbstractRoom room = createRoom(label, w, h, bed, bath);
            room.setPosition(x, y);
            room.setHasDoor(door);
            room.setHasWindow(window);
            rooms.add(room);
        }
        return rooms;
    }

    private static AbstractRoom createRoom(String label, double w, double h, int bed, int bath) {
        if (label.startsWith("Bedroom")) return new BedroomRoom(bed, w, h);
        if (label.startsWith("Bathroom")) return new BathroomRoom(bath, w, h);
        if (label.startsWith("Kitchen")) return new KitchenRoom(w, h);
        if (label.startsWith("Hall")) return new HallRoom(w, h);
        if (label.startsWith("Parking")) return new ParkingRoom(w, h);
        if (label.startsWith("Staircase")) return new StaircaseRoom(w, h);
        if (label.startsWith("Balcony")) return new BalconyRoom(w, h);
        return new HallRoom(w, h);
    }
}
