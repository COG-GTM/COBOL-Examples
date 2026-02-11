# DISPLAY Timing Benchmark

This example benchmarks screen write performance between two different `DISPLAY` positioning syntaxes in GnuCOBOL.

`display_timing.cbl` fills an 80x20 screen grid with the `@` character 100 times using each syntax, repeating 10 runs per method, then computes and displays the average time per run.

The two syntaxes compared are:

1. `DISPLAY "@" AT RRCC` (packed row/column format)
2. `DISPLAY "@" LINE nn COLUMN nn` (keyword format)

## How to Compile

```bash
cobc -x display_timing.cbl
```

## How to Run

```bash
./display_timing
```

The program will prompt you to press Enter to start. After each set of 10 runs it displays timing results and waits for input before continuing.

## Expected Behavior

The program runs in screen mode (ncurses). After both benchmarks complete, average timing results are displayed for each syntax. Actual times will vary depending on your system.

## Prerequisites

- GnuCOBOL (`cobc`)
- A terminal that supports ncurses
- Interactive (requires user input to start and advance between benchmarks)
