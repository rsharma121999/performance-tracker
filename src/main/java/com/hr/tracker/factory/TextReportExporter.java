package com.hr.tracker.factory;

import com.hr.tracker.dto.response.CycleSummaryResponse;
import com.hr.tracker.enums.ExportFormat;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;

@Component
public class TextReportExporter implements ReportExporter {

    @Override public ExportFormat getFormat() { return ExportFormat.TEXT; }

    @Override
    public byte[] export(CycleSummaryResponse summary) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Performance Report: ").append(summary.cycleName()).append(" ===\n");
        sb.append("Strategy       : ").append(summary.ratingStrategy()).append("\n\n");
        sb.append("Average Rating : ").append(summary.averageRating()).append(" / 5.0\n");
        sb.append("Total Reviews  : ").append(summary.totalReviews()).append("\n\n");

        if (summary.topPerformer() != null) {
            sb.append("Top Performer  : ").append(summary.topPerformer().employeeName())
              .append(" (avg ").append(summary.topPerformer().averageRating()).append(")\n\n");
        }

        sb.append("Goals:\n");
        sb.append("  Completed : ").append(summary.goals().completed()).append("\n");
        sb.append("  Missed    : ").append(summary.goals().missed()).append("\n");
        sb.append("  Pending   : ").append(summary.goals().pending()).append("\n");
        sb.append("  Total     : ").append(summary.goals().total()).append("\n");

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
