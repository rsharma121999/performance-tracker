package com.hr.tracker.service;

import com.hr.tracker.dto.request.CreateEmployeeRequest;
import com.hr.tracker.dto.response.EmployeeResponse;
import com.hr.tracker.dto.response.EmployeeWithRatingResponse;
import com.hr.tracker.entity.Employee;
import com.hr.tracker.enums.Department;
import com.hr.tracker.event.EmployeeCreatedEvent;
import com.hr.tracker.exception.ResourceNotFoundException;
import com.hr.tracker.mapper.EntityMapper;
import com.hr.tracker.repository.EmployeeRepository;
import com.hr.tracker.repository.PerformanceReviewRepository;
import com.hr.tracker.repository.projection.EmployeeRatingProjection;
import com.hr.tracker.strategy.RatingCalculationStrategy;
import com.hr.tracker.strategy.RatingStrategyResolver;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepo;
    private final PerformanceReviewRepository reviewRepo;
    private final ApplicationEventPublisher eventPublisher;
    private final RatingStrategyResolver strategyResolver;
    private final EntityMapper mapper;

    public EmployeeService(EmployeeRepository employeeRepo,
                           PerformanceReviewRepository reviewRepo,
                           ApplicationEventPublisher eventPublisher,
                           RatingStrategyResolver strategyResolver,
                           EntityMapper mapper) {
        this.employeeRepo = employeeRepo;
        this.reviewRepo = reviewRepo;
        this.eventPublisher = eventPublisher;
        this.strategyResolver = strategyResolver;
        this.mapper = mapper;
    }


    @Transactional
    public EmployeeResponse create(CreateEmployeeRequest req) {
        Employee employee = new Employee(
            req.name().trim(),
            req.department(),
            req.role().trim(),
            req.joiningDate()
        );
        employee = employeeRepo.save(employee);

        eventPublisher.publishEvent(new EmployeeCreatedEvent(
            this, employee.getId(), employee.getName(), employee.getDepartment()));

        return mapper.toEmployeeResponse(employee);
    }

    @Transactional(readOnly = true)
    public Employee findByIdOrThrow(Long id) {
        return employeeRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
    }


    @Transactional(readOnly = true)
    public List<EmployeeWithRatingResponse> filterByDepartmentAndMinRating(
            Department department, double minRating, String strategyKey) {

        RatingCalculationStrategy strategy = strategyResolver.resolve(strategyKey);

        if ("AVERAGE".equals(strategy.getKey())) {
            return filterWithDbAverage(department, minRating);
        }
        return filterWithStrategy(department, minRating, strategy);
    }

    private List<EmployeeWithRatingResponse> filterWithDbAverage(Department department, double minRating) {
        String deptStr = department != null ? department.name() : null;
        List<EmployeeRatingProjection> projections =
            employeeRepo.findByDepartmentAndMinAvgRating(deptStr, minRating);
        return projections.stream().map(mapper::toEmployeeWithRating).toList();
    }

    private List<EmployeeWithRatingResponse> filterWithStrategy(
            Department department, double minRating, RatingCalculationStrategy strategy) {

        List<Employee> candidates = (department != null)
            ? employeeRepo.findByDepartmentAndActiveTrue(department)
            : employeeRepo.findByActiveTrue();

        return candidates.stream()
            .map(emp -> {
                List<Integer> scores = reviewRepo.findRatingsByEmployeeId(emp.getId());
                if (scores.isEmpty()) return null;
                double computed = strategy.calculate(scores);
                return mapper.toEmployeeWithRating(emp, computed);
            })
            .filter(dto -> dto != null && dto.averageRating() >= minRating)
            .sorted((a, b) -> Double.compare(b.averageRating(), a.averageRating()))
            .toList();
    }
}
