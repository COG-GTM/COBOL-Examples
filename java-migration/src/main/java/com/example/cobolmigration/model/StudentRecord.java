package com.example.cobolmigration.model;

/**
 * Plain POJO from report_writer/report_test.cbl.
 * Maps the input file record structure:
 *   f-test-student-id     PIC 9(6)
 *   f-test-student-name   PIC X(20)
 *   f-test-major          PIC XXX
 *   f-test-num-courses    PIC 99
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

    @Override
    public String toString() {
        return "StudentRecord{studentId=" + studentId + ", studentName='" + studentName
                + "', major='" + major + "', numCourses=" + numCourses + "}";
    }
}
