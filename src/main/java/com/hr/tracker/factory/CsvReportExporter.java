package com.hr.tracker.factory;

import com.hr.tracker.dto.response.CycleSummaryResponse;
import com.hr.tracker.enums.ExportFormat;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;

@Component
public class CsvReportExporter implements ReportExporter {

    @Override public ExportFormat getFormat() { return ExportFormat.CSV; }

    @Override
    public byte[] export(CycleSummaryResponse summary) {
        StringBuilder sb = new StringBuilder();
        sb.append("cycle_id,cycle_name,rating_strategy,average_rating,total_reviews,")
          .append("top_performer_id,top_performer_name,top_performer_avg,")
          .append("goals_completed,goals_missed,goals_pending,goals_total\n");

        sb.append(summary.cycleId()).append(',');
        sb.append(escapeCsv(summary.cycleName())).append(',');
        sb.append(summary.ratingStrategy()).append(',');
        sb.append(summary.averageRating()).append(',');
        sb.append(summary.totalReviews()).append(',');

        if (summary.topPerformer() != null) {
            sb.append(summary.topPerformer().employeeId()).append(',');
            sb.append(escapeCsv(summary.topPerformer().employeeName())).append(',');
            sb.append(summary.topPerformer().averageRating()).append(',');
        } else {
            sb.append(",,,");
        }

        sb.append(summary.goals().completed()).append(',');
        sb.append(summary.goals().missed()).append(',');
        sb.append(summary.goals().pending()).append(',');
        sb.append(summary.goals().total()).append('\n');

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
