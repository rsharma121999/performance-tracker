package com.hr.tracker.strategy;

import org.springframework.stereotype.Component;
import java.util.List;


@Component
public class WeightedRecentStrategy implements RatingCalculationStrategy {

    @Override
    public String getKey() { return "WEIGHTED_RECENT"; }

    @Override
    public String getDescription() { return "Linearly weighted average favoring recent reviews."; }

    @Override
    public double calculate(List<Integer> scores) {
        double weightedSum = 0.0;
        double totalWeight = 0.0;
        for (int i = 0; i < scores.size(); i++) {
            double weight = i + 1;
            weightedSum += scores.get(i) * weight;
            totalWeight += weight;
        }
        return totalWeight > 0 ? weightedSum / totalWeight : 0.0;
    }
}
