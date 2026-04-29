package com.example.cobolmigration.service;

import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service replacing the COBOL Report Writer (report_writer/report_test.cbl).
 *
 * The COBOL report layout uses RD with PAGE LIMIT 66, HEADING 1,
 * FIRST DETAIL 6, LAST DETAIL 42, FOOTING 52.  The report header is
 * "Customer Order Report" with a page counter, and each detail line
 * contains: student ID, student name, major, and number of courses.
 *
 * This service generates the report as CSV text; callers can use the
 * output in REST responses or write it to a file.
 */
@Service
public class ReportService {

    /**
     * Simple POJO mirroring the COBOL fd-test-input-file record
     * (report_test.cbl lines 24-28).
     */
    public static class StudentRecord {
        private final int studentId;
        private final String studentName;
        private final String major;
        private final int numCourses;

        public StudentRecord(int studentId, String studentName,
                             String major, int numCourses) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.major = major;
            this.numCourses = numCourses;
        }

        public int getStudentId() {
            return studentId;
        }

        public String getStudentName() {
            return studentName;
        }

        public String getMajor() {
            return major;
        }

        public int getNumCourses() {
            return numCourses;
        }
    }

    /**
     * Generates a plain-text report matching the COBOL Report Writer output.
     *
     * Layout mirrors the RD declaratives:
     *   - Header line: "Customer Order Report" right-aligned with page counter
     *   - Detail lines: studentId, studentName, major, numCourses
     */
    public String generateTextReport(List<StudentRecord> records) {
        StringBuilder sb = new StringBuilder();
        int pageSize = 36; // LAST DETAIL (42) - FIRST DETAIL (6)
        int page = 1;
        int lineCount = 0;

        appendHeader(sb, page);

        for (StudentRecord record : records) {
            if (lineCount >= pageSize) {
                page++;
                sb.append("\n");
                appendHeader(sb, page);
                lineCount = 0;
            }
            sb.append(String.format("    %-6d    %-20s    %-3s  %2d%n",
                    record.getStudentId(),
                    record.getStudentName(),
                    record.getMajor(),
                    record.getNumCourses()));
            lineCount++;
        }

        return sb.toString();
    }

    /**
     * Generates a CSV report for programmatic consumption.
     */
    public String generateCsvReport(List<StudentRecord> records) {
        StringBuilder sb = new StringBuilder();
        sb.append("Student ID,Student Name,Major,Num Courses\n");
        for (StudentRecord record : records) {
            sb.append(String.format("%d,%s,%s,%d%n",
                    record.getStudentId(),
                    record.getStudentName(),
                    record.getMajor(),
                    record.getNumCourses()));
        }
        return sb.toString();
    }

    private void appendHeader(StringBuilder sb, int page) {
        sb.append(String.format(
                "                                           Customer Order Report"
              + "                                    PAGE %3d%n", page));
        sb.append("\n\n\n\n");
    }
}
