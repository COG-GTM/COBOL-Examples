# Phase 0 — Contracts extracted from `report_writer/report_test.cbl`

The COBOL program is the specification and the system of record. Nothing under
`report_writer/` is modified by this migration; every rule below was derived from the
source and then confirmed against real GnuCOBOL output (`cobc -x report_test.cbl`).

## 1. Inventory

| Artifact | Kind | Role |
| --- | --- | --- |
| `report_writer/report_test.cbl` | GnuCOBOL program `report-test` | Reads the input file, drives the REPORT WRITER |
| `report_writer/input.txt` | Line sequential input file | Source records |
| `report.txt` | Sequential output file, `REPORT IS r-test-report` | Rendered report |
| `r-test-report` | `RD` in the REPORT SECTION | Page geometry + line layouts |
| `report-header` | `01 ... TYPE REPORT HEADING` | Emitted once, at the top of page 1 |
| `report-line` | `01 ... TYPE DETAIL LINE PLUS 1` | One line per `GENERATE report-line` |

There is no CICS, DB2, VSAM, JCL or BMS in this pipeline: it is a single batch program with
one flat input file and one print file.

## 2. Record layout — `f-test-record` (FD `fd-test-input-file`, line sequential)

| Field | PIC | Columns (1-based) | Width | Python representation |
| --- | --- | --- | --- | --- |
| `f-test-student-id` | `9(6)` | 1–6 | 6 | `str` of 6 chars, display-numeric semantics (see §4) |
| `f-test-student-name` | `X(20)` | 7–26 | 20 | `str`, trimmed on read, re-padded to 20 on write |
| `f-test-major` | `XXX` | 27–29 | 3 | `str`, trimmed on read, re-padded to 3 on write |
| `f-test-num-courses` | `99` | 30–31 | 2 | `str` of 2 chars, display-numeric semantics |

Record length is 31 bytes. Because the file is `LINE SEQUENTIAL`, short lines are padded with
spaces to the record length on read and trailing spaces are not significant.

There is no money in this pipeline and therefore no `COMP-3`/`PIC S9(n)V99` field, no rounding
rule to preserve, and no derived value. Consequently **no floating-point type is used anywhere**:
the numeric fields are transported as fixed-width strings, exactly the bytes COBOL would print,
so no client can reinterpret them as doubles. `student_id` and `num_courses` are additionally
exposed as `int | None` convenience fields, never as floats.

### Record key

`f-test-record` has no key: the file is read sequentially and every record produces exactly one
`GENERATE`. Records are addressed positionally (0-based index) by the migrated service.

## 3. Report geometry — `RD r-test-report`

Declared: `PAGE LIMIT IS 66`, `HEADING IS 1`, `FIRST DETAIL 6`, `LAST DETAIL 42`, `FOOTING 52`.

| Element | Source clause | Absolute placement |
| --- | --- | --- |
| Report heading line 1 | `LINE 1 COLUMN 44 PIC X(21) VALUE "Customer Order Report"` | Page 1 only, page-relative line 1, columns 44–64 |
| Report heading line 2 | `LINE 2 COLUMN 100 PIC X(4) VALUE "PAGE"`, `COLUMN 105 PIC ZZ9 SOURCE PAGE-COUNTER` | Page 1 only, page-relative line 2, columns 100–103 and 105–107 |
| Detail line | `TYPE DETAIL LINE PLUS 1`, columns 4 / 15 / 40 / 46 | Page-relative lines 6–42, i.e. **37 detail lines per page** |

Detail line column map: `9(6)` at 4–9, `X(20)` at 15–34, `XXX` at 40–42, `99` at 46–47.
Trailing blanks are not written: every emitted line is right-trimmed, and lines with no content
are written as empty lines. There is no `PAGE HEADING`, `PAGE FOOTING` or `CONTROL FOOTING`, so
pages 2..n carry no heading at all and `PAGE-COUNTER` is only ever *printed* as `  1`.

Observed physical pagination (see discrepancy D-003): pages advance every **65** physical lines,
not 66. Page `p` starts at absolute line `1 + 65 * (p - 1)`; the file always ends with one extra
blank line, for a total of `65 * pages + 1` physical lines.

## 4. Display-numeric move semantics

`SOURCE f-test-student-id` moves a `PIC 9(6)` display item into a `PIC 9(6)` report item, and
likewise `99` → `99`. GnuCOBOL copies the digits byte for byte and converts **spaces to `0`**;
other non-digit bytes are passed through unchanged. Confirmed empirically:

