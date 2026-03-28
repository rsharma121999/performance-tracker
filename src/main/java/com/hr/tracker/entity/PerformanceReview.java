package com.hr.tracker.entity;

import com.hr.tracker.enums.Rating;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "performance_review", indexes = {
    @Index(name = "idx_review_employee", columnList = "employee_id"),
    @Index(name = "idx_review_cycle", columnList = "cycle_id"),
    @Index(name = "idx_review_emp_cycle", columnList = "employee_id, cycle_id")
})
public class PerformanceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cycle_id", nullable = false)
    private ReviewCycle cycle;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private Employee reviewer;

    @Convert(converter = com.hr.tracker.enums.RatingConverter.class)
    @Column(nullable = false)
    private Rating rating;

    @Column(name = "reviewer_notes", length = 4000)
    private String reviewerNotes;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    private Instant submittedAt;

    protected PerformanceReview() {}

    public PerformanceReview(Employee employee, ReviewCycle cycle, Employee reviewer,
                             Rating rating, String reviewerNotes) {
        this.employee = employee;
        this.cycle = cycle;
        this.reviewer = reviewer;
        this.rating = rating;
        this.reviewerNotes = reviewerNotes;
    }

    @PrePersist
    void onCreate() {
        this.submittedAt = Instant.now();
    }

    public Long getId()                          { return id; }
    public Employee getEmployee()                { return employee; }
    public ReviewCycle getCycle()                 { return cycle; }
    public Employee getReviewer()                { return reviewer; }
    public Rating getRating()                    { return rating; }
    public void setRating(Rating rating)         { this.rating = rating; }
    public String getReviewerNotes()             { return reviewerNotes; }
    public void setReviewerNotes(String notes)   { this.reviewerNotes = notes; }
    public Instant getSubmittedAt()              { return submittedAt; }


    public int getRatingScore() {
        return rating.getScore();
    }
}
