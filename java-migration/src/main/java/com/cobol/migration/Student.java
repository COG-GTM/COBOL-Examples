package com.cobol.migration;

import java.util.Objects;

public class Student {
    private int studentId;
    private String studentName;
    private String major;
    private int numCourses;

    public Student() {
    }

    public Student(int studentId, String studentName, String major, int numCourses) {
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

    public String toFileFormat() {
        return String.format("%06d%-20s%-3s%02d",
                studentId,
                padRight(studentName, 20),
                padRight(major, 3),
                numCourses);
    }

    public static Student fromFileFormat(String line) {
        if (line == null || line.length() < 29) {
            throw new IllegalArgumentException("Invalid student record format");
        }

        Student student = new Student();
        student.setStudentId(Integer.parseInt(line.substring(0, 6).trim()));
        student.setStudentName(line.substring(6, 26).trim());
        student.setMajor(line.substring(26, 29).trim());
        
        String numCoursesStr = line.substring(29).trim();
        if (!numCoursesStr.isEmpty()) {
            student.setNumCourses(Integer.parseInt(numCoursesStr));
        }

        return student;
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

    @Override
    public String toString() {
        return String.format("Student ID: %06d, Name: %-20s, Major: %-3s, Courses: %02d",
                studentId, studentName, major, numCourses);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return studentId == student.studentId &&
                numCourses == student.numCourses &&
                Objects.equals(studentName, student.studentName) &&
                Objects.equals(major, student.major);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, studentName, major, numCourses);
    }
}
