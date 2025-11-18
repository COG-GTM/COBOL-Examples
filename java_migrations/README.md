# COBOL to Java Migrations

This directory contains Java implementations of three COBOL programs from the COBOL-Examples repository. Each Java program is functionally equivalent to its COBOL counterpart.

## Overview

The following COBOL programs have been migrated to Java:

1. **Merge Sort Program** - `merge_sort/merge_sort_test.cbl` → `java_migrations/merge_sort/`
2. **Numeric Validation Program** - `numval_test/numval_test.cbl` → `java_migrations/numval_test/`
3. **Command Line Arguments Program** - `read_command_args/read_specific_cmd_line_args.cbl` → `java_migrations/read_command_args/`

## Program Details

### 1. Merge Sort Program

**Location:** `java_migrations/merge_sort/`

**Files:**
- `CustomerRecord.java` - POJO representing a customer record with fixed-length formatting
- `MergeSortExample.java` - Main program implementing merge and sort operations

**Functionality:**
- Creates two test data files with customer records
- Merges two pre-sorted files on ascending customer ID using a two-pointer merge algorithm
- Sorts the merged file by descending contract ID using Java's `Comparator`
- Handles fixed-length record format (135 characters per record)

**COBOL Features Migrated:**
- File I/O (FD descriptors) → Java `BufferedReader`/`BufferedWriter`
- MERGE statement → Two-pointer merge algorithm
- SORT statement → `List.sort()` with custom `Comparator`
- Fixed-length records → String formatting with `String.format()`

**Compilation and Execution:**
```bash
cd java_migrations/merge_sort
javac CustomerRecord.java MergeSortExample.java
java MergeSortExample
```

**Output:**
- Creates `test-file-1.txt` and `test-file-2.txt` with test data
- Creates `merge-output.txt` with merged records sorted by customer ID
- Creates `sorted-contract-id.txt` with records sorted by contract ID (descending)
- Displays all records to console

### 2. Numeric Validation Program

**Location:** `java_migrations/numval_test/`

**Files:**
- `NumvalTest.java` - Program demonstrating numeric input validation and computation

**Functionality:**
- Prompts user for two numeric inputs
- Validates and parses input strings to numbers
- Computes the sum of the two numbers
- Displays the total

**COBOL Features Migrated:**
- ACCEPT statement → Java `Scanner` with `nextLine()`
- NUMVAL function → `Double.parseDouble()` with try-catch
- COMPUTE statement → Standard Java arithmetic
- Error handling → `NumberFormatException` handling

**Compilation and Execution:**
```bash
cd java_migrations/numval_test
javac NumvalTest.java
java NumvalTest
```

**Example Usage:**
```
Enter first number: 123.45
Enter second number: 67.89
Total: 191.34
```

### 3. Command Line Arguments Program

**Location:** `java_migrations/read_command_args/`

**Files:**
- `CmdArgsExample.java` - Program demonstrating command-line argument processing

**Functionality:**
- Reads command-line arguments
- Iterates through all arguments
- Displays each argument to console

**COBOL Features Migrated:**
- ACCEPT FROM ARGUMENT-NUMBER → `args.length`
- PERFORM VARYING loop → Java for-loop
- ACCEPT FROM ARGUMENT-VALUE → Direct array access `args[i]`
- DISPLAY statement → `System.out.println()`

**Compilation and Execution:**
```bash
cd java_migrations/read_command_args
javac CmdArgsExample.java
java CmdArgsExample arg1 arg2 arg3
```

**Example Output:**
```
arg1
arg2
arg3
```

## Migration Approach

Each Java implementation follows these principles:

1. **Functional Equivalence** - Java programs produce the same output as COBOL programs given the same input
2. **Idiomatic Java** - Uses Java best practices and standard library features
3. **Error Handling** - Implements proper exception handling and validation
4. **Readability** - Clear, well-structured code with meaningful variable names
5. **No External Dependencies** - Uses only Java standard library (no third-party libraries)

## Key Differences from COBOL

- **Type System**: Java uses strong static typing vs COBOL's PIC clauses
- **File I/O**: Java uses streams and readers/writers vs COBOL's FD descriptors
- **Data Structures**: Java uses objects and collections vs COBOL's fixed-length records
- **Control Flow**: Java uses standard loops and conditionals vs COBOL's PERFORM statements
- **String Handling**: Java uses String class methods vs COBOL's fixed-length string operations

## Testing

All three Java programs have been tested to ensure they produce functionally equivalent output to their COBOL counterparts. The programs handle:

- File I/O operations
- Data parsing and validation
- Sorting and merging algorithms
- Command-line argument processing
- Error conditions and edge cases

## Requirements

- Java Development Kit (JDK) 8 or higher
- No external dependencies required

## Author

Migrated from COBOL to Java by Devin (Cognition AI)
Original COBOL programs by Erik Eriksen
