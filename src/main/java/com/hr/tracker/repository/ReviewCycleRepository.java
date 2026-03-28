package com.hr.tracker.repository;

import com.hr.tracker.entity.ReviewCycle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewCycleRepository extends JpaRepository<ReviewCycle, Long> {

    boolean existsByName(String name);
}
