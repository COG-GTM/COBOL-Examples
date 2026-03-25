package com.cobolmigration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Model class replacing the COBOL report input record
 * in report_writer/report_test.cbl (lines 24-28).
 * Fields map to:
 *   f-test-student-id    -> studentId
 *   f-test-student-name  -> studentName
 *   f-test-major         -> major
 *   f-test-num-courses   -> numCourses
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportRecord {

    private int studentId;
    private String studentName;
    private String major;
    private int numCourses;
}
