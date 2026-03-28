package com.hr.tracker.dto.response;

import com.hr.tracker.enums.Department;
import java.time.LocalDate;


public record EmployeeResponse(
    Long id,
    String name,
    Department department,
    String role,
    LocalDate joiningDate,
    boolean active
) {}
