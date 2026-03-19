package com.cobolmigration.service;

import com.cobolmigration.model.StudentRecord;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service replacing the COBOL Report Writer (RD) from report_writer/report_test.cbl.
 *
 * <p>COBOL Report Writer mapping:
 * <ul>
 *   <li>RD r-test-report PAGE LIMIT IS 66 &rarr; {@code PAGE_LIMIT = 66}</li>
 *   <li>HEADING IS 1 &rarr; header at line 1</li>
 *   <li>FIRST DETAIL 6 &rarr; first detail line at position 6</li>
 *   <li>LAST DETAIL 42 &rarr; maximum detail lines per page</li>
 *   <li>FOOTING 52 &rarr; page footing position</li>
 *   <li>report-header TYPE REPORT HEADING: "Customer Order Report" at col 44,
 *       "PAGE" + page-counter at col 100</li>
 *   <li>report-line TYPE DETAIL: student-id at col 4, name at col 15,
 *       major at col 40, num-courses at col 46</li>
 * </ul>
 *
 * @see report_writer/report_test.cbl
 */
@Service
public class ReportService {

    private static final int PAGE_LIMIT = 66;
    private static final int FIRST_DETAIL_LINE = 6;
    private static final int LAST_DETAIL_LINE = 42;
    private static final String REPORT_TITLE = "Customer Order Report";

    /**
     * Generates a formatted text report from a list of student records.
     * Produces page headers with title and page numbers, and detail lines
     * with student ID, name, major, and number of courses.
     *
     * @param records the student records to include in the report
     * @return the formatted report as a list of lines
     */
    public List<String> generateReport(List<StudentRecord> records) {
        List<String> reportLines = new ArrayList<>();
        int pageNumber = 1;
        int detailLineCount = 0;
        int maxDetailsPerPage = LAST_DETAIL_LINE - FIRST_DETAIL_LINE + 1;

        // Generate first page header
        reportLines.addAll(generatePageHeader(pageNumber));

        for (StudentRecord record : records) {
            // Check if we need a new page
            if (detailLineCount >= maxDetailsPerPage) {
                // Pad remaining lines to reach page limit
                while (reportLines.size() % PAGE_LIMIT != 0) {
                    reportLines.add("");
                }
                pageNumber++;
                reportLines.addAll(generatePageHeader(pageNumber));
                detailLineCount = 0;
            }

            // Generate detail line (report_test.cbl lines 59-63)
            reportLines.add(formatDetailLine(record));
            detailLineCount++;
        }

        return reportLines;
    }

    /**
     * Generates the page header matching the COBOL report-header definition.
     * Line 1: "Customer Order Report" at column 44
     * Line 2: "PAGE" at column 100, page number at column 105
     * Lines 3-5: blank (spacing before first detail at line 6)
     */
    private List<String> generatePageHeader(int pageNumber) {
        List<String> header = new ArrayList<>();

        // Line 1: Report title at column 44 (report_test.cbl lines 48-50)
        header.add(String.format("%-43s%s", "", REPORT_TITLE));

        // Line 2: PAGE + number at column 100 (report_test.cbl lines 52-57)
        header.add(String.format("%-99s%s%3d", "", "PAGE", pageNumber));

        // Lines 3-5: blank lines before first detail
        header.add("");
        header.add("");
        header.add("");

        return header;
    }

    /**
     * Formats a single detail line matching the COBOL report-line definition.
     * Column layout (report_test.cbl lines 59-63):
     *   col 4: student-id (PIC 9(6))
     *   col 15: student-name (PIC X(20))
     *   col 40: major (PIC XXX)
     *   col 46: num-courses (PIC 99)
     */
    private String formatDetailLine(StudentRecord record) {
        return String.format("   %-6d          %-20s        %-3s  %02d",
                record.getStudentId(),
                record.getStudentName() != null ? record.getStudentName() : "",
                record.getMajor() != null ? record.getMajor() : "",
                record.getNumCourses());
    }
}
