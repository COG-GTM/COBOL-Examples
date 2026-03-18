package com.migration.reports;

import com.migration.database.Account;
import com.migration.database.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Phase 8: Report Generation Tests
 *
 * Test cases mirror the COBOL report_writer/report_test.cbl:
 * - Student report generation with headers and detail lines
 * - Account report in text and CSV formats
 */
@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private ReportService reportService;

    @Test
    @DisplayName("Generate student report from records (COBOL: GENERATE report-line)")
    void generateStudentReport() {
        List<ReportService.StudentRecord> records = List.of(
                new ReportService.StudentRecord(3345, "Test Name", "PHY", 12),
                new ReportService.StudentRecord(1234, "Jane Smith", "CSC", 8),
                new ReportService.StudentRecord(5678, "Bob Johnson", "MAT", 15)
        );

        String report = reportService.generateStudentReportFromRecords(records);

        // Report header present (COBOL: report-header TYPE REPORT HEADING)
        assertTrue(report.contains("Customer Order Report"));
        // Page counter present
        assertTrue(report.contains("PAGE"));
        assertTrue(report.contains("1"));
        // Detail lines present
        assertTrue(report.contains("3345"));
        assertTrue(report.contains("Test Name"));
        assertTrue(report.contains("PHY"));
        assertTrue(report.contains("Jane Smith"));
        assertTrue(report.contains("CSC"));
    }

    @Test
    @DisplayName("Student record from fixed-width line")
    void studentRecordFromFixedWidth() {
        // COBOL: f-test-student-id PIC 9(6), name PIC X(20), major PIC XXX, courses PIC 99
        String line = "003345Test Name           PHY12";
        ReportService.StudentRecord record = ReportService.StudentRecord.fromFixedWidth(line);

        assertEquals(3345, record.studentId());
        assertTrue(record.studentName().contains("Test Name"));
        assertEquals("PHY", record.major());
        assertEquals(12, record.numCourses());
    }

    @Test
    @DisplayName("Generate account text report (COBOL: display-account-results)")
    void generateAccountTextReport() {
        when(accountRepository.findAllByOrderByIdAsc()).thenReturn(createTestAccounts());

        String report = reportService.generateAccountReport();

        // Header
        assertTrue(report.contains("ACCOUNTS:"));
        assertTrue(report.contains("ID"));
        assertTrue(report.contains("First"));
        assertTrue(report.contains("Last"));
        assertTrue(report.contains("Phone"));
        assertTrue(report.contains("Address"));
        assertTrue(report.contains("Enabled"));
        // Data rows
        assertTrue(report.contains("John"));
        assertTrue(report.contains("Tester"));
        assertTrue(report.contains("Bob"));
    }

    @Test
    @DisplayName("Generate account CSV report")
    void generateAccountCsvReport() {
        when(accountRepository.findAllByOrderByIdAsc()).thenReturn(createTestAccounts());

        String csv = reportService.generateAccountReportCsv();

        // CSV header
        assertTrue(csv.contains("ID,First Name,Last Name,Phone,Address,Enabled,Created,Modified"));
        // Data rows
        assertTrue(csv.contains("\"John\""));
        assertTrue(csv.contains("\"Tester\""));
        assertTrue(csv.contains("\"Bob\""));
    }

    @Test
    @DisplayName("Report handles page breaks for many records")
    void reportPageBreaks() {
        // Create more records than fit on one page (LAST_DETAIL = 42, FIRST_DETAIL = 6 => 36 per page)
        List<ReportService.StudentRecord> manyRecords = new java.util.ArrayList<>();
        for (int i = 0; i < 50; i++) {
            manyRecords.add(new ReportService.StudentRecord(i, "Student " + i, "CSC", i % 20));
        }

        String report = reportService.generateStudentReportFromRecords(manyRecords);

        // Should have multiple pages
        assertTrue(report.contains("PAGE   1"));
        assertTrue(report.contains("PAGE   2"));
    }

    private List<Account> createTestAccounts() {
        Account a1 = new Account("John", "Tester", "15555550100", "123 Fake St", "Y");
        a1.setId(1);
        a1.setCreateDt(LocalDateTime.of(2022, 1, 1, 0, 0));
        a1.setModDt(LocalDateTime.of(2022, 1, 1, 0, 0));

        Account a2 = new Account("Bob", "Tester4", "15555550154", "119 Truck St", "N");
        a2.setId(5);
        a2.setCreateDt(LocalDateTime.of(2022, 1, 1, 0, 0));
        a2.setModDt(LocalDateTime.of(2022, 1, 1, 0, 0));

        return List.of(a1, a2);
    }
}
