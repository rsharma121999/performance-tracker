package com.hr.tracker.controller;

import com.hr.tracker.dto.request.CreateReviewRequest;
import com.hr.tracker.dto.response.ReviewResponse;
import com.hr.tracker.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/reviews")
    public ResponseEntity<ReviewResponse> submit(
            @Valid @RequestBody CreateReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.submit(request));
    }

    @GetMapping("/employees/{id}/reviews")
    public ResponseEntity<List<ReviewResponse>> getEmployeeReviews(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReviewsForEmployee(id));
    }
}
