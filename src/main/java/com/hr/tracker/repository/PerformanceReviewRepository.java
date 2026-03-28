package com.hr.tracker.repository;

import com.hr.tracker.entity.PerformanceReview;
import com.hr.tracker.repository.projection.TopPerformerProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {


    @Query("""
        SELECT r FROM PerformanceReview r
        JOIN FETCH r.cycle
        JOIN FETCH r.employee
        LEFT JOIN FETCH r.reviewer
        WHERE r.employee.id = :employeeId
        ORDER BY r.submittedAt DESC
    """)
    List<PerformanceReview> findByEmployeeIdWithDetails(@Param("employeeId") Long employeeId);


    @Query(value = "SELECT AVG(CAST(rating AS DOUBLE)) FROM performance_review WHERE cycle_id = :cycleId",
           nativeQuery = true)
    Double findAverageRatingByCycleId(@Param("cycleId") Long cycleId);

    long countByCycleId(Long cycleId);


    @Query(value = """
        SELECT e.id AS employeeId, e.name AS employeeName, AVG(CAST(r.rating AS DOUBLE)) AS avgRating
        FROM performance_review r
        JOIN employee e ON e.id = r.employee_id
        WHERE r.cycle_id = :cycleId
        GROUP BY e.id, e.name
        ORDER BY avgRating DESC
    """, nativeQuery = true)
    List<TopPerformerProjection> findTopPerformersByCycleId(@Param("cycleId") Long cycleId);


    @Query(value = "SELECT rating FROM performance_review WHERE employee_id = :employeeId ORDER BY submitted_at ASC",
           nativeQuery = true)
    List<Integer> findRatingsByEmployeeId(@Param("employeeId") Long employeeId);


    @Query(value = """
        SELECT e.id AS employeeId, e.name AS employeeName, r.rating AS rating
        FROM performance_review r
        JOIN employee e ON e.id = r.employee_id
        WHERE r.cycle_id = :cycleId
        ORDER BY e.id, r.submitted_at ASC
    """, nativeQuery = true)
    List<CycleRatingTuple> findAllRatingsForCycle(@Param("cycleId") Long cycleId);


    interface CycleRatingTuple {
        Long getEmployeeId();
        String getEmployeeName();
        Integer getRating();
    }
}
