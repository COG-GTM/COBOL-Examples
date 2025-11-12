package com.cobol.examples;

/**
 * Java representation of COBOL student record structure.
 * Maps to COBOL PIC clauses:
 * - f-test-student-id: pic 9(6) -> int
 * - f-test-student-name: pic x(20) -> String
 * - f-test-major: pic xxx -> String
 * - f-test-num-courses: pic 99 -> int
 */
public class StudentRecord {
    private int studentId;
    private String studentName;
    private String major;
    private int numCourses;

    public StudentRecord() {
    }

    public StudentRecord(int studentId, String studentName, String major, int numCourses) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.major = major;
        this.numCourses = numCourses;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public int getNumCourses() {
        return numCourses;
    }

    public void setNumCourses(int numCourses) {
        this.numCourses = numCourses;
    }

    /**
     * Formats the record to match COBOL file format.
     * COBOL uses fixed-width fields without delimiters.
     */
    public String toFileFormat() {
        return String.format("%06d%-20s%-3s%02d",
                studentId,
                padRight(studentName, 20),
                padRight(major, 3),
                numCourses);
    }

    /**
     * Parses a COBOL-formatted line into a StudentRecord.
     * Expected format: 6-digit ID, 20-char name, 3-char major, 2-digit num courses
     */
    public static StudentRecord fromString(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        if (line.length() < 31) {
            throw new IllegalArgumentException("Invalid record format: line too short");
        }

        StudentRecord record = new StudentRecord();
        record.setStudentId(Integer.parseInt(line.substring(0, 6).trim()));
        record.setStudentName(line.substring(6, 26).trim());
        record.setMajor(line.substring(26, 29).trim());
        record.setNumCourses(Integer.parseInt(line.substring(29, 31).trim()));

        return record;
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

    @Override
    public String toString() {
        return String.format("StudentRecord{id=%d, name='%s', major='%s', courses=%d}",
                studentId, studentName, major, numCourses);
    }
}
