package com.cobolmigration.service;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Replaces report_writer/report_test.cbl.
 * Generates a "Customer Order Report" using text-based formatting instead of
 * COBOL's Report Writer (RD/REPORT SECTION).
 *
 * Report layout from COBOL:
 * - Page limit: 66 lines
 * - Heading at line 1
 * - First detail at line 6
 * - Last detail at line 42
 * - Footing at line 52
 */
@Service
public class ReportService {

    private static final int PAGE_LIMIT = 66;
    private static final int FIRST_DETAIL_LINE = 6;
    private static final int LAST_DETAIL_LINE = 42;

    /**
     * Represents a student/customer record for the report.
     * Maps to: f-test-record from report_test.cbl lines 24-28
     */
    public record ReportRecord(int studentId, String studentName, String major, int numCourses) {
    }

    /**
     * Generates a text-based report matching the COBOL Report Writer output.
     * Replaces: RD r-test-report and GENERATE report-line
     *
     * @param records the records to include in the report
     * @return formatted report as a string
     */
    public String generateReport(List<ReportRecord> records) {
        StringBuilder report = new StringBuilder();
        int currentLine = 0;
        int pageNumber = 1;
        boolean needsHeader = true;

        for (ReportRecord record : records) {
            if (needsHeader) {
                appendHeader(report, pageNumber);
                currentLine = FIRST_DETAIL_LINE;
                needsHeader = false;
            }

            if (currentLine > LAST_DETAIL_LINE) {
                // Start new page
                appendPageBreak(report);
                pageNumber++;
                appendHeader(report, pageNumber);
                currentLine = FIRST_DETAIL_LINE;
            }

            appendDetailLine(report, record);
            currentLine++;
        }

        return report.toString();
    }

    /**
     * Appends the report header.
     * Replaces: report-header TYPE REPORT HEADING (lines 48-57)
     */
    private void appendHeader(StringBuilder report, int pageNumber) {
        // Line 1: Report title at column 44
        report.append(String.format("%43s%-21s%36sPAGE %3d%n",
                "", "Customer Order Report", "", pageNumber));
        report.append(System.lineSeparator());
        // Column headers
        report.append(String.format("   %-6s   %-20s   %-3s  %-2s%n",
                "ID", "Student Name", "Maj", "NC"));
        report.append(String.format("   %-6s   %-20s   %-3s  %-2s%n",
                "------", "--------------------", "---", "--"));
        report.append(System.lineSeparator());
    }

    /**
     * Appends a detail line.
     * Replaces: report-line TYPE DETAIL LINE PLUS 1 (lines 59-63)
     */
    private void appendDetailLine(StringBuilder report, ReportRecord record) {
        report.append(String.format("   %06d   %-20s   %-3s  %02d%n",
                record.studentId(),
                record.studentName(),
                record.major(),
                record.numCourses()));
    }

    private void appendPageBreak(StringBuilder report) {
        report.append(System.lineSeparator());
        report.append("--- Page Break ---");
        report.append(System.lineSeparator());
        report.append(System.lineSeparator());
    }

    /**
     * Parses a fixed-width input line into a ReportRecord.
     * Input format: studentId(6) + studentName(20) + major(3) + numCourses(2)
     */
    public ReportRecord parseInputLine(String line) {
        if (line == null || line.length() < 31) {
            String padded = String.format("%-31s", line != null ? line : "");
            line = padded;
        }
        int studentId = Integer.parseInt(line.substring(0, 6).trim());
        String studentName = line.substring(6, 26);
        String major = line.substring(26, 29);
        int numCourses = Integer.parseInt(line.substring(29, 31).trim());
        return new ReportRecord(studentId, studentName, major, numCourses);
    }
}
