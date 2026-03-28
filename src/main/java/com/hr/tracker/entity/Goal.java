package com.hr.tracker.entity;

import com.hr.tracker.enums.GoalStatus;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "goal", indexes = {
    @Index(name = "idx_goal_employee", columnList = "employee_id"),
    @Index(name = "idx_goal_cycle", columnList = "cycle_id"),
    @Index(name = "idx_goal_status", columnList = "cycle_id, status")
})
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cycle_id", nullable = false)
    private ReviewCycle cycle;

    @Column(nullable = false, length = 500)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GoalStatus status = GoalStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Goal() {}

    public Goal(Employee employee, ReviewCycle cycle, String title) {
        this.employee = employee;
        this.cycle = cycle;
        this.title = title;
        this.status = GoalStatus.PENDING;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public Long getId()                          { return id; }
    public Employee getEmployee()                { return employee; }
    public ReviewCycle getCycle()                 { return cycle; }
    public String getTitle()                     { return title; }
    public void setTitle(String title)           { this.title = title; }
    public GoalStatus getStatus()                { return status; }
    public void setStatus(GoalStatus status)     { this.status = status; }
    public Instant getCreatedAt()                { return createdAt; }
    public Instant getUpdatedAt()                { return updatedAt; }
}
