package com.hr.tracker.dto.request;

import jakarta.validation.constraints.*;


public record CreateReviewRequest(

    @NotNull(message = "Employee ID is required")
    Long employeeId,

    @NotNull(message = "Cycle ID is required")
    Long cycleId,

    Long reviewerId,

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    Integer rating,

    @Size(max = 4000, message = "Reviewer notes must not exceed 4000 characters")
    String reviewerNotes
) {}
