package com.migration.reports;

import com.migration.database.Account;
import com.migration.database.AccountRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Phase 8: Report Generation
 *
 * Migrates COBOL Report Writer (report_writer/report_test.cbl) to Java.
 *
 * COBOL Report Writer features mapped:
 *
 * RD r-test-report PAGE LIMIT IS 66 HEADING IS 1 FIRST DETAIL 6
 *   -> Page size and layout constants
 *
 * report-header TYPE REPORT HEADING
 *   -> generateHeader() method
 *
 * report-line TYPE DETAIL LINE PLUS 1
 *   -> generateDetailLine() method
 *
 * PAGE-COUNTER
 *   -> pageCounter field, auto-incremented
 *
 * INITIATE r-test-report / GENERATE report-line / TERMINATE r-test-report
 *   -> generateReport() orchestrates the full flow
 *
 * The COBOL report reads from an input file (input.txt) with student records:
 *   f-test-student-id     PIC 9(6)
 *   f-test-student-name   PIC X(20)
 *   f-test-major          PIC XXX
 *   f-test-num-courses    PIC 99
 *
 * This service can generate reports from either file data or database records.
 * Output formats: plain text (matching COBOL output), CSV.
 */
@Service
public class ReportService {

    private static final int PAGE_LIMIT = 66;
    private static final int FIRST_DETAIL_LINE = 6;
    private static final int LAST_DETAIL_LINE = 42;
    private static final int REPORT_WIDTH = 110;

    private final AccountRepository accountRepository;

    public ReportService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Student record matching the COBOL input file format.
     */
    public record StudentRecord(
            int studentId,
            String studentName,
            String major,
            int numCourses
    ) {
        public static StudentRecord fromFixedWidth(String line) {
            if (line.length() < 31) {
                line = String.format("%-31s", line);
            }
            int id = Integer.parseInt(line.substring(0, 6).strip());
            String name = line.substring(6, 26);
            String maj = line.substring(26, 29);
            int courses = Integer.parseInt(line.substring(29, 31).strip());
            return new StudentRecord(id, name, maj, courses);
        }
    }

    /**
     * Generates a plain-text report from a student input file.
     * This directly mirrors the COBOL report_test.cbl flow:
     *   OPEN INPUT fd-test-input-file OUTPUT fd-report-file
     *   INITIATE r-test-report
     *   PERFORM UNTIL ws-eof READ ... GENERATE report-line END-PERFORM
     *   TERMINATE r-test-report
     */
    public String generateStudentReport(Path inputFile) throws IOException {
        List<StudentRecord> records = new ArrayList<>();
        try (Stream<String> lines = Files.lines(inputFile)) {
            lines.filter(line -> !line.isBlank())
                    .forEach(line -> records.add(StudentRecord.fromFixedWidth(line)));
        }
        return generateStudentReportFromRecords(records);
    }

    /**
     * Generates a plain-text student report from a list of records.
     */
    public String generateStudentReportFromRecords(List<StudentRecord> records) {
        StringBuilder report = new StringBuilder();
        int pageCounter = 1;
        int lineCounter = 0;

        // Report header (COBOL: report-header TYPE REPORT HEADING)
        report.append(generateStudentReportHeader(pageCounter));
        lineCounter = FIRST_DETAIL_LINE;

        for (StudentRecord record : records) {
            if (lineCounter >= LAST_DETAIL_LINE) {
                // Page break
                report.append("\n");
                pageCounter++;
                report.append(generateStudentReportHeader(pageCounter));
                lineCounter = FIRST_DETAIL_LINE;
            }

            // Detail line (COBOL: report-line TYPE DETAIL LINE PLUS 1)
            report.append(String.format("   %-6d      %-20s   %-3s  %2d%n",
                    record.studentId(),
                    record.studentName(),
                    record.major(),
                    record.numCourses()));
            lineCounter++;
        }

        return report.toString();
    }

    private String generateStudentReportHeader(int pageCounter) {
        StringBuilder header = new StringBuilder();
        // Line 1: Report title (COBOL: column 44 PIC X(21) VALUE "Customer Order Report")
        header.append(String.format("%43s%-21s%n", "", "Customer Order Report"));
        // Line 2: Page number (COBOL: column 100 "PAGE" + column 105 page-counter)
        header.append(String.format("%99sPAGE %3d%n", "", pageCounter));
        // Blank lines before first detail
        header.append("\n\n\n");
        // Column headers
        header.append(String.format("   %-10s  %-20s   %-5s %s%n",
                "ID", "Name", "Major", "Courses"));
        header.append(String.format("   %-10s  %-20s   %-5s %s%n",
                "------", "--------------------", "-----", "-------"));
        return header.toString();
    }

    /**
     * Generates a plain-text account report from database records.
     * Uses the same report structure as the COBOL display-account-results paragraph.
     */
    public String generateAccountReport() {
        List<Account> accounts = accountRepository.findAllByOrderByIdAsc();
        StringBuilder report = new StringBuilder();

        report.append("\nACCOUNTS:\n\n");
        report.append(String.format(" %-5s | %-8s | %-8s | %-10s | %-22s | %-7s%n",
                "ID", "First", "Last", "Phone", "Address", "Enabled"));
        report.append(String.format("-------|----------|----------|------------|" +
                "------------------------|---------%n"));

        for (Account account : accounts) {
            report.append(String.format(" %-5d | %-8s | %-8s | %-10s | %-22s | %-7s%n",
                    account.getId(),
                    account.getFirstName(),
                    account.getLastName(),
                    account.getPhone(),
                    account.getAddress(),
                    account.getIsEnabled()));
        }

        return report.toString();
    }

    /**
     * Generates a CSV account report.
     */
    public String generateAccountReportCsv() {
        List<Account> accounts = accountRepository.findAllByOrderByIdAsc();
        StringBuilder csv = new StringBuilder();
        csv.append("ID,First Name,Last Name,Phone,Address,Enabled,Created,Modified\n");

        for (Account account : accounts) {
            csv.append(String.format("%d,\"%s\",\"%s\",\"%s\",\"%s\",%s,%s,%s%n",
                    account.getId(),
                    account.getFirstName(),
                    account.getLastName(),
                    account.getPhone(),
                    account.getAddress(),
                    account.getIsEnabled(),
                    account.getCreateDt(),
                    account.getModDt()));
        }

        return csv.toString();
    }
}
