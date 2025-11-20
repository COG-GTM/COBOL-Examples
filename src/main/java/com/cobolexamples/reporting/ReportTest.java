package com.cobolexamples.reporting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ReportTest {
    
    private static final String INPUT_FILE = "input.txt";
    private static final String REPORT_FILE = "report.txt";
    private static final int PAGE_SIZE = 66;
    private static final int FIRST_DETAIL_LINE = 6;
    private static final int LAST_DETAIL_LINE = 42;
    
    private static int pageCounter = 1;
    private static int currentLine = 1;
    
    public static void main(String[] args) {
        System.out.println("Starting test report program.");
        
        try (BufferedReader reader = new BufferedReader(new FileReader(INPUT_FILE));
             PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(REPORT_FILE)))) {
            
            System.out.println("Init test report.");
            
            writeReportHeader(writer);
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    System.out.println("Generate report line.");
                    
                    if (currentLine > LAST_DETAIL_LINE) {
                        pageCounter++;
                        currentLine = 1;
                        writeReportHeader(writer);
                    }
                    
                    StudentRecord record = StudentRecord.fromFileFormat(line);
                    writeDetailLine(writer, record);
                    currentLine++;
                }
            }
            
            System.out.println("Terminate report.");
            
        } catch (IOException e) {
            System.err.println("Error processing report: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("Done.");
    }
    
    private static void writeReportHeader(PrintWriter writer) {
        writer.println();
        writer.println(centerText("Customer Order Report", 120));
        writer.println();
        writer.println(padRight("", 99) + "PAGE " + String.format("%3d", pageCounter));
        writer.println();
        writer.println();
        currentLine = FIRST_DETAIL_LINE;
    }
    
    private static void writeDetailLine(PrintWriter writer, StudentRecord record) {
        String line = String.format("   %6d          %-20s                     %-3s      %2d",
            record.getStudentId(),
            record.getStudentName(),
            record.getMajor(),
            record.getNumCourses());
        writer.println(line);
    }
    
    private static String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return padRight("", padding) + text;
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
    
    static class StudentRecord {
        private int studentId;
        private String studentName;
        private String major;
        private int numCourses;
        
        public StudentRecord(int studentId, String studentName, String major, int numCourses) {
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
        
        public static StudentRecord fromFileFormat(String line) {
            if (line.length() < 31) {
                line = padRight(line, 31);
            }
            
            int studentId = Integer.parseInt(line.substring(0, 6).trim());
            String studentName = line.substring(6, 26).trim();
            String major = line.substring(26, 29).trim();
            int numCourses = Integer.parseInt(line.substring(29, 31).trim());
            
            return new StudentRecord(studentId, studentName, major, numCourses);
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
}
