package com.hr.tracker.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
public class AuditLogListener {

    private static final Logger log = LoggerFactory.getLogger(AuditLogListener.class);

    @EventListener
    public void onReviewSubmitted(ReviewSubmittedEvent event) {
        log.info("AUDIT | Review submitted: reviewId={}, employeeId={}, cycleId={}, rating={}",
            event.getReviewId(), event.getEmployeeId(),
            event.getCycleId(), event.getRating().getLabel());
    }

    @EventListener
    public void onEmployeeCreated(EmployeeCreatedEvent event) {
        log.info("AUDIT | Employee created: employeeId={}, name={}, department={}",
            event.getEmployeeId(), event.getEmployeeName(), event.getDepartment());
    }
}
