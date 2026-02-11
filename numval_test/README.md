# NUMVAL Function Test

This example demonstrates converting an alphanumeric (`PIC X`) value to a numeric value using the `FUNCTION NUMVAL` intrinsic function.

`numval_test.cbl` accepts two numbers from the user -- one stored as `PIC X(10)` and another as `PIC 9(10)` -- then uses `FUNCTION NUMVAL` to convert the alphanumeric value for arithmetic and adds them together.

## How to Compile

```bash
cobc -x numval_test.cbl
```

## How to Run

```bash
./numval_test
```

The program prompts for two numeric values.

## Expected Output

```
Enter first number: 10
Enter second number: 20
Total:  3.00000000000000E+001
```

The total is displayed in scientific notation because the result is stored in a `COMP-2` (double-precision floating point) variable.

## Prerequisites

None beyond GnuCOBOL (`cobc`). This example requires user input.
