package com.hr.tracker.strategy;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class SimpleAverageStrategy implements RatingCalculationStrategy {

    @Override
    public String getKey() { return "AVERAGE"; }

    @Override
    public String getDescription() { return "Simple arithmetic mean of all review scores."; }

    @Override
    public double calculate(List<Integer> scores) {
        return scores.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }
}
