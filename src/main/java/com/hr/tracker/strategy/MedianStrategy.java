package com.hr.tracker.strategy;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Component
public class MedianStrategy implements RatingCalculationStrategy {

    @Override
    public String getKey() { return "MEDIAN"; }

    @Override
    public String getDescription() { return "Median score — resilient to outliers."; }

    @Override
    public double calculate(List<Integer> scores) {
        List<Integer> sorted = new ArrayList<>(scores);
        Collections.sort(sorted);
        int n = sorted.size();
        if (n % 2 == 1) {
            return sorted.get(n / 2);
        }
        return (sorted.get(n / 2 - 1) + sorted.get(n / 2)) / 2.0;
    }
}
