# COBOL Examples

A comprehensive collection of example and test COBOL programs demonstrating various features and capabilities of the COBOL programming language using GnuCOBOL.

## Overview

This repository contains practical examples covering a wide range of COBOL functionality, from basic input/output operations to advanced features like SQL database connectivity, JSON/XML generation, and screen handling. Each example is designed to be self-contained and educational, with detailed comments and documentation.

## Prerequisites

All programs were written and tested using [GnuCOBOL](https://gnucobol.sourceforge.io/) on Linux. To compile and run these examples, you'll need:

- GnuCOBOL compiler (cobc)
- Linux/Unix environment (or Windows with appropriate setup)
- Additional libraries for specific examples (see individual README files)

## Compiling and Running Examples

To compile a COBOL program:

```bash
cd <example-directory>
cobc -x <program-name>.cbl -o <output-name>
```

To run the compiled program:

```bash
./<output-name>
```

## Examples Directory

### Input/Output Operations

| Directory | Description |
|-----------|-------------|
| `accept` | Demonstrates various `ACCEPT` statement syntax options including secure input, command-line arguments, environment variables, and screen mode |
| `display_test` | Basic `DISPLAY` statement examples for outputting text to the terminal |
| `display_timing` | Performance testing for display operations |

### Data Manipulation

| Directory | Description |
|-----------|-------------|
| `comp_test` | Computational data type examples and testing |
| `is_numeric` | Demonstrates numeric validation using the `IS NUMERIC` condition |
| `numval_test` | Examples using the `NUMVAL` and `NUMVAL-C` intrinsic functions |
| `trim` | String trimming operations using the `TRIM` function |
| `unstring` | Demonstrates the `UNSTRING` statement for parsing delimited strings |
| `redifines` | Examples of the `REDEFINES` clause for data aliasing |

### Data Structures and Algorithms

| Directory | Description |
|-----------|-------------|
| `merge_sort` | Implementation of the merge sort algorithm in COBOL |
| `search` | Demonstrates the `SEARCH` and `SEARCH ALL` statements for table lookups |

### File and Report Processing

| Directory | Description |
|-----------|-------------|
| `report_writer` | Examples using COBOL's Report Writer feature for formatted output |

### Screen Handling

| Directory | Description |
|-----------|-------------|
| `screen_size` | Demonstrates how to get terminal screen dimensions |
| `mouse` | Mouse input handling in screen mode |

### External Data Formats

| Directory | Description |
|-----------|-------------|
| `json_generate` | JSON document generation using the `JSON GENERATE` statement (requires libjson-c) |
| `xml_generate` | XML document generation using the `XML GENERATE` statement (requires libxml2) |

### Database Connectivity

| Directory | Description |
|-----------|-------------|
| `sql` | PostgreSQL database connectivity using the esqlOC precompiler and ODBC |

### Program Structure

| Directory | Description |
|-----------|-------------|
| `sub_program` | Demonstrates calling subprograms and parameter passing |
| `read_command_args` | Reading and processing command-line arguments |

## Special Requirements

Some examples require additional libraries or configuration:

### JSON Generation
Requires [libjson-c](https://github.com/json-c/json-c) and GnuCOBOL configured with `--with-json`. See `json_generate/README.md` for details.

### XML Generation
Requires [libxml2](https://github.com/GNOME/libxml2) and GnuCOBOL configured with `--with-xml2`. See `xml_generate/README.md` for details.

### SQL/Database
Requires PostgreSQL, unixODBC, odbc-postgresql driver, and the esqlOC precompiler. See `sql/README.md` for detailed setup instructions.

## Screen Mode Notes

Several examples use COBOL's screen handling capabilities, which utilize either pdcurses (Windows) or ncurses (Linux/Mac). When entering screen mode:

- Screen x,y locations must be provided for output positioning
- Output defaults to the upper left corner if no position is specified
- The `SCREEN SECTION` can be used to define screen layouts

## Contributing

Contributions are welcome! If you have additional COBOL examples or improvements to existing ones, please feel free to submit a pull request.

## License

See the [LICENSE](LICENSE) file for details.

## Resources

- [GnuCOBOL Documentation](https://gnucobol.sourceforge.io/doc/gnucobol.html)
- [GnuCOBOL FAQ](https://gnucobol.sourceforge.io/faq/index.html)
- [GnuCOBOL Programmer's Guide](https://gnucobol.sourceforge.io/guides.html)  



