package com.hr.tracker.repository.projection;

/**
 * JPA projection for the employee filter query.
 * Replaces unsafe Object[] casts with a type-safe interface.
 */
public interface EmployeeRatingProjection {
    Long getId();
    String getName();
    String getDepartment();
    String getRole();
    java.time.LocalDate getJoiningDate();
    Double getAvgRating();
}
