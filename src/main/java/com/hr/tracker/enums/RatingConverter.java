package com.hr.tracker.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter(autoApply = false)
public class RatingConverter implements AttributeConverter<Rating, Short> {

    @Override
    public Short convertToDatabaseColumn(Rating rating) {
        return rating != null ? (short) rating.getScore() : null;
    }

    @Override
    public Rating convertToEntityAttribute(Short score) {
        return score != null ? Rating.fromScore(score) : null;
    }
}