| Input bytes | Printed |
| --- | --- |
| `AB C12` | `AB0C12` |
| `12 456` | `120456` |
| `      ` (record area never loaded) | `000000` |
| `9 ` (`PIC 99`) | `90` |

The migrated code reproduces this rule in `cobol_move_display_numeric()` rather than parsing to
`int` and reformatting, which would corrupt these values.

`PAGE-COUNTER` is printed through `PIC ZZ9`: right justified in 3 characters with leading zeros
suppressed to spaces (`1` → `"  1"`).

## 5. Discrepancy register

Authority order: **copybook / FD record layout > program source > documentation**. Each ruling
has an ID that the code and tests cite.

| ID | Conflict | Ruling | Authority |
| --- | --- | --- | --- |
| D-001 | `PROCEDURE DIVISION` reads with `AT END SET ws-eof TO TRUE` but the `PERFORM UNTIL ws-eof` body executes `GENERATE report-line` **unconditionally**, so the final record is emitted twice (7 input rows → 8 detail lines), and an empty input file still emits one detail line from the never-loaded record area (`000000` / blanks / `00`). | **Preserved.** This is observable output that any downstream consumer of `report.txt` already sees. The Python port reproduces it byte for byte. A `strict_eof=True` option exists to suppress it, is **off by default**, and is never used by the parity harness. | Program source |
| D-002 | The header comment says "Report writer test application" and the heading literal says "Customer Order Report", while the record layout is student data (`student-id`, `major`, `num-courses`). | **Layout wins.** Field names and semantics follow the FD; the printed heading text is reproduced verbatim as `Customer Order Report`. No renaming, no "corrected" heading. | FD record layout > documentation |
| D-003 | `PAGE LIMIT IS 66` implies 66-line pages, but GnuCOBOL's emitted file advances page origins every 65 lines and appends one trailing blank line (`65 * pages + 1` lines total). | **Preserved.** The port matches the emitted bytes, not the declared literal, because the file is the contract. Documented here so a reader of the RD is not surprised. | Program source (as executed) |
| D-004 | `FOOTING 52` and `LAST DETAIL 42` reserve a footing area, but no `TYPE PAGE FOOTING`/`CONTROL FOOTING` group exists. | No footing is emitted; the region is blank filler. Nothing to port. | Program source |
| D-005 | The heading's `PAGE` label sits at column 100 and the counter at 105, beyond the usual 80/132-ish print width, and appears on page 1 only. | **Preserved** as-is (column 100/105, page 1 only). Callers that parse the header by column offset would break if we re-flowed it. The web UI renders the report in a horizontally scrollable monospace pane so the columns stay intact. | Program source |
| D-006 | `populate-input-file` is dead code — it is never `PERFORM`ed, and its `MOVE`s are commented out, so it would `WRITE` an uninitialised record. | **Not ported.** Nothing reachable, and porting it would create a writer where the COBOL has none. | Program source |
| D-007 | Input records shorter than 31 bytes (line sequential) leave trailing fields as spaces. | Short lines are space-padded to 31 bytes on read, then §4 applies, matching GnuCOBOL. | FD record layout |
| D-008 | The FD layout is applied to **bytes**, so a multibyte UTF-8 character in `f-test-student-name` shifts every later field; Python string slicing would instead count characters and silently realign the record. | The reader round-trips each line through Latin-1 (`to_byte_string`) so one Python character is one byte for the whole pipeline, and the print file is compared as bytes. JSON views decode back to text (`from_byte_string`). Fixture `multibyte_utf8` pins this against GnuCOBOL. | FD record layout |
| D-009 | `PIC X(n)` fields keep their leading and trailing spaces; `move f-test-student-name to student-name-out` copies the padded field verbatim, so `"  Alice"` prints indented. | Detail lines are built from the **raw** field bytes, never from a trimmed value. The trimmed `student_name` / `major` properties exist only for the JSON record table and never reach the report. Fixture `embedded_spaces` pins this. | FD record layout |

## 6. Cloud representation

| COBOL concept | Migrated form |
| --- | --- |
| `open input` / `read ... at end` loop | `RecordReader.read()` over the uploaded text |
| `initiate` / `generate` / `terminate` | `ReportWriter.initiate()` / `.generate_report_line()` / `.terminate()` |
| `report.txt` print file | `report_text` in the JSON response; pages also exposed structurally |
| `display` statements | `console` array in the JSON response, with the COBOL's literal text |
| Green-screen/console output | Bank Leumi themed single page app with a page-by-page pager |

Console literals reused verbatim: `Starting test report program.`, `Init test report.`,
`Generate report line.` (once per `GENERATE`), `Terminate report.`, `Done.`
