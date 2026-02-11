# Report Writer Example

This example demonstrates the COBOL Report Writer feature, which provides a declarative way to produce formatted reports.

`report_test.cbl` reads student records from `input.txt` and generates a formatted report written to `report.txt`. The report includes:

- A report heading with a title and page number
- Detail lines for each input record showing student ID, name, major, and number of courses
- Automatic page control (page limits, heading placement, detail line spacing)

## How to Compile

```bash
cobc -x report_test.cbl
```

## How to Run

```bash
./report_test
```

The program reads from `input.txt` (included in this directory) and writes the formatted report to `report.txt` in the current directory.

## Expected Output (console)

```
Starting test report program.
Init test report.
Generate report line.
Generate report line.
...
Terminate report.
Done.
```

The generated `report.txt` file will contain a paginated report with a header and detail lines.

## Input File Format

Each line in `input.txt` is a fixed-width record:

| Field | Position | PIC | Description |
|-------|----------|-----|-------------|
| Student ID | 1-6 | `9(6)` | Numeric student ID |
| Student Name | 7-26 | `X(20)` | Student name |
| Major | 27-29 | `XXX` | Three-letter major code |
| Num Courses | 30-31 | `99` | Number of courses |

## Prerequisites

None beyond GnuCOBOL (`cobc`). The included `input.txt` file must be in the working directory when running.
