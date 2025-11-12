package com.cobol.examples;

import java.io.*;
import java.util.*;

/**
 * Java migration of report_test.cbl
 * 
 * This program demonstrates:
 * - Report generation with formatting
 * - Pagination logic
 * - Column alignment
 * - Page numbering
 * 
 * COBOL-to-Java mappings:
 * - COBOL Report Writer -> Java manual formatting with String.format()
 * - COBOL INITIATE -> Java report initialization
 * - COBOL GENERATE -> Java write formatted line
 * - COBOL TERMINATE -> Java report finalization
 * - Page limit/heading/detail/footing -> Manual line counting and page breaks
 */
public class ReportWriterExample {
    private static final int PAGE_LIMIT = 66;
    private static final int HEADING_LINE = 1;
    private static final int FIRST_DETAIL = 6;
    private static final int LAST_DETAIL = 42;
    private static final int FOOTING_LINE = 52;

    private int currentLine;
    private int pageCounter;
    private BufferedWriter reportWriter;

    public static void main(String[] args) {
        try {
            ReportWriterExample example = new ReportWriterExample();
            example.run();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void run() throws IOException {
        System.out.println("Starting test report program.");

        List<StudentRecord> records = readInputFile("input.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("report.txt"))) {
            this.reportWriter = writer;
            System.out.println("Init test report.");
            initiateReport();

            for (StudentRecord record : records) {
                if (record != null) {
                    System.out.println("Generate report line.");
                    generateReportLine(record);
                }
            }

            System.out.println("Terminate report.");
            terminateReport();
        }

        System.out.println("Done.");
    }

    /**
     * Initializes the report (corresponds to COBOL INITIATE).
     * Sets up page counter and writes the first page header.
     */
    private void initiateReport() throws IOException {
        pageCounter = 1;
        currentLine = 1;
        writePageHeader();
    }

    /**
     * Writes a page header matching COBOL report header definition (lines 48-58).
     */
    private void writePageHeader() throws IOException {
        currentLine = HEADING_LINE;

        String headerTitle = "Customer Order Report";
        int titleColumn = 44;
        String titleLine = padLeft("", titleColumn - 1) + headerTitle;
        reportWriter.write(titleLine);
        reportWriter.newLine();
        currentLine++;

        String pageLabel = "PAGE";
        int pageLabelColumn = 100;
        int pageNumberColumn = 105;
        String pageNumber = String.format("%3d", pageCounter);
        
        String pageLine = padLeft("", pageLabelColumn - 1) + pageLabel + 
                         padLeft("", pageNumberColumn - pageLabelColumn - pageLabel.length()) + pageNumber;
        reportWriter.write(pageLine);
        reportWriter.newLine();
        currentLine++;

        while (currentLine < FIRST_DETAIL) {
            reportWriter.newLine();
            currentLine++;
        }
    }

    /**
     * Generates a detail line for a student record (corresponds to COBOL GENERATE).
     * Matches report-line definition (lines 59-63).
     */
    private void generateReportLine(StudentRecord record) throws IOException {
        if (currentLine > LAST_DETAIL) {
            advanceToNextPage();
        }

        String detailLine = String.format("   %6d          %-20s     %-3s  %2d",
                record.getStudentId(),
                padRight(record.getStudentName(), 20),
                padRight(record.getMajor(), 3),
                record.getNumCourses());

        reportWriter.write(detailLine);
        reportWriter.newLine();
        currentLine++;
    }

    /**
     * Advances to the next page with proper page break.
     */
    private void advanceToNextPage() throws IOException {
        while (currentLine <= PAGE_LIMIT) {
            reportWriter.newLine();
            currentLine++;
        }

        pageCounter++;
        writePageHeader();
    }

    /**
     * Terminates the report (corresponds to COBOL TERMINATE).
     * Fills remaining lines on the current page.
     */
    private void terminateReport() throws IOException {
        while (currentLine <= PAGE_LIMIT) {
            reportWriter.newLine();
            currentLine++;
        }
    }

    /**
     * Reads student records from input file.
     */
    private List<StudentRecord> readInputFile(String filename) throws IOException {
        List<StudentRecord> records = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        StudentRecord record = StudentRecord.fromString(line);
                        if (record != null) {
                            records.add(record);
                        }
                    } catch (Exception e) {
                        System.err.println("Warning: Could not parse line: " + line);
                    }
                }
            }
        }

        return records;
    }

    private static String padLeft(String s, int n) {
        if (s == null) {
            s = "";
        }
        if (s.length() >= n) {
            return s;
        }
        return String.format("%" + n + "s", s);
    }

    private static String padRight(String s, int n) {
        if (s == null) {
            s = "";
        }
        if (s.length() >= n) {
            return s.substring(0, n);
        }
        return String.format("%-" + n + "s", s);
    }
}
