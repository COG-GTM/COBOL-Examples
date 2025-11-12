package com.cobol.examples;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests that verify Java implementations produce output
 * matching the COBOL programs' behavior.
 */
public class IntegrationTest {

    @AfterEach
    public void cleanup() {
        deleteFileIfExists("test-file-1.txt");
        deleteFileIfExists("test-file-2.txt");
        deleteFileIfExists("merge-output.txt");
        deleteFileIfExists("sorted-contract-id.txt");
        deleteFileIfExists("work-temp.txt");
        deleteFileIfExists("input.txt");
        deleteFileIfExists("report.txt");
    }

    @Test
    public void testMergeSortCompleteWorkflow() throws IOException {
        MergeSortExample example = new MergeSortExample();
        example.run();

        assertTrue(new File("test-file-1.txt").exists(), "Test file 1 should be created");
        assertTrue(new File("test-file-2.txt").exists(), "Test file 2 should be created");
        assertTrue(new File("merge-output.txt").exists(), "Merge output should be created");
        assertTrue(new File("sorted-contract-id.txt").exists(), "Sorted output should be created");

        List<String> mergedLines = Files.readAllLines(Paths.get("merge-output.txt"));
        assertEquals(11, mergedLines.size(), "Merged file should contain 11 records");

        List<CustomerRecord> mergedRecords = new ArrayList<>();
        for (String line : mergedLines) {
            mergedRecords.add(CustomerRecord.fromString(line));
        }

        for (int i = 0; i < mergedRecords.size() - 1; i++) {
            assertTrue(mergedRecords.get(i).getCustomerId() <= mergedRecords.get(i + 1).getCustomerId(),
                "Merged records should be sorted by customer ID ascending");
        }

        List<String> sortedLines = Files.readAllLines(Paths.get("sorted-contract-id.txt"));
        assertEquals(11, sortedLines.size(), "Sorted file should contain 11 records");

        List<CustomerRecord> sortedRecords = new ArrayList<>();
        for (String line : sortedLines) {
            sortedRecords.add(CustomerRecord.fromString(line));
        }

        for (int i = 0; i < sortedRecords.size() - 1; i++) {
            assertTrue(sortedRecords.get(i).getContractId() >= sortedRecords.get(i + 1).getContractId(),
                "Sorted records should be sorted by contract ID descending");
        }
    }

    @Test
    public void testMergeSortDataIntegrity() throws IOException {
        MergeSortExample example = new MergeSortExample();
        example.run();

        List<String> file1Lines = Files.readAllLines(Paths.get("test-file-1.txt"));
        List<String> file2Lines = Files.readAllLines(Paths.get("test-file-2.txt"));
        List<String> mergedLines = Files.readAllLines(Paths.get("merge-output.txt"));

        assertEquals(file1Lines.size() + file2Lines.size(), mergedLines.size(),
            "Merged file should contain all records from both input files");

        Set<Integer> inputIds = new HashSet<>();
        for (String line : file1Lines) {
            inputIds.add(CustomerRecord.fromString(line).getCustomerId());
        }
        for (String line : file2Lines) {
            inputIds.add(CustomerRecord.fromString(line).getCustomerId());
        }

        Set<Integer> outputIds = new HashSet<>();
        for (String line : mergedLines) {
            outputIds.add(CustomerRecord.fromString(line).getCustomerId());
        }

        assertEquals(inputIds, outputIds, "All input customer IDs should be present in output");
    }

    @Test
    public void testCommandLineArgsWithVariousInputs() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            String[] args = {"first", "second", "third"};
            CommandLineArgsExample.main(args);

            String output = outContent.toString();
            String[] lines = output.split(System.lineSeparator());

            assertEquals(3, lines.length, "Should output 3 lines for 3 arguments");
            assertEquals("first", lines[0]);
            assertEquals("second", lines[1]);
            assertEquals("third", lines[2]);
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void testCommandLineArgsWithComplexStrings() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            String[] args = {"path/to/file.txt", "key=value", "test@example.com"};
            CommandLineArgsExample.main(args);

            String output = outContent.toString();
            String[] lines = output.split(System.lineSeparator());

