package com.hr.tracker.service;

import com.hr.tracker.dto.request.CreateReviewRequest;
import com.hr.tracker.dto.response.ReviewResponse;
import com.hr.tracker.entity.Employee;
import com.hr.tracker.entity.PerformanceReview;
import com.hr.tracker.entity.ReviewCycle;
import com.hr.tracker.enums.Rating;
import com.hr.tracker.event.ReviewSubmittedEvent;
import com.hr.tracker.exception.ResourceNotFoundException;
import com.hr.tracker.mapper.EntityMapper;
import com.hr.tracker.repository.PerformanceReviewRepository;
import com.hr.tracker.repository.ReviewCycleRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final PerformanceReviewRepository reviewRepo;
    private final ReviewCycleRepository cycleRepo;
    private final EmployeeService employeeService;
    private final ApplicationEventPublisher eventPublisher;
    private final EntityMapper mapper;

    public ReviewService(PerformanceReviewRepository reviewRepo,
                         ReviewCycleRepository cycleRepo,
                         EmployeeService employeeService,
                         ApplicationEventPublisher eventPublisher,
                         EntityMapper mapper) {
        this.reviewRepo = reviewRepo;
        this.cycleRepo = cycleRepo;
        this.employeeService = employeeService;
        this.eventPublisher = eventPublisher;
        this.mapper = mapper;
    }

    /**
     * OBSERVER: Publishes ReviewSubmittedEvent after persistence.
     * Listeners handle audit logging, notifications, and low-rating alerts.
     */
    @Transactional
    public ReviewResponse submit(CreateReviewRequest req) {
        Employee employee = employeeService.findByIdOrThrow(req.employeeId());

        ReviewCycle cycle = cycleRepo.findById(req.cycleId())
            .orElseThrow(() -> new ResourceNotFoundException("ReviewCycle", req.cycleId()));

        Employee reviewer = null;
        if (req.reviewerId() != null) {
            reviewer = employeeService.findByIdOrThrow(req.reviewerId());
        }

        Rating rating = Rating.fromScore(req.rating());

        PerformanceReview review = new PerformanceReview(
            employee, cycle, reviewer, rating, req.reviewerNotes()
        );
        review = reviewRepo.save(review);

        eventPublisher.publishEvent(new ReviewSubmittedEvent(
            this, review.getId(), employee.getId(), cycle.getId(), rating));

        return mapper.toReviewResponse(review);
    }

    /**
     * Fetch-join query — no N+1 on cycle or reviewer.
     */
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsForEmployee(Long employeeId) {
        employeeService.findByIdOrThrow(employeeId);
        return reviewRepo.findByEmployeeIdWithDetails(employeeId)
            .stream().map(mapper::toReviewResponse).toList();
    }
}
