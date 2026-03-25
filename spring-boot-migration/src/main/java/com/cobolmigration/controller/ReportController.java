package com.cobolmigration.controller;

import com.cobolmigration.model.ReportRecord;
import com.cobolmigration.service.ReportService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for report generation.
 * Replaces COBOL Report Writer output (report_writer/report_test.cbl).
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * GET /api/reports/customers - Generates a customer report.
     * Returns HTML by default. Use ?format=text for plain text output.
     * Uses sample data mirroring the COBOL input.txt format.
     */
    @GetMapping("/customers")
    public ResponseEntity<String> getCustomerReport(
            @RequestParam(defaultValue = "html") String format) {

        List<ReportRecord> sampleRecords = getSampleReportData();

        if ("text".equalsIgnoreCase(format)) {
            String textReport = reportService.generateTextReport(sampleRecords);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(textReport);
        }

        String htmlReport = reportService.generateHtmlReport(sampleRecords);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(htmlReport);
    }

    private List<ReportRecord> getSampleReportData() {
        return List.of(
                ReportRecord.builder().studentId(100001).studentName("Alice Johnson").major("CSC").numCourses(5).build(),
                ReportRecord.builder().studentId(100002).studentName("Bob Williams").major("MTH").numCourses(4).build(),
                ReportRecord.builder().studentId(100003).studentName("Carol Davis").major("PHY").numCourses(6).build(),
                ReportRecord.builder().studentId(100004).studentName("David Brown").major("ENG").numCourses(3).build(),
                ReportRecord.builder().studentId(100005).studentName("Eve Martinez").major("CSC").numCourses(7).build()
        );
    }
}
