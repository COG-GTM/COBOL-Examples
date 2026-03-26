package com.cobolmigration.service;

import com.cobolmigration.model.StudentRecord;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for generating formatted text reports.
 * Replaces the COBOL Report Writer (RD) from report_writer/report_test.cbl:
 *
 *   rd r-test-report
 *       page limit is 66
 *       heading is 1
 *       first detail 6
 *       last detail 42
 *       footing 52.
 *
 *   01 report-header type report heading.
 *       05 line 1 column 44 pic x(21) value "Customer Order Report".
 *       05 line 2. 10 column 100 pic x(4) value "PAGE".
 *                  10 column 105 pic zz9 source page-counter.
 *
 *   01 report-line type detail line plus 1.
 *       05 column 4  pic 9(6) source f-test-student-id.
 *       05 column 15 pic x(20) source f-test-student-name.
 *       05 column 40 pic xxx source f-test-major.
 *       05 column 46 pic 99 source f-test-num-courses.
 */
@Service
public class ReportService {

    private static final int PAGE_LIMIT = 66;
    private static final int HEADING_LINE = 1;
    private static final int FIRST_DETAIL_LINE = 6;
    private static final int LAST_DETAIL_LINE = 42;
    private static final int REPORT_WIDTH = 110;

    /**
     * Generate a formatted text report from student records.
     * Replicates the COBOL Report Writer layout with page headers and detail lines.
     */
    public String generateReport(List<StudentRecord> students) {
        StringBuilder report = new StringBuilder();
        int currentLine = 0;
        int pageCounter = 1;
        boolean needsHeader = true;

        for (StudentRecord student : students) {
            if (needsHeader || currentLine >= LAST_DETAIL_LINE) {
                if (currentLine > 0) {
                    // Pad remaining lines to fill the page
                    while (currentLine < PAGE_LIMIT) {
                        report.append("\n");
                        currentLine++;
                    }
                    pageCounter++;
                }
                currentLine = writeHeader(report, pageCounter);
                needsHeader = false;
            }

            // Pad lines until first detail line if needed
            while (currentLine < FIRST_DETAIL_LINE) {
                report.append("\n");
                currentLine++;
            }

            writeDetailLine(report, student);
            currentLine++;
        }

        return report.toString();
    }

    private int writeHeader(StringBuilder report, int pageCounter) {
        // Line 1: Report heading at column 44
        StringBuilder headerLine1 = new StringBuilder();
        for (int i = 0; i < 43; i++) headerLine1.append(' ');
        headerLine1.append("Customer Order Report");
        report.append(headerLine1);
        report.append("\n");

        // Line 2: PAGE counter at column 100
        StringBuilder headerLine2 = new StringBuilder();
        for (int i = 0; i < 99; i++) headerLine2.append(' ');
        headerLine2.append("PAGE");
        headerLine2.append(String.format("%3d", pageCounter));
        report.append(headerLine2);
        report.append("\n");

        return HEADING_LINE + 1;
    }

    private void writeDetailLine(StringBuilder report, StudentRecord student) {
        // Column 4: student id (6 digits)
        // Column 15: student name (20 chars)
        // Column 40: major (3 chars)
        // Column 46: num courses (2 digits)
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < REPORT_WIDTH; i++) line.append(' ');

        String id = String.format("%06d", student.getStudentId());
        replaceAt(line, 3, id);

        String name = String.format("%-20s", student.getStudentName());
        replaceAt(line, 14, name.substring(0, 20));

        String major = String.format("%-3s", student.getMajor());
        replaceAt(line, 39, major.substring(0, 3));

        String courses = String.format("%02d", student.getNumCourses());
        replaceAt(line, 45, courses);

        report.append(line.toString().stripTrailing());
        report.append("\n");
    }

    private void replaceAt(StringBuilder sb, int startIndex, String value) {
        for (int i = 0; i < value.length() && (startIndex + i) < sb.length(); i++) {
            sb.setCharAt(startIndex + i, value.charAt(i));
        }
    }
}
