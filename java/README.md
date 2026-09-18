# COBOL-Examples — Java port

Java 21 ports of every GnuCOBOL example program in this repository, organised
under `com.cobolexamples.<folder>` mirroring the COBOL directory layout, plus a
shared `com.cobolexamples.support` package that emulates COBOL data semantics.

## Build

```
cd java
mvn -q package
```

## Run

```
java -cp target/classes com.cobolexamples.trim.TrimExample
```

Programs that `ACCEPT` read lines from stdin — pipe input to run them
non-interactively, e.g. `printf '12\n' | java -cp target/classes com.cobolexamples.numvaltest.NumvalExample`.
Screen-mode programs emit ANSI escapes on stdout; `Screen.size()` shells out to
`stty size` on `/dev/tty` and falls back to 24x80.

## File mapping

| COBOL source | Java class | Notes |
|---|---|---|
| trim/trim.cbl | trim.TrimExample | FUNCTION TRIM/LEADING/TRAILING → `Pic.trim*`. Output is byte-identical to the COBOL binary. |
| unstring/unstring.cbl | unstring.UnstringExample | UNSTRING → `Unstring` helper (delimiters with ALL, WITH POINTER, DELIMITER IN, COUNT IN, TALLYING, overflow). Output byte-identical; EX1–EX6 covered by unit test. |
| redifines/redefines.cbl | redefines.RedefinesExample | REDEFINES → a shared 30-char `name` buffer with `firstName()/lastName()/corpName()` views; level-88 → `CustomerType` enum; x(10)/COMP-2 redefines → `byte[]` + little-endian `ByteBuffer`. The second test section's raw-byte text may differ from GnuCOBOL's non-printable output. |
| is_numeric/is_numeric.cbl | isnumeric.IsNumericExample | IS NUMERIC → `Pic.isNumeric`; JUSTIFIED RIGHT + INSPECT REPLACING → `justifiedRight` + replace. |
| numval_test/numval_test.cbl | numvaltest.NumvalExample | FUNCTION NUMVAL → `Pic.numval` (BigDecimal). |
| sub_program/main_app.cbl + sub.cbl | subprogram.MainApp / subprogram.SubApp | CALL USING BY CONTENT → pass `new Field(copy)`; BY REFERENCE → shared `Field` object; CANCEL → `SubApp.cancel()` resets statics (WORKING-STORAGE = static fields, LOCAL-STORAGE = locals). |
| read_command_args/read_cmd_line_args.cbl | readcommandargs.ReadCmdLineArgs | ACCEPT FROM COMMAND-LINE → `String.join(" ", args)` padded to 256; INSPECT TALLYING → case-insensitive `--test` count. |
| read_command_args/read_specific_cmd_line_args.cbl | readcommandargs.CmdArgsExample | ACCEPT FROM ARGUMENT-VALUE loop → iterate `args`, each padded to 99. |
| accept/accept.cbl | accept.AcceptExample | ACCEPT → `Console.accept*`; screen-mode ACCEPTs → `Screen.displayAt` + `acceptWithTimeout`/secure reads. |
| accept/accept-secure.cbl | accept.AcceptSecure | ACCEPT SECURE → `System.console().readPassword()` with plain fallback. |
| accept/accept_from.cbl | accept.AcceptFromExample | ACCEPT FROM DATE/DAY/TIME/DAY-OF-WEEK/USER NAME/LINES/COLUMNS → java.time + `Screen.size()`. FROM ENVIRONMENT → `Console.env` (a `Map` overlay is checked first because `SET ENVIRONMENT` cannot mutate the JVM's environment). FROM EXCEPTION STATUS → emulated as `0000001537` after a failed env ACCEPT. |
| comp_test/comp_test.cbl | comptest.CompConversionTest | PIC 999 COMP displays as 3 digits → `Pic.nine`; PIC ZZ9 → `Pic.z`. |
| json_generate/json_generate.cbl | jsongenerate.JsonGenerateExample | JSON GENERATE → manual JSON build `{"ws-record":{"name":...,"ws-record-blank":"","enabled":"true"}}`; NAME OF renames fields; COUNT IN → string length. |
| xml_generate/xml_generate.cbl | xmlgenerate.XmlGenerateExample | XML GENERATE → javax.xml DocumentBuilder + Transformer; TYPE IS ATTRIBUTE → `enabled` attribute; SUPPRESS WHEN SPACES → blank field omitted; WITH XML-DECLARATION → `<?xml version="1.0"?>`; output matches the COBOL binary byte-for-byte (count 0107). |
| search/search.cbl | search.SearchExample | SEARCH ALL → `Collections.binarySearch` on sorted `List<Item>`; sequential SEARCH → linear loop. |
| display_test/display-test.cbl | displaytest.DisplayTest | DISPLAY AT/LINE COLUMN, BLANK LINE, ERASE EOL, BELL, colours → `Screen` ANSI escapes. |
| display_timing/display_timing.cbl | displaytiming.DisplayTiming | Same loop structure (10 runs × 100 refreshes × 20x80 cells); ACCEPT FROM TIME → `System.nanoTime`; diffs/averages in the same HHMMSSss shape. |
| merge_sort/merge_sort_test.cbl | mergesort.MergeSortExample | Fixed-width 135-char `CustomerRecord` (format/parse); MERGE → read both files + stable sort asc by customer-id; SORT → sort desc by contract-id. Files land in CWD (`Path baseDir` is injectable for tests). Output byte-identical. |
| report_writer/report_test.cbl | reportwriter.ReportTest | REPORT SECTION/RD → page buffer: limit 66, heading at line 1 col 44, PAGE/ZZ9 counter line 2, first detail line 6, last detail 42. Reproduces the COBOL loop literally (read → check EOF → GENERATE), so the last record is emitted twice (8 detail lines for 7 input rows) — that is a faithful port of the program's own quirk, not a bug. |
| mouse/mouse_example.cbl | mouse.MouseExample | ncurses mouse can't be replicated in plain Java — keyboard-only approximation: digits change the draw colour (capped at 7), Q or ESC exits, banner drawn in white-on-blue at row 20. Raw input via `stty -echo -icanon` on `/dev/tty` when a TTY is present. |
| screen_size/get_screen_size.cbl | screensize.ScreenSizeTest | ACCEPT FROM LINES/COLUMNS and CBL_GET_SCR_SIZE → `Screen.size()` (`stty size`, fallback 24x80); ZZ9 → `Pic.z`. |
| sql/sql_example.cbl | sql.SqlExample | esqlOC cursors → JDBC `PreparedStatement`s (all / disabled / LIKE on 4 columns). Connection via `COBOL_DB_URL`/`COBOL_DB_USER`/`COBOL_DB_PASSWORD` env vars (defaults `jdbc:postgresql://localhost:5432/cobol_db_example`, `postgres`, `password`). Same menu loop and padded table output. SQL errors print SQLSTATE + message and exit. Requires the PostgreSQL JDBC driver (`mvn package` puts it on the classpath only as a runtime dep — run with `java -cp "target/classes:$(mvn -q dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q 2>/dev/null)"` or add the jar). |

## Support package (`com.cobolexamples.support`)

- `Pic` — COBOL data semantics: `x` (PIC X(n) MOVE: pad/truncate), `nine` (PIC 9(n): zero-fill, high-order truncation), `z` (PIC ZZ9 zero-suppression), `justifiedRight`, `isNumeric` (all digits, no sign/space), `numval` (FUNCTION NUMVAL → BigDecimal), `trim/trimLeading/trimTrailing`.
- `Unstring` — fluent UNSTRING: per-delimiter ALL flag, destinations with width + DELIMITER IN / COUNT IN receivers, `withPointer(int[])` (1-based, mutated), `tallying(int[])`, boolean overflow return.
- `Console` — `display` (operand concatenation like COBOL DISPLAY), `accept`/`acceptField` line input, env-var overlay for SET ENVIRONMENT.
- `Screen` — ANSI screen mode: `displayAt(row,col[,fg,bg])` (COBOL colours 0–7 mapped to ANSI), `clear`, `eraseEol`, `bell`, `size`, `readSecure`, `acceptWithTimeout`.
- `Field` — mutable fixed-width holder used for CALL linkage.

## Known fidelity gaps

- `json_generate`: this box's GnuCOBOL builds the program but emits an empty JSON document (count 0000 — json-c path not functional), so the Java port emits the documented expected JSON form rather than the empty string.
- `redefines` section 2: reading COMP-2 bytes as text produces non-printable bytes that differ from GnuCOBOL's rendering; the numeric read-back (12345.63) is exact.
- `accept_from` EXCEPTION STATUS is emulated (`0000001537` on failed env ACCEPT).
- `mouse` is a keyboard-only approximation (no ncurses mouse reporting).
