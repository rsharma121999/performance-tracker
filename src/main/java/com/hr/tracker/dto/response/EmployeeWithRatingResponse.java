package com.hr.tracker.dto.response;

import com.hr.tracker.enums.Department;
import java.time.LocalDate;


public record EmployeeWithRatingResponse(
    Long id,
    String name,
    Department department,
    String role,
    LocalDate joiningDate,
    double averageRating
) {}
