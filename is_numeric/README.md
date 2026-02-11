# IS NUMERIC Test

This example demonstrates how to use the `IS NUMERIC` class condition to check whether an alphanumeric variable contains only digits.

`is_numeric.cbl` shows three approaches:

1. **Plain** -- A standard `PIC X(10)` variable. Trailing spaces cause the `IS NUMERIC` test to fail even when only digits are entered.
2. **Right justify + zero fill** -- Using `JUSTIFIED RIGHT` and `INSPECT REPLACING LEADING SPACES BY '0'` to pad the value so the numeric test passes.
3. **Trim** -- Using `FUNCTION TRIM` to strip spaces before testing, which also allows the numeric test to pass.

## How to Compile

```bash
cobc -x is_numeric.cbl
```

## How to Run

```bash
./is_numeric
```

The program prompts for three separate values.

## Expected Output

```
(plain) Enter a value: 123
123        is not numeric.
(right justify, zero fill) Enter another value: 456
0000000456 is numeric!
(trim) Enter a third value: 789
789 is numeric!
```

## Prerequisites

None beyond GnuCOBOL (`cobc`). This example requires user input.
