package com.hr.tracker.factory;

import com.hr.tracker.dto.response.CycleSummaryResponse;
import com.hr.tracker.enums.ExportFormat;


public interface ReportExporter {
    ExportFormat getFormat();
    byte[] export(CycleSummaryResponse summary);
}
