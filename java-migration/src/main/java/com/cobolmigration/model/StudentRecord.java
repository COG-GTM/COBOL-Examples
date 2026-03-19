package com.cobolmigration.model;

/**
 * Model mapping from the COBOL FD student record layout in report_writer/report_test.cbl.
 *
 * <p>COBOL field mapping:
 * <ul>
 *   <li>f-test-student-id (PIC 9(6)) &rarr; studentId (int)</li>
 *   <li>f-test-student-name (PIC X(20)) &rarr; studentName (String)</li>
 *   <li>f-test-major (PIC XXX) &rarr; major (String)</li>
 *   <li>f-test-num-courses (PIC 99) &rarr; numCourses (int)</li>
 * </ul>
 *
 * @see report_writer/report_test.cbl lines 24-28
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
     * Parses a fixed-width record line matching the COBOL FD layout.
     * Total width: 6 + 20 + 3 + 2 = 31 characters.
     *
     * @param line the fixed-width line to parse
     * @return a StudentRecord parsed from the line
     */
    public static StudentRecord fromFixedWidth(String line) {
        if (line == null || line.length() < 31) {
            throw new IllegalArgumentException("Line too short for student record: " + line);
        }
        StudentRecord record = new StudentRecord();
        record.setStudentId(Integer.parseInt(line.substring(0, 6).trim()));
        record.setStudentName(line.substring(6, 26).trim());
        record.setMajor(line.substring(26, 29).trim());
        record.setNumCourses(Integer.parseInt(line.substring(29, 31).trim()));
        return record;
    }

    @Override
    public String toString() {
        return String.format("%-6d%-20s%-3s%02d", studentId,
                studentName != null ? studentName : "",
                major != null ? major : "",
                numCourses);
    }
}
