package com.example.cobolmigration.service;

import com.example.cobolmigration.model.StudentRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportServiceTest {

    private ReportService service;

    @BeforeEach
    void setUp() {
        service = new ReportService();
    }

    @Test
    void generateReport_shouldContainHeader() {
        List<StudentRecord> records = List.of(
            new StudentRecord(3345, "Test Name", "PHY", 12)
        );
        String report = service.generateReport(records);
        assertTrue(report.contains("Customer Order Report"));
        assertTrue(report.contains("PAGE   1"));
    }

    @Test
    void generateReport_shouldContainStudentData() {
        List<StudentRecord> records = List.of(
            new StudentRecord(3345, "Test Name", "PHY", 12)
        );
        String report = service.generateReport(records);
        assertTrue(report.contains("003345"));
        assertTrue(report.contains("Test Name"));
        assertTrue(report.contains("PHY"));
        assertTrue(report.contains("12"));
    }

    @Test
    void generateReport_shouldPaginateForManyRecords() {
        // Page limit: 37 detail lines per page (lines 6-42)
        List<StudentRecord> records = new java.util.ArrayList<>();
        for (int i = 0; i < 50; i++) {
            records.add(new StudentRecord(i, "Student " + i, "CS", 5));
        }
        String report = service.generateReport(records);
        assertTrue(report.contains("PAGE   1"));
        assertTrue(report.contains("PAGE   2"));
    }

    @Test
    void generateReport_emptyRecords_shouldStillContainHeader() {
        String report = service.generateReport(List.of());
        assertTrue(report.contains("Customer Order Report"));
        assertFalse(report.contains("--- Page Break ---"));
    }
}
