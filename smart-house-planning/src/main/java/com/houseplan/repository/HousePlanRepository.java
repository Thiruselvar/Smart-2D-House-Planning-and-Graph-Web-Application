package com.houseplan.repository;

import com.houseplan.model.HousePlan;
import com.houseplan.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HousePlanRepository extends JpaRepository<HousePlan, Long> {
    List<HousePlan> findByUserOrderByUpdatedAtDesc(User user);
    long countByUser(User user);
    List<HousePlan> findAllByOrderByCreatedAtDesc();
}
