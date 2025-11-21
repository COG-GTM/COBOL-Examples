package com.cobol.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ReportWriter {
    private static final Logger logger = LoggerFactory.getLogger(ReportWriter.class);
    
    private static final String INPUT_FILE = "input.txt";
    private static final String REPORT_FILE = "report.txt";
    private static final int PAGE_LIMIT = 66;
    private static final int LINES_PER_PAGE = 36;

    public static void main(String[] args) {
        logger.info("Starting test report program.");
        System.out.println("Starting test report program.");

        try {
            List<Student> students = readInputFile();
            generateReport(students);
            
            System.out.println("Done.");
            logger.info("Report generation completed successfully");
        } catch (IOException e) {
            logger.error("Error during report generation: {}", e.getMessage(), e);
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static List<Student> readInputFile() throws IOException {
        logger.info("Reading input file: {}", INPUT_FILE);
        
        Path inputPath = Paths.get(INPUT_FILE);
        List<Student> students = new ArrayList<>();

        if (!Files.exists(inputPath)) {
            logger.warn("Input file does not exist: {}", INPUT_FILE);
            return students;
        }

        try (BufferedReader reader = Files.newBufferedReader(inputPath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        students.add(Student.fromFileFormat(line));
                    } catch (Exception e) {
                        logger.warn("Skipping invalid line: {}", line);
                    }
                }
            }
        }

        logger.info("Read {} student records from input file", students.size());
        return students;
    }

    private static void generateReport(List<Student> students) throws IOException {
        logger.info("Init test report.");
        System.out.println("Init test report.");

        Path reportPath = Paths.get(REPORT_FILE);

        try (BufferedWriter writer = Files.newBufferedWriter(reportPath)) {
            int pageNumber = 1;
            int lineCount = 0;

            writeReportHeader(writer, pageNumber);
            lineCount = 6;

            for (Student student : students) {
                if (lineCount >= LINES_PER_PAGE) {
                    writePageBreak(writer);
                    pageNumber++;
                    writeReportHeader(writer, pageNumber);
                    lineCount = 6;
                }

                logger.debug("Generate report line for student: {}", student.getStudentId());
                System.out.println("Generate report line.");
                writeReportLine(writer, student);
                lineCount++;
            }

            writeReportFooter(writer);
        }

        logger.info("Terminate report.");
        System.out.println("Terminate report.");
    }

    private static void writeReportHeader(BufferedWriter writer, int pageNumber) throws IOException {
        writer.write(centerText("Customer Order Report", 120));
        writer.newLine();
        
        String pageText = String.format("PAGE %3d", pageNumber);
        writer.write(padLeft(pageText, 108));
        writer.newLine();
        
        writer.newLine();
        writer.write(String.format("%-10s %-20s %-10s %-10s", 
                "ID", "Name", "Major", "Courses"));
        writer.newLine();
        writer.write(repeatChar('-', 80));
        writer.newLine();
    }

    private static void writeReportLine(BufferedWriter writer, Student student) throws IOException {
        String line = String.format("    %06d   %-20s   %-3s    %02d",
                student.getStudentId(),
                padRight(student.getStudentName(), 20),
                padRight(student.getMajor(), 3),
                student.getNumCourses());
        
        writer.write(line);
        writer.newLine();
    }

    private static void writeReportFooter(BufferedWriter writer) throws IOException {
        writer.newLine();
        writer.write(repeatChar('-', 80));
        writer.newLine();
        writer.write(centerText("End of Report", 80));
        writer.newLine();
    }

    private static void writePageBreak(BufferedWriter writer) throws IOException {
        writer.newLine();
        writer.newLine();
        writer.write(repeatChar('=', 80));
        writer.newLine();
        writer.newLine();
    }

    private static String centerText(String text, int width) {
        if (text.length() >= width) {
            return text;
        }
        int padding = (width - text.length()) / 2;
        return repeatChar(' ', padding) + text;
    }

    private static String padRight(String str, int length) {
        if (str == null) {
            str = "";
        }
        if (str.length() >= length) {
            return str.substring(0, length);
        }
        return String.format("%-" + length + "s", str);
    }

    private static String padLeft(String str, int length) {
        if (str == null) {
            str = "";
        }
        if (str.length() >= length) {
            return str.substring(0, length);
        }
        return String.format("%" + length + "s", str);
    }

    private static String repeatChar(char c, int count) {
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }
}
