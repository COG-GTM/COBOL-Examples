# Phase 8: Report Generation

## COBOL Sources
- `report_writer/report_test.cbl` - Report Writer (RD) with page limits and detail lines

## COBOL-to-Java Mapping

### Report Writer Directives
| COBOL | Java |
|-------|------|
| `RD r-test-report PAGE LIMIT IS 66` | `PAGE_LIMIT = 66` constant |
| `HEADING IS 1` | Report header at start of each page |
| `FIRST DETAIL 6` | `FIRST_DETAIL_LINE = 6` constant |
| `LAST DETAIL 42` | `LAST_DETAIL_LINE = 42` constant |
| `FOOTING 52` | Page footer position |

### Report Sections
| COBOL | Java |
|-------|------|
| `report-header TYPE REPORT HEADING` | `generateStudentReportHeader()` method |
| `report-line TYPE DETAIL LINE PLUS 1` | `String.format()` per record |
| `PAGE-COUNTER` | `pageCounter` field, auto-incremented |
| `INITIATE r-test-report` | Start of `generateReport()` |
| `GENERATE report-line` | Append formatted detail line |
| `TERMINATE r-test-report` | End of `generateReport()` |

### Input Record
```
COBOL:                            Java:
f-test-student-id    PIC 9(6)  -> int studentId
f-test-student-name  PIC X(20) -> String studentName
f-test-major         PIC XXX   -> String major
f-test-num-courses   PIC 99    -> int numCourses
```

## Output Formats

The `ReportService` supports multiple output formats:

1. **Plain text** - Matches COBOL report output format with headers, columns, and page breaks
2. **CSV** - Comma-separated values for spreadsheet import

## Behavioral Differences

1. **Page breaks**: COBOL Report Writer automatically manages page breaks based on LINE PLUS and PAGE LIMIT. Java implementation manually tracks line count and inserts page headers.

2. **Column positioning**: COBOL uses exact column numbers (`COLUMN 4`, `COLUMN 15`). Java uses `String.format()` width specifiers for alignment.

3. **JasperReports**: For production PDF generation, JasperReports can be added as an alternative. The current implementation focuses on text-based reports matching the COBOL output format.
