package com.cobolmigration.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ReportService - validates migration of report_writer/report_test.cbl.
 */
class ReportServiceTest {

    private ReportService service;

    @BeforeEach
    void setUp() {
        service = new ReportService();
    }

    @Test
    void generateReport_containsHeader() {
        List<ReportService.ReportRecord> records = List.of(
                new ReportService.ReportRecord(1234, "John Smith", "CS", 5)
        );
        String report = service.generateReport(records);
        assertTrue(report.contains("Customer Order Report"));
        assertTrue(report.contains("PAGE"));
    }

    @Test
    void generateReport_containsDetailLines() {
        List<ReportService.ReportRecord> records = List.of(
                new ReportService.ReportRecord(1234, "John Smith", "CS", 5),
                new ReportService.ReportRecord(2345, "Jane Doe", "PHY", 3)
        );
        String report = service.generateReport(records);
        assertTrue(report.contains("001234"));
        assertTrue(report.contains("John Smith"));
        assertTrue(report.contains("002345"));
        assertTrue(report.contains("Jane Doe"));
    }

    @Test
    void generateReport_emptyRecords() {
        String report = service.generateReport(List.of());
        assertEquals("", report);
    }

    @Test
    void generateReport_pageBreakAfterMaxDetail() {
        // Create enough records to fill past LAST_DETAIL_LINE (42 - 6 = 36 records per page)
        List<ReportService.ReportRecord> records = new java.util.ArrayList<>();
        for (int i = 0; i < 40; i++) {
            records.add(new ReportService.ReportRecord(i + 1, "Student " + i, "CS", i % 10));
        }
        String report = service.generateReport(records);
        assertTrue(report.contains("PAGE   1"));
        assertTrue(report.contains("PAGE   2"));
    }

    @Test
    void parseInputLine_parsesCorrectly() {
        // studentId(6) + studentName(20) + major(3) + numCourses(2)
        String line = "001234John Smith          CS 05";
        ReportService.ReportRecord record = service.parseInputLine(line);
        assertEquals(1234, record.studentId());
        assertEquals("John Smith          ", record.studentName());
        assertEquals("CS ", record.major());
        assertEquals(5, record.numCourses());
    }

    @Test
    void generateReport_formatsStudentIdWithLeadingZeros() {
        List<ReportService.ReportRecord> records = List.of(
                new ReportService.ReportRecord(42, "Test Student", "MAT", 7)
        );
        String report = service.generateReport(records);
        assertTrue(report.contains("000042"));
    }
}
