# Reading Command-Line Arguments

This directory contains two examples of reading command-line arguments in GnuCOBOL.

## read_cmd_line_args.cbl

Reads the full command-line string using `ACCEPT ... FROM COMMAND-LINE` and checks for a specific flag (`--test`) using `INSPECT TALLYING`.

### How to Compile

```bash
cobc -x read_cmd_line_args.cbl
```

### How to Run

```bash
./read_cmd_line_args --test hello world
```

### Expected Output

```
Pass arg '--test' for special message
Full command line args: --test hello world
You entered the '--test' cmd arg!
```

## read_specific_cmd_line_args.cbl

Iterates through each command-line argument individually using `ACCEPT ... FROM ARGUMENT-NUMBER` to get the count, then `DISPLAY ... UPON ARGUMENT-NUMBER` and `ACCEPT ... FROM ARGUMENT-VALUE` to retrieve each argument by index.

### How to Compile

```bash
cobc -x read_specific_cmd_line_args.cbl
```

### How to Run

```bash
./read_specific_cmd_line_args "arg one" arg2 arg3
```

### Expected Output

```
arg one
arg2
arg3
```

## Prerequisites

None beyond GnuCOBOL (`cobc`). Pass arguments on the command line when running.
