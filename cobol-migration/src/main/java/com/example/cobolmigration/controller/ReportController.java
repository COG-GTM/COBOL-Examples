package com.example.cobolmigration.controller;

import com.example.cobolmigration.model.StudentRecord;
import com.example.cobolmigration.service.ReportService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST endpoint replacing report_writer/report_test.cbl.
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Accepts student records and returns a formatted text report.
     * Replaces the COBOL report writer flow.
     */
    @PostMapping(value = "/student", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> generateStudentReport(@RequestBody List<StudentRecord> records) {
        return ResponseEntity.ok(reportService.generateReport(records));
    }
}
