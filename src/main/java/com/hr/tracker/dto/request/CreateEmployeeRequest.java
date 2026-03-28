package com.hr.tracker.dto.request;

import com.hr.tracker.enums.Department;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;


public record CreateEmployeeRequest(

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 200, message = "Name must be between 2 and 200 characters")
    String name,

    @NotNull(message = "Department is required")
    Department department,

    @NotBlank(message = "Role is required")
    @Size(min = 2, max = 150, message = "Role must be between 2 and 150 characters")
    String role,

    @NotNull(message = "Joining date is required")
    @PastOrPresent(message = "Joining date cannot be in the future")
    LocalDate joiningDate
) {}
