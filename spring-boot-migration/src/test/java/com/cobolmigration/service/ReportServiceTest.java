package com.cobolmigration.service;

import com.cobolmigration.model.StudentRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for ReportService verifying report layout matches
 * the COBOL Report Writer definition from report_writer/report_test.cbl.
 */
class ReportServiceTest {

    private ReportService reportService;

    @BeforeEach
    void setUp() {
        reportService = new ReportService();
    }

    @Test
    void generateReport_shouldContainHeading() {
        List<StudentRecord> students = Arrays.asList(
                new StudentRecord(1001, "John Smith", "CSC", 5)
        );

        String report = reportService.generateReport(students);

        assertNotNull(report);
        assertTrue(report.contains("Customer Order Report"));
    }

    @Test
    void generateReport_shouldContainPageCounter() {
        List<StudentRecord> students = Arrays.asList(
                new StudentRecord(1001, "John Smith", "CSC", 5)
        );

        String report = reportService.generateReport(students);

        assertTrue(report.contains("PAGE"));
        assertTrue(report.contains("1"));
    }

    @Test
    void generateReport_shouldContainStudentData() {
        List<StudentRecord> students = Arrays.asList(
                new StudentRecord(3345, "Test Name", "PHY", 12),
                new StudentRecord(1234, "Jane Doe", "CSC", 8)
        );

        String report = reportService.generateReport(students);

        assertTrue(report.contains("003345"));
        assertTrue(report.contains("Test Name"));
        assertTrue(report.contains("PHY"));
        assertTrue(report.contains("12"));
        assertTrue(report.contains("001234"));
        assertTrue(report.contains("Jane Doe"));
    }

    @Test
    void generateReport_withEmptyList_shouldReturnEmptyReport() {
        String report = reportService.generateReport(List.of());
        assertNotNull(report);
        assertTrue(report.isEmpty());
    }

    @Test
    void generateReport_withManyRecords_shouldPaginate() {
        // Create enough records to exceed the detail line limit (42 - 6 = 36 detail lines per page)
        List<StudentRecord> students = new java.util.ArrayList<>();
        for (int i = 1; i <= 40; i++) {
            students.add(new StudentRecord(i, "Student " + i, "CS", i % 20));
        }

        String report = reportService.generateReport(students);

        // Should have page 2
        assertTrue(report.contains("PAGE  2"));
    }
}
