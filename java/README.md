# COBOL Examples — Java port

A modern Java 21 reimplementation of the COBOL example collection in this repo.

The goal (JD-99) is to modernise the UI of the examples once migrated to Java and
follow Java best practices. The headline changes:

- **One unified CLI instead of ~20 standalone programs.** Every example is a
  discoverable subcommand of `cobol-examples`, with consistent help, colour and
  argument parsing via [Picocli](https://picocli.info/).
- **Business logic separated from the UI.** All ported logic lives under
  `com.cobol.examples.core.*` and is free of any I/O, so it is unit-tested
  directly. Presentation is centralised in `com.cobol.examples.ui.ConsoleView`
  (colour via JAnsi) and the `com.cobol.examples.cli.*` commands.
- **Idiomatic Java replacements for COBOL constructs:** `BigDecimal` for
  `COMP`/`PIC` numerics, sealed types for `REDEFINES`, Jackson for
  `JSON GENERATE`/`XML GENERATE`, JDBC + H2 for embedded SQL, and Lanterna for
  the screen/mouse TUI surfaces.

## Build & test

```bash
cd java
mvn verify          # compile, run tests, build the shaded jar
```

## Run

```bash
java -jar target/cobol-examples.jar --help
java -jar target/cobol-examples.jar merge-sort
java -jar target/cobol-examples.jar json
java -jar target/cobol-examples.jar sql --search "Hop%"
```

## Example mapping

| COBOL source | Java command | Core logic |
|---|---|---|
| `merge_sort/` | `merge-sort` | `core.mergesort.MergeSortService` |
| `search/` | `search` | `core.search.SearchService` |
| `numval_test/`, `is_numeric/`, `comp_test/` | `numeric` | `core.numeric.NumericUtils` |
| `trim/`, `unstring/` | `text` | `core.text.TextUtils` |
| `redifines/` | `redefines` | `core.redefines.Customer` (sealed) |
| `report_writer/` | `report` | `core.report.ReportWriter` |
| `json_generate/` | `json` | `core.data.DataGenerator` |
| `xml_generate/` | `xml` | `core.data.DataGenerator` |
| `sql/` | `sql` | `core.sql.AccountRepository` (H2) |
| `read_command_args/` | `args` | `core.args.CommandArgsService` |
| `sub_program/` | `sub-program` | `core.subprogram.SubProgram` |
| `display_test/`, `display_timing/` | `display` | `ui.ConsoleView` |
| `screen_size/` | `screen-info` | Lanterna |
| `accept/` (secure / no-echo) | `secure-input` | `java.io.Console` |
| `mouse/` | `mouse-paint` | Lanterna (interactive) |

The interactive commands (`screen-info`, `secure-input`, `mouse-paint`) require a
real terminal and are therefore not part of the headless test suite.
