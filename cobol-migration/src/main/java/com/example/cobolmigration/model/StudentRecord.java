package com.example.cobolmigration.model;

/**
 * Corresponds to the COBOL report input record from report_writer/report_test.cbl (lines 24-28):
 * f-test-student-id pic 9(6), f-test-student-name pic x(20),
 * f-test-major pic xxx, f-test-num-courses pic 99.
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
}
