package com.hr.tracker.repository;

import com.hr.tracker.entity.Employee;
import com.hr.tracker.enums.Department;
import com.hr.tracker.repository.projection.EmployeeRatingProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByDepartmentAndActiveTrue(Department department);

    List<Employee> findByActiveTrue();


    @Query(value = """
        SELECT e.id AS id, e.name AS name, e.department AS department,
               e.role AS role, e.joining_date AS joiningDate,
               AVG(CAST(r.rating AS DOUBLE)) AS avgRating
        FROM performance_review r
        JOIN employee e ON e.id = r.employee_id
        WHERE e.active = true
          AND (:department IS NULL OR e.department = :department)
        GROUP BY e.id, e.name, e.department, e.role, e.joining_date
        HAVING AVG(CAST(r.rating AS DOUBLE)) >= :minRating
        ORDER BY avgRating DESC
    """, nativeQuery = true)
    List<EmployeeRatingProjection> findByDepartmentAndMinAvgRating(
        @Param("department") String department,
        @Param("minRating") double minRating
    );
}
