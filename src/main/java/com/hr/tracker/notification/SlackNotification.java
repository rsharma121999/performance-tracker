package com.hr.tracker.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlackNotification implements Notification {

    private static final Logger log = LoggerFactory.getLogger(SlackNotification.class);
    private final String channel;
    private final String message;

    public SlackNotification(String channel, String message) {
        this.channel = channel;
        this.message = message;
    }

    @Override public String getType() { return "SLACK"; }

    @Override
    public void send() {
        log.info("Posting to Slack channel={}, message='{}'", channel, message);
    }
}
