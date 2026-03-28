package com.hr.tracker.dto.response;


public record CycleSummaryResponse(
    Long cycleId,
    String cycleName,
    String ratingStrategy,
    double averageRating,
    int totalReviews,
    TopPerformer topPerformer,
    GoalStats goals
) {

    public record TopPerformer(
        Long employeeId,
        String employeeName,
        double averageRating
    ) {}

    public record GoalStats(
        long completed,
        long missed,
        long pending,
        long total
    ) {}
}
