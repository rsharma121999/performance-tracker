package com.hr.tracker.repository.projection;

import com.hr.tracker.enums.GoalStatus;

/**
 * JPA projection for goal count grouped by status.
 */
public interface GoalStatusCountProjection {
    GoalStatus getStatus();
    Long getCount();
}
