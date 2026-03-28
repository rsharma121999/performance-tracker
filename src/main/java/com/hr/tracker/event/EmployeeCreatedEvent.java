package com.hr.tracker.event;

import com.hr.tracker.enums.Department;
import org.springframework.context.ApplicationEvent;


public class EmployeeCreatedEvent extends ApplicationEvent {

    private final Long employeeId;
    private final String employeeName;
    private final Department department;

    public EmployeeCreatedEvent(Object source, Long employeeId,
                                String employeeName, Department department) {
        super(source);
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.department = department;
    }

    public Long getEmployeeId()      { return employeeId; }
    public String getEmployeeName()  { return employeeName; }
    public Department getDepartment() { return department; }
}
