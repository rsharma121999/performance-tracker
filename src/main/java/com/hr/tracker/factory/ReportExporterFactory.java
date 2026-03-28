package com.hr.tracker.factory;

import com.hr.tracker.enums.ExportFormat;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
public class ReportExporterFactory {

    private final Map<ExportFormat, ReportExporter> exportersByFormat;
    private final ReportExporter defaultExporter;

    public ReportExporterFactory(List<ReportExporter> exporters) {
        this.exportersByFormat = exporters.stream()
            .collect(Collectors.toMap(ReportExporter::getFormat, Function.identity()));

        this.defaultExporter = exportersByFormat.getOrDefault(ExportFormat.JSON,
            exporters.stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No ReportExporter beans found")));
    }

    public ReportExporter getExporter(ExportFormat format) {
        if (format == null) return defaultExporter;
        return exportersByFormat.getOrDefault(format, defaultExporter);
    }
}
