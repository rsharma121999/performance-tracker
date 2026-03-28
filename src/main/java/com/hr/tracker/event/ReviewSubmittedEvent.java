package com.hr.tracker.event;

import com.hr.tracker.enums.Rating;
import org.springframework.context.ApplicationEvent;


public class ReviewSubmittedEvent extends ApplicationEvent {

    private final Long reviewId;
    private final Long employeeId;
    private final Long cycleId;
    private final Rating rating;

    public ReviewSubmittedEvent(Object source, Long reviewId, Long employeeId,
                                Long cycleId, Rating rating) {
        super(source);
        this.reviewId = reviewId;
        this.employeeId = employeeId;
        this.cycleId = cycleId;
        this.rating = rating;
    }

    public Long getReviewId()    { return reviewId; }
    public Long getEmployeeId()  { return employeeId; }
    public Long getCycleId()     { return cycleId; }
    public Rating getRating()    { return rating; }
}
