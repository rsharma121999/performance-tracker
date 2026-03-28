package com.hr.tracker.enums;


public enum Department {
    ENGINEERING,
    PRODUCT,
    DESIGN,
    MARKETING,
    SALES,
    HR,
    FINANCE,
    OPERATIONS,
    LEGAL,
    QA;


    public static Department fromString(String value) {
        try {
            return Department.valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Invalid department: '" + value + "'. Allowed values: " + java.util.Arrays.toString(values()));
        }
    }
}
