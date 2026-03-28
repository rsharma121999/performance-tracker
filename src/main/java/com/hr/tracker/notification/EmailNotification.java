package com.hr.tracker.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailNotification implements Notification {

    private static final Logger log = LoggerFactory.getLogger(EmailNotification.class);
    private final String recipient;
    private final String subject;
    private final String body;

    public EmailNotification(String recipient, String subject, String body) {
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
    }

    @Override public String getType() { return "EMAIL"; }

    @Override
    public void send() {
        log.info("Sending email to={}, subject='{}'", recipient, subject);
    }
}
