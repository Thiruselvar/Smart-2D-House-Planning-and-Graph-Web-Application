package com.houseplan.model.room;

import com.houseplan.model.dto.PlanRequestDto;
import java.util.List;

/**
 * Interface for room allocation strategies.
 */
public interface RoomAllocator {
    List<AbstractRoom> allocate(PlanRequestDto request);
}
