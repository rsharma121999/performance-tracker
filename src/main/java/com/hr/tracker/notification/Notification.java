package com.hr.tracker.notification;

public interface Notification {
    String getType();
    void send();
}
