package com.hr.tracker.controller;

import com.hr.tracker.dto.request.CreateEmployeeRequest;
import com.hr.tracker.dto.response.EmployeeResponse;
import com.hr.tracker.dto.response.EmployeeWithRatingResponse;
import com.hr.tracker.enums.Department;
import com.hr.tracker.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> create(
            @Valid @RequestBody CreateEmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.create(request));
    }


    @GetMapping
    public ResponseEntity<List<EmployeeWithRatingResponse>> filter(
            @RequestParam(required = false) Department department,
            @RequestParam(defaultValue = "0") double minRating,
            @RequestParam(defaultValue = "AVERAGE") String ratingStrategy) {

        return ResponseEntity.ok(
            employeeService.filterByDepartmentAndMinRating(department, minRating, ratingStrategy));
    }
}
