# COBOL Examples

A collection of example and test COBOL programs demonstrating various COBOL language features, syntax, and techniques. All programs are written for and compiled with [GnuCOBOL](https://gnucobol.sourceforge.io/) on Linux.

## Examples Index

| Directory | Main File(s) | Description | Prerequisites |
|-----------|-------------|-------------|---------------|
| [accept](accept/) | `accept.cbl`, `accept-secure.cbl`, `accept_from.cbl` | Various forms of the `ACCEPT` statement including secure input, timeouts, and environment data | Interactive (requires user input) |
| [comp_test](comp_test/) | `comp_test.cbl` | Converting between `COMP` (binary) and `DISPLAY` numeric types | Interactive |
| [display_test](display_test/) | `display-test.cbl` | `DISPLAY` statement options: screen positioning, colors, blank line, erase, and bell | Uses screen mode (ncurses) |
| [display_timing](display_timing/) | `display_timing.cbl` | Benchmarking screen write speed between two `DISPLAY AT` position syntaxes | Interactive, uses screen mode |
| [is_numeric](is_numeric/) | `is_numeric.cbl` | Using `IS NUMERIC` to validate input with plain, zero-fill, and trim approaches | Interactive |
| [json_generate](json_generate/) | `json_generate.cbl` | Generating JSON documents from COBOL records using `JSON GENERATE` | [json-c](https://github.com/json-c/json-c); GnuCOBOL built with `--with-json` |
| [merge_sort](merge_sort/) | `merge_sort_test.cbl` | `SORT` and `MERGE` verbs with file-based data | None |
| [mouse](mouse/) | `mouse_example.cbl` | Mouse input handling in a simple drawing program | Uses screen mode (ncurses), interactive |
| [numval_test](numval_test/) | `numval_test.cbl` | Converting `PIC X` to numeric using the `NUMVAL` intrinsic function | Interactive |
| [read_command_args](read_command_args/) | `read_cmd_line_args.cbl`, `read_specific_cmd_line_args.cbl` | Reading and parsing command-line arguments | None (pass arguments when running) |
| [redifines](redifines/) | `redefines.cbl` | `REDEFINES` clause to reinterpret data in different formats | None |
| [report_writer](report_writer/) | `report_test.cbl` | COBOL Report Writer generating a formatted report from an input file | None (uses included `input.txt`) |
| [screen_size](screen_size/) | `get_screen_size.cbl` | Getting terminal dimensions using `ACCEPT FROM LINES/COLUMNS` and `CBL_GET_SCR_SIZE` | Interactive, uses screen mode |
| [search](search/) | `search.cbl` | `SEARCH` (sequential) and `SEARCH ALL` (binary) on indexed tables | Interactive |
| [sql](sql/) | `sql_example.cbl` | Connecting to and querying a PostgreSQL database via embedded SQL | [esqlOC](https://sourceforge.net/p/gnucobol/contrib/HEAD/tree/trunk/esql/), PostgreSQL, unixODBC, odbc-postgresql |
| [sub_program](sub_program/) | `main_app.cbl`, `sub.cbl` | Calling sub-programs with `BY CONTENT` and `BY REFERENCE`, plus `CANCEL` | Interactive |
| [trim](trim/) | `trim.cbl` | Intrinsic `TRIM` function for leading/trailing space removal | None |
| [unstring](unstring/) | `unstring.cbl` | `UNSTRING` verb with various delimiter and pointer options | None |
| [xml_generate](xml_generate/) | `xml_generate.cbl` | Generating XML documents from COBOL records using `XML GENERATE` | [libxml2](https://github.com/GNOME/libxml2); GnuCOBOL built with `--with-xml2` |

## Getting Started

### Compiler

All examples require [GnuCOBOL](https://gnucobol.sourceforge.io/) (`cobc`). Install it via your package manager or build from source.

### General Compile and Run

For most examples, compile and run with:

```bash
cd <example_directory>
cobc -x <program>.cbl
./<program>
```

The `-x` flag tells `cobc` to produce a standalone executable.

### Multi-file Programs

Some examples (like `sub_program`) require compiling multiple source files together:

```bash
cobc -x main_app.cbl sub.cbl -o a.out
./a.out
```

### Special Builds

- **JSON example** requires `json-c` and GnuCOBOL configured with `--with-json`.
- **XML example** requires `libxml2` and GnuCOBOL configured with `--with-xml2`.
- **SQL example** requires the esqlOC precompiler, PostgreSQL, and unixODBC. See the [sql/README.md](sql/README.md) for full build instructions.

## Interactive Examples

Many examples use `ACCEPT` to read user input and will wait for keyboard entry. If running in an automated or non-interactive environment, you can pipe input or use a timeout:

```bash
echo "42" | ./<program>
timeout 10s ./<program>
```

Some examples enter COBOL "screen mode" (backed by ncurses on Linux) when using positioned `DISPLAY` or `ACCEPT` statements. In screen mode, output is rendered via ncurses rather than standard output.

## License

See [LICENSE](LICENSE) for details.    