            assertEquals(3, lines.length);
            assertEquals("path/to/file.txt", lines[0]);
            assertEquals("key=value", lines[1]);
            assertEquals("test@example.com", lines[2]);
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void testReportWriterCompleteWorkflow() throws IOException {
        List<String> inputLines = Arrays.asList(
            "003345Test Name2          PHY12",
            "001234John Doe            CSC15",
            "005678Jane Smith          MAT10",
            "009999Bob Johnson         ENG08"
        );
        Files.write(Paths.get("input.txt"), inputLines);

        ReportWriterExample example = new ReportWriterExample();
        example.run();

        assertTrue(new File("report.txt").exists(), "Report file should be created");

        List<String> reportLines = Files.readAllLines(Paths.get("report.txt"));
        assertEquals(66, reportLines.size(), "Report should have exactly 66 lines (one page)");

        boolean hasHeader = reportLines.stream().anyMatch(line -> line.contains("Customer Order Report"));
        assertTrue(hasHeader, "Report should contain header");

        boolean hasPageNumber = reportLines.stream().anyMatch(line -> line.contains("PAGE"));
        assertTrue(hasPageNumber, "Report should contain page number");

        int detailCount = 0;
        for (String line : reportLines) {
            if (line.contains("3345") || line.contains("1234") || 
                line.contains("5678") || line.contains("9999")) {
                detailCount++;
            }
        }
        assertEquals(4, detailCount, "Report should contain all 4 detail records");
    }

    @Test
    public void testReportWriterPagination() throws IOException {
        List<String> inputLines = new ArrayList<>();
        for (int i = 1; i <= 40; i++) {
            String studentName = String.format("Student %03d", i);
            inputLines.add(String.format("%06d%-20s%-3s%02d", 
                i * 100, studentName, "CSC", (i % 20) + 1));
        }
        Files.write(Paths.get("input.txt"), inputLines);

        ReportWriterExample example = new ReportWriterExample();
        example.run();

        List<String> reportLines = Files.readAllLines(Paths.get("report.txt"));

        int pageCount = 0;
        for (String line : reportLines) {
            if (line.contains("PAGE")) {
                pageCount++;
            }
        }

        assertTrue(pageCount >= 1, "Report should have at least one page");
        assertTrue(reportLines.size() >= 66, "Report should have at least one full page");
        
        boolean hasStudentData = false;
        for (String line : reportLines) {
            if (line.contains("Student")) {
                hasStudentData = true;
                break;
            }
        }
        assertTrue(hasStudentData, "Report should contain student data");
    }

    @Test
    public void testEndToEndDataConsistency() throws IOException {
        MergeSortExample mergeSortExample = new MergeSortExample();
        mergeSortExample.run();

        List<String> sortedLines = Files.readAllLines(Paths.get("sorted-contract-id.txt"));
        List<CustomerRecord> records = new ArrayList<>();
        for (String line : sortedLines) {
            records.add(CustomerRecord.fromString(line));
        }

        CustomerRecord highestContractRecord = records.get(0);
        assertEquals(12323, highestContractRecord.getContractId(), 
            "Highest contract ID should be 12323");
        assertEquals(5, highestContractRecord.getCustomerId(),
            "Customer with highest contract ID should be customer 5");

        CustomerRecord lowestContractRecord = records.get(records.size() - 1);
        assertEquals(247, lowestContractRecord.getContractId(),
            "Lowest contract ID should be 247");
        assertEquals(24, lowestContractRecord.getCustomerId(),
            "Customer with lowest contract ID should be customer 24");
    }

    @Test
    public void testEmptyFileHandling() throws IOException {
        Files.write(Paths.get("input.txt"), new byte[0]);

        ReportWriterExample example = new ReportWriterExample();
        example.run();

        assertTrue(new File("report.txt").exists(), "Report should be created even with empty input");
        List<String> reportLines = Files.readAllLines(Paths.get("report.txt"));
        assertEquals(66, reportLines.size(), "Report should still have 66 lines with empty input");
    }

    @Test
    public void testRecordFormatConsistency() throws IOException {
        CustomerRecord record = new CustomerRecord(12345, "TestLast", "TestFirst", 99999, "TestComment");
        String formatted = record.toString();

        assertEquals(135, formatted.length(), "Formatted record should be exactly 135 characters");

        CustomerRecord parsed = CustomerRecord.fromString(formatted);
        assertEquals(record.getCustomerId(), parsed.getCustomerId());
        assertEquals(record.getLastName(), parsed.getLastName());
        assertEquals(record.getFirstName(), parsed.getFirstName());
        assertEquals(record.getContractId(), parsed.getContractId());
        assertEquals(record.getComment(), parsed.getComment());
    }

    private void deleteFileIfExists(String filename) {
        try {
            Files.deleteIfExists(Paths.get(filename));
        } catch (IOException e) {
        }
    }
}
