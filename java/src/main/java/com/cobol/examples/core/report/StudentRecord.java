package com.cobol.examples.core.report;

/** Input record for the report writer, ported from {@code f-test-record}. */
public record StudentRecord(int studentId, String name, String major, int numCourses) {
}
