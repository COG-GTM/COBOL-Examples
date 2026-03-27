package com.example.migration.service;

import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service replacing the COBOL Report Writer from report_writer/report_test.cbl.
 *
 * COBOL Report Writer directives replaced:
 *   RD r-test-report PAGE LIMIT IS 66 HEADING IS 1 FIRST DETAIL 6
 *       LAST DETAIL 42 FOOTING 52
 *   (report_test.cbl lines 41-46)
 *
 *   Report header: "Customer Order Report" at line 1, column 44
 *                  "PAGE" + page-counter at line 2, columns 100-107
 *   (report_test.cbl lines 48-57)
 *
 *   Detail line: student-id(col 4), student-name(col 15), major(col 40), num-courses(col 46)
 *   (report_test.cbl lines 59-63)
 *
 * Input record format (from fd-test-input-file):
 *   f-test-student-id    pic 9(6)
 *   f-test-student-name  pic x(20)
 *   f-test-major         pic xxx
 *   f-test-num-courses   pic 99
 */
@Service
public class ReportService {

    private static final int PAGE_LIMIT = 66;
    private static final int FIRST_DETAIL_LINE = 6;
    private static final int LAST_DETAIL_LINE = 42;
    private static final int DETAILS_PER_PAGE = LAST_DETAIL_LINE - FIRST_DETAIL_LINE + 1;

    /**
     * Generates a "Customer Order Report" from student records.
     * Replaces the COBOL INITIATE/GENERATE/TERMINATE cycle
     * (report_test.cbl lines 75-87).
     */
    public String generateReport(List<StudentRecord> records) {
        StringBuilder report = new StringBuilder();
        int pageNumber = 1;
        int detailCount = 0;

        report.append(generatePageHeader(pageNumber));

        for (StudentRecord record : records) {
            if (detailCount >= DETAILS_PER_PAGE) {
                report.append("\n");
                pageNumber++;
                detailCount = 0;
                report.append(generatePageHeader(pageNumber));
            }

            report.append(generateDetailLine(record));
            report.append("\n");
            detailCount++;
        }

        return report.toString();
    }

    private String generatePageHeader(int pageNumber) {
        StringBuilder header = new StringBuilder();
        header.append(String.format("%43s%s%n", "", "Customer Order Report"));
        header.append(String.format("%99s%s%3d%n", "", "PAGE", pageNumber));
        header.append("\n");
        header.append("\n");
        header.append("\n");
        return header.toString();
    }

    private String generateDetailLine(StudentRecord record) {
        return String.format("   %06d      %-20s    %-3s %02d",
                record.studentId(),
                record.studentName(),
                record.major(),
                record.numCourses());
    }

    /**
     * Record replacing COBOL input file record (report_test.cbl lines 24-28):
     *   f-test-student-id    pic 9(6)
     *   f-test-student-name  pic x(20)
     *   f-test-major         pic xxx
     *   f-test-num-courses   pic 99
     */
    public record StudentRecord(int studentId, String studentName, String major, int numCourses) {
    }
}
