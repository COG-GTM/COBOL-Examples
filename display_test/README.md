# DISPLAY Statement Options

This example demonstrates various `DISPLAY` statement options available in GnuCOBOL for screen-mode output.

`display-test.cbl` shows:

- Positioning text with `AT RRCC` syntax (e.g., `at 0505`)
- Positioning with `LINE` and `COLUMN` keywords
- `WITH BLANK LINE` to clear the rest of the line
- `WITH ERASE EOL` to erase to end of line
- `WITH BELL` to produce an audible alert
- Setting `BACKGROUND-COLOR` and `FOREGROUND-COLOR`

## How to Compile

```bash
cobc -x display-test.cbl
```

## How to Run

```bash
./display-test
```

## Expected Behavior

The program enters screen mode (ncurses) and displays "hello world" at several screen positions with different formatting options. One line will trigger a terminal bell sound. The final line is displayed with a cyan foreground on a cyan background.

## Prerequisites

- GnuCOBOL (`cobc`)
- A terminal that supports ncurses (screen mode is entered automatically when positional `DISPLAY` is used)
