package com.hr.tracker.controller;

import com.hr.tracker.dto.response.CycleSummaryResponse;
import com.hr.tracker.enums.ExportFormat;
import com.hr.tracker.service.CycleSummaryService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cycles")
public class CycleController {

    private final CycleSummaryService summaryService;

    public CycleController(CycleSummaryService summaryService) {
        this.summaryService = summaryService;
    }


    @GetMapping("/{id}/summary")
    public ResponseEntity<CycleSummaryResponse> getCycleSummary(
            @PathVariable Long id,
            @RequestParam(defaultValue = "AVERAGE") String ratingStrategy) {
        return ResponseEntity.ok(summaryService.getSummary(id, ratingStrategy));
    }


    @GetMapping("/{id}/export")
    public ResponseEntity<byte[]> exportCycleSummary(
            @PathVariable Long id,
            @RequestParam(defaultValue = "JSON") String format,
            @RequestParam(defaultValue = "AVERAGE") String ratingStrategy) {

        ExportFormat exportFormat = ExportFormat.fromString(format);
        CycleSummaryService.ExportResult result =
            summaryService.exportSummary(id, exportFormat, ratingStrategy);

        String filename = "cycle_" + id + "_report." + result.fileExtension();

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, result.contentType())
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .body(result.data());
    }
}
