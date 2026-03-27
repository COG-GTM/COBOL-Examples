package com.example.migration.service;

import com.example.migration.service.ReportService.StudentRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ReportService.
 * Verifies report output format matches COBOL Report Writer output
 * from report_writer/report_test.cbl.
 *
 * Expected report format:
 *   Header: "Customer Order Report" + page counter
 *   Detail lines: student ID, name, major, num courses
 */
class ReportServiceTest {

    private ReportService reportService;

    @BeforeEach
    void setUp() {
        reportService = new ReportService();
    }

    @Test
    void generateReport_containsPageHeader() {
        List<StudentRecord> records = List.of(
                new StudentRecord(3345, "Test Name", "PHY", 12)
        );

        String report = reportService.generateReport(records);

        assertTrue(report.contains("Customer Order Report"));
        assertTrue(report.contains("PAGE"));
        assertTrue(report.contains("1"));
    }

    @Test
    void generateReport_containsDetailLines() {
        List<StudentRecord> records = List.of(
                new StudentRecord(3345, "Test Name", "PHY", 12),
                new StudentRecord(1001, "Jane Smith", "CS", 8)
        );

        String report = reportService.generateReport(records);

        assertTrue(report.contains("003345"));
        assertTrue(report.contains("Test Name"));
        assertTrue(report.contains("PHY"));
        assertTrue(report.contains("12"));
        assertTrue(report.contains("001001"));
        assertTrue(report.contains("Jane Smith"));
        assertTrue(report.contains("CS"));
    }

    @Test
    void generateReport_handlesEmptyInput() {
        String report = reportService.generateReport(List.of());

        assertTrue(report.contains("Customer Order Report"));
        assertTrue(report.contains("PAGE"));
    }

    @Test
    void generateReport_paginatesAtDetailLimit() {
        List<StudentRecord> records = new java.util.ArrayList<>();
        for (int i = 0; i < 40; i++) {
            records.add(new StudentRecord(i, "Student " + i, "CS", i % 10));
        }

        String report = reportService.generateReport(records);

        long pageCount = report.lines()
                .filter(line -> line.contains("Customer Order Report"))
                .count();
        assertTrue(pageCount >= 2, "Should have at least 2 pages for 40 records");
    }
}
