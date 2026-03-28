package com.hr.tracker.entity;

import com.hr.tracker.enums.Department;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "employee", indexes = {
    @Index(name = "idx_employee_department", columnList = "department"),
    @Index(name = "idx_employee_active", columnList = "active")
})
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Department department;

    @Column(nullable = false, length = 150)
    private String role;

    @Column(name = "joining_date", nullable = false)
    private LocalDate joiningDate;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Employee() {}

    public Employee(String name, Department department, String role, LocalDate joiningDate) {
        this.name = name;
        this.department = department;
        this.role = role;
        this.joiningDate = joiningDate;
        this.active = true;
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

    public Long getId()                     { return id; }
    public String getName()                 { return name; }
    public void setName(String name)        { this.name = name; }
    public Department getDepartment()       { return department; }
    public void setDepartment(Department d) { this.department = d; }
    public String getRole()                 { return role; }
    public void setRole(String role)        { this.role = role; }
    public LocalDate getJoiningDate()       { return joiningDate; }
    public boolean isActive()               { return active; }
    public void setActive(boolean active)   { this.active = active; }
    public Instant getCreatedAt()           { return createdAt; }
    public Instant getUpdatedAt()           { return updatedAt; }
}
