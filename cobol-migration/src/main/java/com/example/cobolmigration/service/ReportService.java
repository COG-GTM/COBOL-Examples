package com.example.cobolmigration.service;

import com.example.cobolmigration.model.StudentRecord;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Replaces report_writer/report_test.cbl.
 * Generates a formatted text report matching the COBOL report layout:
 *   - Header: "Customer Order Report" (line 50)
 *   - Page numbers (lines 53-57)
 *   - Detail lines with student data (lines 59-63)
 *   - Page limit of 66 lines, first detail at line 6, last detail at line 42
 */
@Service
public class ReportService {

    private static final int PAGE_LIMIT = 66;
    private static final int FIRST_DETAIL_LINE = 6;
    private static final int LAST_DETAIL_LINE = 42;
    private static final int DETAILS_PER_PAGE = LAST_DETAIL_LINE - FIRST_DETAIL_LINE + 1;

    /**
     * Generates a formatted text report from student records.
     * Matches the COBOL report writer output format from report_test.cbl.
     */
    public String generateReport(List<StudentRecord> records) {
        StringBuilder report = new StringBuilder();
        int pageNumber = 1;
        int detailLineCount = 0;

        appendHeader(report, pageNumber);

        for (StudentRecord record : records) {
            if (detailLineCount >= DETAILS_PER_PAGE) {
                appendPageBreak(report);
                pageNumber++;
                detailLineCount = 0;
                appendHeader(report, pageNumber);
            }

            appendDetailLine(report, record);
            detailLineCount++;
        }

        return report.toString();
    }

    private void appendHeader(StringBuilder report, int pageNumber) {
        // Header at line 1, column 44: "Customer Order Report"
        report.append(String.format("%43s%-21s", "", "Customer Order Report"));
        // Page number at column 100
        report.append(String.format("%35sPAGE %3d", "", pageNumber));
        report.append(System.lineSeparator());
        report.append(System.lineSeparator());
        // Blank lines until first detail line (lines 2-5)
        report.append(String.format("   %-6s   %-20s   %-3s  %-2s",
                "ID", "Name", "Mjr", "NC"));
        report.append(System.lineSeparator());
        report.append("   ------   --------------------   ---  --");
        report.append(System.lineSeparator());
    }

    private void appendDetailLine(StringBuilder report, StudentRecord record) {
        // Columns from COBOL: col 4=id(6), col 15=name(20), col 40=major(3), col 46=courses(2)
        report.append(String.format("   %06d   %-20s   %-3s  %02d",
                record.getStudentId(),
                record.getStudentName(),
                record.getMajor(),
                record.getNumCourses()));
        report.append(System.lineSeparator());
    }

    private void appendPageBreak(StringBuilder report) {
        report.append(System.lineSeparator());
        report.append("--- Page Break ---");
        report.append(System.lineSeparator());
        report.append(System.lineSeparator());
    }
}
