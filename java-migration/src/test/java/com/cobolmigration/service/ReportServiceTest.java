package com.cobolmigration.service;

import com.cobolmigration.service.ReportService.ReportRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ReportService.
 * Verifies report output matches the COBOL report_writer layout.
 */
class ReportServiceTest {

    private ReportService service;

    @BeforeEach
    void setUp() {
        service = new ReportService();
    }

    @Test
    void generateReport_shouldIncludeHeader() {
        List<ReportRecord> records = List.of(
                new ReportRecord(1001, "John Smith", "CS", 5)
        );
        String report = service.generateReport(records);

        assertTrue(report.contains("Customer Order Report"));
    }

    @Test
    void generateReport_shouldIncludePageNumber() {
        List<ReportRecord> records = List.of(
                new ReportRecord(1001, "John Smith", "CS", 5)
        );
        String report = service.generateReport(records);

        assertTrue(report.contains("PAGE"));
        assertTrue(report.contains("  1"));
    }

    @Test
    void generateReport_shouldFormatDetailLines() {
        List<ReportRecord> records = List.of(
                new ReportRecord(3345, "Test Name", "PHY", 12)
        );
        String report = service.generateReport(records);

        // Student ID should be zero-padded to 6 digits
        assertTrue(report.contains("003345"));
        assertTrue(report.contains("Test Name"));
        assertTrue(report.contains("PHY"));
        assertTrue(report.contains("12"));
    }

    @Test
    void generateReport_shouldHandleMultipleRecords() {
        List<ReportRecord> records = List.of(
                new ReportRecord(1001, "Alice", "CS", 5),
                new ReportRecord(1002, "Bob", "MAT", 3),
                new ReportRecord(1003, "Charlie", "ENG", 7)
        );
        String report = service.generateReport(records);

        assertTrue(report.contains("001001"));
        assertTrue(report.contains("001002"));
        assertTrue(report.contains("001003"));
        assertTrue(report.contains("Alice"));
        assertTrue(report.contains("Bob"));
        assertTrue(report.contains("Charlie"));
    }

    @Test
    void generateReport_shouldHandleEmptyList() {
        List<ReportRecord> records = List.of();
        String report = service.generateReport(records);

        // Should still have header
        assertTrue(report.contains("Customer Order Report"));
    }

    @Test
    void generateReport_shouldPaginateWhenExceedingDetailLines() {
        // COBOL: first detail line 6, last detail line 42 = 37 detail lines per page
        List<ReportRecord> records = new java.util.ArrayList<>();
        for (int i = 0; i < 50; i++) {
            records.add(new ReportRecord(i + 1, "Student " + i, "CS", i % 10));
        }
        String report = service.generateReport(records);

        // Should have page 1 and page 2
        assertTrue(report.contains("  1")); // page 1
        assertTrue(report.contains("  2")); // page 2
    }
}
