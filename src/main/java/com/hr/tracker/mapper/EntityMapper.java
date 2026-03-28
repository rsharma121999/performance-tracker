package com.hr.tracker.mapper;

import com.hr.tracker.dto.response.*;
import com.hr.tracker.entity.Employee;
import com.hr.tracker.entity.PerformanceReview;
import com.hr.tracker.entity.ReviewCycle;
import com.hr.tracker.enums.Department;
import com.hr.tracker.repository.projection.EmployeeRatingProjection;
import org.springframework.stereotype.Component;


@Component
public class EntityMapper {

    public EmployeeResponse toEmployeeResponse(Employee e) {
        return new EmployeeResponse(
            e.getId(),
            e.getName(),
            e.getDepartment(),
            e.getRole(),
            e.getJoiningDate(),
            e.isActive()
        );
    }

    public EmployeeWithRatingResponse toEmployeeWithRating(EmployeeRatingProjection p) {
        return new EmployeeWithRatingResponse(
            p.getId(),
            p.getName(),
            Department.valueOf(p.getDepartment()),
            p.getRole(),
            p.getJoiningDate(),
            round(p.getAvgRating())
        );
    }

    public EmployeeWithRatingResponse toEmployeeWithRating(Employee e, double rating) {
        return new EmployeeWithRatingResponse(
            e.getId(),
            e.getName(),
            e.getDepartment(),
            e.getRole(),
            e.getJoiningDate(),
            round(rating)
        );
    }

    public ReviewResponse toReviewResponse(PerformanceReview r) {
        ReviewCycle c = r.getCycle();
        Employee reviewer = r.getReviewer();

        return new ReviewResponse(
            r.getId(),
            r.getEmployee().getId(),
            r.getEmployee().getName(),
            reviewer != null ? reviewer.getId() : null,
            reviewer != null ? reviewer.getName() : null,
            new ReviewResponse.CycleInfo(
                c.getId(), c.getName(), c.getStartDate(), c.getEndDate()
            ),
            r.getRating().getScore(),
            r.getRating().getLabel(),
            r.getReviewerNotes(),
            r.getSubmittedAt()
        );
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
