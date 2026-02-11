# COMP Conversion Test

This example demonstrates converting between `COMP` (binary) and `DISPLAY` (human-readable) numeric data types in COBOL.

`comp_test.cbl` shows how a `COMP` variable can be used in arithmetic, moved to a standard `DISPLAY` variable, and moved to a dynamically formatted variable using `PIC ZZ9` (zero-suppressed).

## How to Compile

```bash
cobc -x comp_test.cbl
```

## How to Run

```bash
./comp_test
```

The program will prompt for a numeric value to convert.

## Expected Output

```
COMP: 0024
DISP:024
DYNA: 24
INPUT: 050
INPUT: 050
COMP: 0050
```

## Prerequisites

None beyond GnuCOBOL (`cobc`). This example requires user input.
