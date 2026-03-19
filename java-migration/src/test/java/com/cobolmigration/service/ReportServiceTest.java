package com.cobolmigration.service;

import com.cobolmigration.model.StudentRecord;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for ReportService validating report formatting matching report_writer/report_test.cbl.
 */
class ReportServiceTest {

    private final ReportService reportService = new ReportService();

    @Test
    @DisplayName("generateReport includes page header with report title")
    void testReportHeader() {
        List<StudentRecord> records = List.of(
                new StudentRecord(123456, "Test Student", "CSC", 5));

        List<String> report = reportService.generateReport(records);

        // Header should contain the report title
        assertThat(report.get(0)).contains("Customer Order Report");
    }

    @Test
    @DisplayName("generateReport includes page number in header")
    void testPageNumber() {
        List<StudentRecord> records = List.of(
                new StudentRecord(123456, "Test Student", "CSC", 5));

        List<String> report = reportService.generateReport(records);

        // Second line should contain PAGE and page number
        assertThat(report.get(1)).contains("PAGE");
        assertThat(report.get(1)).contains("1");
    }

    @Test
    @DisplayName("generateReport includes detail lines with student data")
    void testDetailLines() {
        List<StudentRecord> records = List.of(
                new StudentRecord(334500, "Test Name", "PHY", 12),
                new StudentRecord(112233, "Another Name", "CSC", 8));

        List<String> report = reportService.generateReport(records);

        // Detail lines start after 5 header lines
        String detailLine1 = report.get(5);
        assertThat(detailLine1).contains("334500");
        assertThat(detailLine1).contains("Test Name");
        assertThat(detailLine1).contains("PHY");
        assertThat(detailLine1).contains("12");

        String detailLine2 = report.get(6);
        assertThat(detailLine2).contains("112233");
        assertThat(detailLine2).contains("Another Name");
    }

    @Test
    @DisplayName("generateReport handles empty record list")
    void testEmptyRecords() {
        List<String> report = reportService.generateReport(List.of());

        // Should still have a page header
        assertThat(report).isNotEmpty();
        assertThat(report.get(0)).contains("Customer Order Report");
    }

    @Test
    @DisplayName("generateReport creates new page when exceeding max details per page")
    void testPageBreak() {
        // Create enough records to trigger a page break (>37 detail lines)
        StudentRecord[] records = new StudentRecord[40];
        for (int i = 0; i < 40; i++) {
            records[i] = new StudentRecord(100000 + i, "Student " + i, "CSC", i % 20);
        }

        List<String> report = reportService.generateReport(Arrays.asList(records));

        // Should contain two page headers
        long pageHeaderCount = report.stream()
                .filter(line -> line.contains("Customer Order Report"))
                .count();
        assertThat(pageHeaderCount).isEqualTo(2);
    }
}
