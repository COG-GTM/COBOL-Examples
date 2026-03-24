package com.cobolmigration.model;

/**
 * Model for report writer input data.
 * Replaces the COBOL f-test-record group item from report_writer/report_test.cbl:
 *   05 f-test-student-id      pic 9(6)
 *   05 f-test-student-name    pic x(20)
 *   05 f-test-major           pic xxx
 *   05 f-test-num-courses     pic 99
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

    /**
     * Parse from fixed-width line matching COBOL record layout.
     * Total: 6 + 20 + 3 + 2 = 31 characters
     */
    public static StudentRecord fromFixedWidth(String line) {
        if (line.length() < 31) {
            line = String.format("%-31s", line);
        }
        int id = Integer.parseInt(line.substring(0, 6).trim());
        String name = line.substring(6, 26).trim();
        String major = line.substring(26, 29).trim();
        int courses = Integer.parseInt(line.substring(29, 31).trim());
        return new StudentRecord(id, name, major, courses);
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
}
