package com.hr.tracker.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InAppNotification implements Notification {

    private static final Logger log = LoggerFactory.getLogger(InAppNotification.class);
    private final Long targetUserId;
    private final String title;
    private final String message;

    public InAppNotification(Long targetUserId, String title, String message) {
        this.targetUserId = targetUserId;
        this.title = title;
        this.message = message;
    }

    @Override public String getType() { return "IN_APP"; }

    @Override
    public void send() {
        log.info("In-app notification for userId={}, title='{}'", targetUserId, title);
    }
}
