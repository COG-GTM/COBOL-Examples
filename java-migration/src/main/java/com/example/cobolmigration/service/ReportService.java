package com.example.cobolmigration.service;

import com.example.cobolmigration.model.StudentRecord;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Spring Service mapping report_writer/report_test.cbl.
 * Generates a formatted plain-text report with page headers, detail lines, and page breaks.
 *
 * COBOL report layout:
 *   Page limit: 66 lines
 *   Heading at line 1
 *   First detail at line 6
 *   Last detail at line 42
 *   Footing at line 52
 *
 * Detail line columns:
 *   studentId at col 4, studentName at col 15, major at col 40, numCourses at col 46
 */
@Service
public class ReportService {

    private static final int PAGE_LIMIT = 66;
    private static final int FIRST_DETAIL_LINE = 6;
    private static final int LAST_DETAIL_LINE = 42;
    private static final String REPORT_TITLE = "Customer Order Report";

    /**
     * Generates a formatted plain-text report from a list of student records.
     */
    public String generateReport(List<StudentRecord> students) {
        StringBuilder sb = new StringBuilder();
        int pageNumber = 1;
        int currentLine = 0;
        int detailCount = 0;
        boolean needHeader = true;

        for (StudentRecord student : students) {
            if (needHeader) {
                appendPageHeader(sb, pageNumber);
                currentLine = FIRST_DETAIL_LINE;
                needHeader = false;
            }

            appendDetailLine(sb, student);
            currentLine++;
            detailCount++;

            if (currentLine >= LAST_DETAIL_LINE) {
                // Fill remaining lines to reach page limit
                int remaining = PAGE_LIMIT - currentLine;
                for (int i = 0; i < remaining; i++) {
                    sb.append("\n");
                }
                pageNumber++;
                needHeader = true;
            }
        }

        return sb.toString();
    }

    private void appendPageHeader(StringBuilder sb, int pageNumber) {
        // Line 1: title at column 44
        sb.append(padLeft(REPORT_TITLE, 64));
        sb.append("\n");
        // Line 2: PAGE at column 100, page number at column 105
        sb.append(padLeft("PAGE", 103));
        sb.append(String.format(" %3d", pageNumber));
        sb.append("\n");
        // Lines 3-5: blank lines before first detail
        sb.append("\n\n\n");
    }

    private void appendDetailLine(StringBuilder sb, StudentRecord student) {
        // Col 4: studentId (6 digits), Col 15: studentName (20 chars),
        // Col 40: major (3 chars), Col 46: numCourses (2 digits)
        StringBuilder line = new StringBuilder();
        line.append("   "); // 3 spaces to reach col 4
        line.append(String.format("%06d", student.getStudentId()));
        line.append("     "); // spaces to reach col 15
        line.append(String.format("%-25s", student.getStudentName()));
        line.append(String.format("%-6s", student.getMajor()));
        line.append(String.format("%02d", student.getNumCourses()));
        sb.append(line);
        sb.append("\n");
    }

    private String padLeft(String s, int totalWidth) {
        if (s.length() >= totalWidth) {
            return s;
        }
        return " ".repeat(totalWidth - s.length()) + s;
    }
}
