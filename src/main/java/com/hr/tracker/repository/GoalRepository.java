package com.hr.tracker.repository;

import com.hr.tracker.entity.Goal;
import com.hr.tracker.repository.projection.GoalStatusCountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {


    @Query("""
        SELECT g.status AS status, COUNT(g) AS count
        FROM Goal g
        WHERE g.cycle.id = :cycleId
        GROUP BY g.status
    """)
    List<GoalStatusCountProjection> countByStatusForCycle(@Param("cycleId") Long cycleId);
}
