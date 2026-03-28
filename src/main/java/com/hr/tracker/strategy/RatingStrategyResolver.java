package com.hr.tracker.strategy;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
public class RatingStrategyResolver {

    private final Map<String, RatingCalculationStrategy> strategiesByKey;
    private final RatingCalculationStrategy defaultStrategy;

    public RatingStrategyResolver(List<RatingCalculationStrategy> strategies) {
        this.strategiesByKey = strategies.stream()
            .collect(Collectors.toMap(
                s -> s.getKey().toUpperCase(),
                Function.identity()
            ));
        this.defaultStrategy = strategiesByKey.getOrDefault("AVERAGE",
            strategies.stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No RatingCalculationStrategy beans found")));
    }

    public RatingCalculationStrategy resolve(String key) {
        if (key == null || key.isBlank()) return defaultStrategy;
        return strategiesByKey.getOrDefault(key.toUpperCase(), defaultStrategy);
    }

    public RatingCalculationStrategy getDefault() { return defaultStrategy; }

    public List<String> getSupportedKeys() { return List.copyOf(strategiesByKey.keySet()); }
}
