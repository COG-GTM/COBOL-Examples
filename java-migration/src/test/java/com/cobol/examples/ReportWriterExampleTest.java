package com.cobol.examples;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit and integration tests for ReportWriterExample.
 * Tests report generation and pagination to ensure it matches COBOL behavior.
 */
public class ReportWriterExampleTest {

    @BeforeEach
    public void setUp() throws IOException {
        createTestInputFile();
    }

    @AfterEach
    public void tearDown() {
        deleteFileIfExists("input.txt");
        deleteFileIfExists("report.txt");
    }

    @Test
    public void testReportGeneration() throws IOException {
        ReportWriterExample example = new ReportWriterExample();
        example.run();

        assertTrue(new File("report.txt").exists());
    }

    @Test
    public void testReportHeader() throws IOException {
        ReportWriterExample example = new ReportWriterExample();
        example.run();

        List<String> lines = Files.readAllLines(Paths.get("report.txt"));
        assertTrue(lines.size() > 0);

        boolean foundHeader = false;
        for (String line : lines) {
            if (line.contains("Customer Order Report")) {
                foundHeader = true;
                break;
            }
        }
        assertTrue(foundHeader, "Report should contain 'Customer Order Report' header");
    }

    @Test
    public void testPageNumber() throws IOException {
        ReportWriterExample example = new ReportWriterExample();
        example.run();

        List<String> lines = Files.readAllLines(Paths.get("report.txt"));

        boolean foundPageNumber = false;
        for (String line : lines) {
            if (line.contains("PAGE") && line.matches(".*PAGE\\s+\\d+.*")) {
                foundPageNumber = true;
                break;
            }
        }
        assertTrue(foundPageNumber, "Report should contain page number");
    }

    @Test
    public void testDetailLines() throws IOException {
        ReportWriterExample example = new ReportWriterExample();
        example.run();

        List<String> lines = Files.readAllLines(Paths.get("report.txt"));

        int detailLineCount = 0;
        for (String line : lines) {
            if (line.contains("3345") || line.contains("PHY")) {
                detailLineCount++;
            }
        }

        assertTrue(detailLineCount >= 7, "Report should contain detail lines for all input records");
    }

    @Test
    public void testReportLength() throws IOException {
        ReportWriterExample example = new ReportWriterExample();
        example.run();

        List<String> lines = Files.readAllLines(Paths.get("report.txt"));

        assertEquals(66, lines.size(), "Report should have exactly 66 lines (one page)");
    }

    @Test
    public void testEmptyInputFile() throws IOException {
        Files.write(Paths.get("input.txt"), new byte[0]);

        ReportWriterExample example = new ReportWriterExample();
        example.run();

        assertTrue(new File("report.txt").exists());
        List<String> lines = Files.readAllLines(Paths.get("report.txt"));
        assertEquals(66, lines.size(), "Report should still have 66 lines even with no data");
    }

    @Test
    public void testSingleRecord() throws IOException {
        List<String> inputLines = Arrays.asList("003345Test Name2          PHY12");
        Files.write(Paths.get("input.txt"), inputLines);

        ReportWriterExample example = new ReportWriterExample();
        example.run();

        List<String> lines = Files.readAllLines(Paths.get("report.txt"));
        assertTrue(lines.size() > 0);

        boolean foundRecord = false;
        for (String line : lines) {
            if (line.contains("3345") && line.contains("Test Name2")) {
                foundRecord = true;
                break;
            }
        }
        assertTrue(foundRecord, "Report should contain the single input record");
    }

    @Test
    public void testMultipleRecords() throws IOException {
        List<String> inputLines = Arrays.asList(
            "003345Test Name2          PHY12",
            "001234John Doe            CSC15",
            "005678Jane Smith          MAT10"
        );
        Files.write(Paths.get("input.txt"), inputLines);

        ReportWriterExample example = new ReportWriterExample();
        example.run();

        List<String> lines = Files.readAllLines(Paths.get("report.txt"));

        int recordCount = 0;
        if (lines.stream().anyMatch(line -> line.contains("3345"))) recordCount++;
        if (lines.stream().anyMatch(line -> line.contains("1234"))) recordCount++;
        if (lines.stream().anyMatch(line -> line.contains("5678"))) recordCount++;

        assertEquals(3, recordCount, "Report should contain all three input records");
    }

    @Test
    public void testPaginationWithManyRecords() throws IOException {
        List<String> inputLines = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            inputLines.add(String.format("%06dStudent Name %02d     CSC%02d", i, i, i % 20));
        }
        Files.write(Paths.get("input.txt"), inputLines);

        ReportWriterExample example = new ReportWriterExample();
        example.run();

        List<String> lines = Files.readAllLines(Paths.get("report.txt"));

        int pageCount = 0;
        for (String line : lines) {
            if (line.contains("PAGE")) {
                pageCount++;
            }
        }

        assertTrue(pageCount >= 2, "Report should have multiple pages for many records");
    }

    private void createTestInputFile() throws IOException {
        List<String> lines = Arrays.asList(
            "003345Test Name2          PHY12",
            "003345Test Name2          PHY12",
            "003345Test Name2          PHY12",
            "003345Test Name2          PHY12",
            "003345Test Name2          PHY12",
            "003345Test Name2          PHY12",
            "003345Test Name2          PHY12"
        );
        Files.write(Paths.get("input.txt"), lines);
    }

    private void deleteFileIfExists(String filename) {
        try {
            Files.deleteIfExists(Paths.get(filename));
        } catch (IOException e) {
        }
    }
}
