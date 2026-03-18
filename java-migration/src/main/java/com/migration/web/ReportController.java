package com.migration.web;

import com.migration.reports.ReportService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoint for report generation.
 * Exposes GET /api/reports/customers to generate account reports.
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * GET /api/reports/customers - Generate an account report.
     *
     * @param format "text" (default) or "csv"
     */
    @GetMapping("/customers")
    public ResponseEntity<String> getCustomerReport(
            @RequestParam(defaultValue = "text") String format) {

        String report;
        MediaType mediaType;

        if ("csv".equalsIgnoreCase(format)) {
            report = reportService.generateAccountReportCsv();
            mediaType = MediaType.parseMediaType("text/csv");
        } else {
            report = reportService.generateAccountReport();
            mediaType = MediaType.TEXT_PLAIN;
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(report);
    }
}
