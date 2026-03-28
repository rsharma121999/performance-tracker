package com.hr.tracker.factory;

import com.hr.tracker.enums.Rating;
import com.hr.tracker.event.EmployeeCreatedEvent;
import com.hr.tracker.event.ReviewSubmittedEvent;
import com.hr.tracker.notification.*;
import org.springframework.stereotype.Component;


@Component
public class NotificationFactory {


    public Notification createFromReviewEvent(ReviewSubmittedEvent event) {
        if (event.getRating().getScore() <= Rating.NEEDS_IMPROVEMENT.getScore()) {
            return new SlackNotification(
                "#hr-alerts",
                String.format("Low rating alert: Employee %d received %s (%d) in cycle %d",
                    event.getEmployeeId(), event.getRating().getLabel(),
                    event.getRating().getScore(), event.getCycleId())
            );
        }
        return new InAppNotification(
            event.getEmployeeId(),
            "New Performance Review",
            String.format("A review (%s) has been submitted for cycle %d.",
                event.getRating().getLabel(), event.getCycleId())
        );
    }


    public Notification createFromEmployeeEvent(EmployeeCreatedEvent event) {
        return new EmailNotification(
            event.getEmployeeName().toLowerCase().replace(' ', '.') + "@company.com",
            "Welcome to the team!",
            String.format("Hi %s, welcome to %s! Your performance profile is ready.",
                event.getEmployeeName(), event.getDepartment())
        );
    }
}
