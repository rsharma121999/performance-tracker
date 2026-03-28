package com.hr.tracker.service;

import com.hr.tracker.dto.response.CycleSummaryResponse;
import com.hr.tracker.entity.ReviewCycle;
import com.hr.tracker.enums.ExportFormat;
import com.hr.tracker.enums.GoalStatus;
import com.hr.tracker.exception.ResourceNotFoundException;
import com.hr.tracker.factory.ReportExporter;
import com.hr.tracker.factory.ReportExporterFactory;
import com.hr.tracker.repository.GoalRepository;
import com.hr.tracker.repository.PerformanceReviewRepository;
import com.hr.tracker.repository.PerformanceReviewRepository.CycleRatingTuple;
import com.hr.tracker.repository.ReviewCycleRepository;
import com.hr.tracker.repository.projection.GoalStatusCountProjection;
import com.hr.tracker.repository.projection.TopPerformerProjection;
import com.hr.tracker.strategy.RatingCalculationStrategy;
import com.hr.tracker.strategy.RatingStrategyResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CycleSummaryService {

    private final ReviewCycleRepository cycleRepo;
    private final PerformanceReviewRepository reviewRepo;
    private final GoalRepository goalRepo;
    private final RatingStrategyResolver strategyResolver;
    private final ReportExporterFactory exporterFactory;

    public CycleSummaryService(ReviewCycleRepository cycleRepo,
                               PerformanceReviewRepository reviewRepo,
                               GoalRepository goalRepo,
                               RatingStrategyResolver strategyResolver,
                               ReportExporterFactory exporterFactory) {
        this.cycleRepo = cycleRepo;
        this.reviewRepo = reviewRepo;
        this.goalRepo = goalRepo;
        this.strategyResolver = strategyResolver;
        this.exporterFactory = exporterFactory;
    }


    @Transactional(readOnly = true)
    public CycleSummaryResponse getSummary(Long cycleId, String strategyKey) {
        ReviewCycle cycle = cycleRepo.findById(cycleId)
            .orElseThrow(() -> new ResourceNotFoundException("ReviewCycle", cycleId));

        RatingCalculationStrategy strategy = strategyResolver.resolve(strategyKey);
        long totalReviews = reviewRepo.countByCycleId(cycleId);

        double avgRating;
        CycleSummaryResponse.TopPerformer topPerformer;

        if ("AVERAGE".equals(strategy.getKey())) {
            Double dbAvg = reviewRepo.findAverageRatingByCycleId(cycleId);
            avgRating = dbAvg != null ? round(dbAvg) : 0.0;
            topPerformer = findTopPerformerByDbAvg(cycleId);
        } else {
            StrategyResult result = computeWithStrategy(cycleId, strategy);
            avgRating = result.overallAverage;
            topPerformer = result.topPerformer;
        }

        return new CycleSummaryResponse(
            cycle.getId(), cycle.getName(), strategy.getKey(),
            avgRating, (int) totalReviews, topPerformer, buildGoalStats(cycleId));
    }


    @Transactional(readOnly = true)
    public ExportResult exportSummary(Long cycleId, ExportFormat format, String strategyKey) {
        CycleSummaryResponse summary = getSummary(cycleId, strategyKey);
        ReportExporter exporter = exporterFactory.getExporter(format);
        return new ExportResult(
            exporter.export(summary),
            exporter.getFormat().getContentType(),
            exporter.getFormat().name().toLowerCase());
    }



    private CycleSummaryResponse.TopPerformer findTopPerformerByDbAvg(Long cycleId) {
        List<TopPerformerProjection> ranked = reviewRepo.findTopPerformersByCycleId(cycleId);
        if (ranked.isEmpty()) return null;
        TopPerformerProjection top = ranked.get(0);
        return new CycleSummaryResponse.TopPerformer(
            top.getEmployeeId(), top.getEmployeeName(), round(top.getAvgRating()));
    }

    private StrategyResult computeWithStrategy(Long cycleId, RatingCalculationStrategy strategy) {
        List<CycleRatingTuple> allRatings = reviewRepo.findAllRatingsForCycle(cycleId);

        Map<Long, String> names = new LinkedHashMap<>();
        Map<Long, List<Integer>> scoresByEmployee = new LinkedHashMap<>();

        for (CycleRatingTuple tuple : allRatings) {
            names.putIfAbsent(tuple.getEmployeeId(), tuple.getEmployeeName());
            scoresByEmployee.computeIfAbsent(tuple.getEmployeeId(), k -> new ArrayList<>())
                .add(tuple.getRating());
        }

        double totalWeighted = 0.0;
        int count = 0;
        CycleSummaryResponse.TopPerformer topPerformer = null;
        double topScore = Double.NEGATIVE_INFINITY;

        for (Map.Entry<Long, List<Integer>> entry : scoresByEmployee.entrySet()) {
            double score = strategy.calculate(entry.getValue());
            totalWeighted += score;
            count++;
            if (score > topScore) {
                topScore = score;
                topPerformer = new CycleSummaryResponse.TopPerformer(
                    entry.getKey(), names.get(entry.getKey()), round(score));
            }
        }

        double avg = count > 0 ? round(totalWeighted / count) : 0.0;
        return new StrategyResult(avg, topPerformer);
    }

    private CycleSummaryResponse.GoalStats buildGoalStats(Long cycleId) {
        EnumMap<GoalStatus, Long> counts = new EnumMap<>(GoalStatus.class);
        for (GoalStatus s : GoalStatus.values()) counts.put(s, 0L);

        for (GoalStatusCountProjection row : goalRepo.countByStatusForCycle(cycleId)) {
            counts.put(row.getStatus(), row.getCount());
        }

        long completed = counts.get(GoalStatus.COMPLETED);
        long missed    = counts.get(GoalStatus.MISSED);
        long pending   = counts.get(GoalStatus.PENDING);
        return new CycleSummaryResponse.GoalStats(completed, missed, pending,
            completed + missed + pending);
    }

    private double round(double v) { return Math.round(v * 100.0) / 100.0; }

    private record StrategyResult(double overallAverage, CycleSummaryResponse.TopPerformer topPerformer) {}

    public record ExportResult(byte[] data, String contentType, String fileExtension) {}
}
