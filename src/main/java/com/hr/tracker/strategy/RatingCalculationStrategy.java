package com.hr.tracker.strategy;

import java.util.List;


public interface RatingCalculationStrategy {


    String getKey();


    String getDescription();


    double calculate(List<Integer> scores);
}
