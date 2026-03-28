package com.hr.tracker.enums;


public enum Rating {

    UNSATISFACTORY(1, "Unsatisfactory"),
    NEEDS_IMPROVEMENT(2, "Needs Improvement"),
    MEETS_EXPECTATIONS(3, "Meets Expectations"),
    EXCEEDS_EXPECTATIONS(4, "Exceeds Expectations"),
    OUTSTANDING(5, "Outstanding");

    private final int score;
    private final String label;

    Rating(int score, String label) {
        this.score = score;
        this.label = label;
    }

    public int getScore() { return score; }

    public String getLabel() { return label; }


    public static Rating fromScore(int score) {
        for (Rating r : values()) {
            if (r.score == score) return r;
        }
        throw new IllegalArgumentException(
            "Invalid rating score: " + score + ". Must be between 1 and 5.");
    }
}
