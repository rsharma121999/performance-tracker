package com.hr.tracker.enums;


public enum ExportFormat {
    JSON("application/json"),
    CSV("text/csv"),
    TEXT("text/plain");

    private final String contentType;

    ExportFormat(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() { return contentType; }

    public static ExportFormat fromString(String value) {
        try {
            return ExportFormat.valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return JSON; // graceful fallback
        }
    }
}
