package com.hr.tracker.repository.projection;

/**
 * JPA projection for top performer queries.
 */
public interface TopPerformerProjection {
    Long getEmployeeId();
    String getEmployeeName();
    Double getAvgRating();
}
