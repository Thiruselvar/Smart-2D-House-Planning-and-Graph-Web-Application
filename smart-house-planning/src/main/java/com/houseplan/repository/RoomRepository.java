package com.houseplan.repository;

import com.houseplan.model.Room;
import com.houseplan.model.HousePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHousePlan(HousePlan housePlan);
    void deleteByHousePlan(HousePlan housePlan);
}
