package com.hr.tracker.dto.response;

import java.time.Instant;


public record ReviewResponse(
    Long id,
    Long employeeId,
    String employeeName,
    Long reviewerId,
    String reviewerName,
    CycleInfo cycle,
    int rating,
    String ratingLabel,
    String reviewerNotes,
    Instant submittedAt
) {


    public record CycleInfo(
        Long id,
        String name,
        java.time.LocalDate startDate,
        java.time.LocalDate endDate
    ) {}
}
