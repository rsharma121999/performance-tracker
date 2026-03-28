package com.hr.tracker.event;

import com.hr.tracker.enums.Rating;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
public class LowRatingAlertListener {

    private static final Logger log = LoggerFactory.getLogger(LowRatingAlertListener.class);
    private static final Rating THRESHOLD = Rating.NEEDS_IMPROVEMENT;

    @EventListener
    public void onReviewSubmitted(ReviewSubmittedEvent event) {
        if (event.getRating().getScore() <= THRESHOLD.getScore()) {
            log.warn("ALERT | Low rating detected: employeeId={}, cycleId={}, rating={} ({}). Flagging for HR.",
                event.getEmployeeId(), event.getCycleId(),
                event.getRating().getScore(), event.getRating().getLabel());
        }
    }
}
