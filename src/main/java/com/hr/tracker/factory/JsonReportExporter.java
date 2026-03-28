package com.hr.tracker.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hr.tracker.dto.response.CycleSummaryResponse;
import com.hr.tracker.enums.ExportFormat;
import org.springframework.stereotype.Component;

@Component
public class JsonReportExporter implements ReportExporter {

    private final ObjectMapper mapper;

    public JsonReportExporter() {
        this.mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .findAndRegisterModules();
    }

    @Override public ExportFormat getFormat() { return ExportFormat.JSON; }

    @Override
    public byte[] export(CycleSummaryResponse summary) {
        try {
            return mapper.writeValueAsBytes(summary);
        } catch (Exception e) {
            throw new RuntimeException("Failed to export JSON report", e);
        }
    }
}
