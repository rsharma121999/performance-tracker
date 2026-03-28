package com.hr.tracker.event;

import com.hr.tracker.factory.NotificationFactory;
import com.hr.tracker.notification.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);
    private final NotificationFactory notificationFactory;

    public NotificationListener(NotificationFactory notificationFactory) {
        this.notificationFactory = notificationFactory;
    }

    @Async
    @EventListener
    public void onReviewSubmitted(ReviewSubmittedEvent event) {
        Notification notification = notificationFactory.createFromReviewEvent(event);
        notification.send();
        log.info("NOTIFY | Dispatched {} for reviewId={}", notification.getType(), event.getReviewId());
    }

    @Async
    @EventListener
    public void onEmployeeCreated(EmployeeCreatedEvent event) {
        Notification notification = notificationFactory.createFromEmployeeEvent(event);
        notification.send();
        log.info("NOTIFY | Dispatched {} for employeeId={}", notification.getType(), event.getEmployeeId());
    }
}
