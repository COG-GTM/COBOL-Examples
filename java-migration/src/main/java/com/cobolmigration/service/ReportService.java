package com.cobolmigration.service;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for generating formatted text reports.
 *
 * Migrated from: report_writer/report_test.cbl
 *
 * The original COBOL report writer defines:
 *   - Page limit: 66 lines
 *   - Heading at line 1
 *   - First detail at line 6
 *   - Last detail at line 42
 *   - Footing at line 52
 *   - Report header: "Customer Order Report" at column 44, with PAGE counter at column 100
 *   - Detail line: student-id (col 4), student-name (col 15), major (col 40), num-courses (col 46)
 */
@Service
public class ReportService {

    private static final int PAGE_LIMIT = 66;
    private static final int FIRST_DETAIL_LINE = 6;
    private static final int LAST_DETAIL_LINE = 42;
    private static final int REPORT_WIDTH = 110;

    /**
     * Represents a single detail record in the report.
     * Matches the COBOL FD:
     *   05  f-test-student-id      PIC 9(6).
     *   05  f-test-student-name    PIC X(20).
     *   05  f-test-major           PIC XXX.
     *   05  f-test-num-courses     PIC 99.
     */
    public record ReportRecord(int studentId, String studentName, String major, int numCourses) {
    }

    /**
     * Generates a formatted report matching the COBOL report writer layout.
     *
     * @param records the list of records to include in the report
     * @return the formatted report as a string
     */
    public String generateReport(List<ReportRecord> records) {
        StringBuilder report = new StringBuilder();
        int currentLine = 0;
        int pageNumber = 0;
        int recordIndex = 0;

        while (recordIndex < records.size()) {
            // Start new page
            pageNumber++;
            currentLine = 1;

            // Page header (line 1)
            report.append(formatPageHeader(pageNumber));
            report.append(System.lineSeparator());
            currentLine++;

            // Page number line (line 2)
            report.append(formatPageNumberLine(pageNumber));
            report.append(System.lineSeparator());
            currentLine++;

            // Blank lines until first detail line
            while (currentLine < FIRST_DETAIL_LINE) {
                report.append(System.lineSeparator());
                currentLine++;
            }

            // Detail lines (from FIRST_DETAIL_LINE to LAST_DETAIL_LINE)
            while (currentLine <= LAST_DETAIL_LINE && recordIndex < records.size()) {
                ReportRecord record = records.get(recordIndex);
                report.append(formatDetailLine(record));
                report.append(System.lineSeparator());
                currentLine++;
                recordIndex++;
            }

            // Fill remaining lines to page limit
            while (currentLine <= PAGE_LIMIT) {
                report.append(System.lineSeparator());
                currentLine++;
            }
        }

        // Handle empty record list
        if (records.isEmpty()) {
            pageNumber = 1;
            report.append(formatPageHeader(pageNumber));
            report.append(System.lineSeparator());
            report.append(formatPageNumberLine(pageNumber));
            report.append(System.lineSeparator());
        }

        return report.toString();
    }

    /**
     * Formats the page header line.
     * COBOL: 05 line 1 column 44 pic x(21) value "Customer Order Report".
     */
    private String formatPageHeader(int pageNumber) {
        StringBuilder line = new StringBuilder();
        // Pad to column 44 (0-indexed: 43 spaces)
        line.append(" ".repeat(43));
        line.append("Customer Order Report");
        return line.toString();
    }

    /**
     * Formats the page number line.
     * COBOL: 05 line 2. 10 column 100 pic x(4) value "PAGE". 10 column 105 pic zz9 source page-counter.
     */
    private String formatPageNumberLine(int pageNumber) {
        StringBuilder line = new StringBuilder();
        // Pad to column 100 (0-indexed: 99 spaces)
        line.append(" ".repeat(99));
        line.append("PAGE");
        // Page counter at column 105, formatted as zz9 (3 chars, leading zeros suppressed)
        line.append(String.format("%3d", pageNumber));
        return line.toString();
    }

    /**
     * Formats a detail line.
     * COBOL:
     *   05 column 4  pic 9(6) source f-test-student-id.
     *   05 column 15 pic x(20) source f-test-student-name.
     *   05 column 40 pic xxx source f-test-major.
     *   05 column 46 pic 99 source f-test-num-courses.
     */
    private String formatDetailLine(ReportRecord record) {
        StringBuilder line = new StringBuilder();
        // Pad to column 4 (0-indexed: 3 spaces)
        line.append("   ");
        // Student ID: 6 digits, zero-padded
        line.append(String.format("%06d", record.studentId()));
        // Pad to column 15 (currently at position 9, need 5 more spaces)
        while (line.length() < 14) {
            line.append(' ');
        }
        // Student name: 20 chars, left-aligned
        line.append(String.format("%-20s", record.studentName()));
        // Pad to column 40 (currently at position 34, need spaces)
        while (line.length() < 39) {
            line.append(' ');
        }
        // Major: 3 chars
        line.append(String.format("%-3s", record.major()));
        // Pad to column 46
        while (line.length() < 45) {
            line.append(' ');
        }
        // Num courses: 2 digits
        line.append(String.format("%02d", record.numCourses()));
        return line.toString();
    }
}
