# Java Merge Sort Example

## Overview

This is a Java migration of the COBOL merge sort example (`merge_sort_test.cbl`). It demonstrates the same sorting and merging concepts using idiomatic Java code.

## Purpose

This example demonstrates:
- Creating test data programmatically
- Merging two data sources and sorting by a key field
- Re-sorting merged data by a different key field
- File I/O operations in Java
- Using Java Streams and Comparators for sorting

## COBOL to Java Migration Notes

### Data Structures
- **COBOL Records** → **Java POJO (CustomerRecord class)**
  - COBOL `PIC 9(5)` fields → Java `int`
  - COBOL `PIC X(n)` fields → Java `String`
  - COBOL record layout → Java class with getters

### File Operations
- **COBOL `SELECT`/`FD` statements** → **Java NIO `Files` API**
  - `OPEN OUTPUT` → `Files.newBufferedWriter()`
  - `WRITE` → `writer.write()` + `writer.newLine()`
  - `OPEN INPUT` → `Files.newBufferedReader()`
  - `READ` → `reader.readLine()`
  - `CLOSE` → try-with-resources auto-closes

### Sorting and Merging
- **COBOL `MERGE` statement** → **Java `Stream.concat()` + `sorted()`**
  - The COBOL `MERGE` statement merges two input files and sorts them in one operation
  - In Java, we read both files, concatenate the streams, and sort using `Comparator`

- **COBOL `SORT` statement** → **Java `Stream.sorted()`**
  - COBOL's `SORT ON ASCENDING KEY` → `Comparator.comparingInt()`
  - COBOL's `SORT ON DESCENDING KEY` → `Comparator.comparingInt().reversed()`

### Key Differences

1. **In-Memory vs. External Sorting**: The COBOL version uses external sorting (file-based), while this Java version loads data into memory. For large datasets, consider using external sorting libraries.

2. **File Status Handling**: COBOL uses file status codes (`ws-fs-status`). Java uses exception handling with `IOException`.

3. **End-of-File Detection**: COBOL uses condition names (`ws-eof`). Java uses `readLine()` returning `null`.

4. **Record Formatting**: COBOL uses `PIC` clauses for fixed-width formatting. Java uses `String.format()` to maintain the same format.

## Files

- `CustomerRecord.java` - POJO representing a customer record with formatting methods
- `MergeSortExample.java` - Main program that creates test data, merges, and sorts

## Compilation and Execution

### Compile
```bash
cd src
javac CustomerRecord.java MergeSortExample.java
```

### Run
```bash
java MergeSortExample
```

### Expected Output

The program will:
1. Create two test data files (`test-file-1.txt` and `test-file-2.txt`)
2. Merge and sort them by customer ID (ascending), displaying the results
3. Sort the merged data by contract ID (descending), displaying the results

Output files created:
- `test-file-1.txt` - East region customer data
- `test-file-2.txt` - West region customer data
- `merge-output.txt` - Merged and sorted by customer ID
- `sorted-contract-id.txt` - Sorted by contract ID (descending)

## Learning Points

### For COBOL Developers Learning Java

1. **Object-Oriented Design**: Java uses classes to represent data structures, whereas COBOL uses record definitions in the DATA DIVISION.

2. **Streams API**: Java's Stream API provides functional-style operations for collections, similar to COBOL's `SORT` and `MERGE` statements but more flexible.

3. **Exception Handling**: Java uses try-catch blocks instead of file status codes.

4. **Type Safety**: Java's strong typing system catches errors at compile time that might only appear at runtime in COBOL.

### For Java Developers Learning COBOL Concepts

1. **Fixed-Width Records**: COBOL typically uses fixed-width fields. The `toString()` method demonstrates this formatting.

2. **File-Based Processing**: COBOL programs often process data file-by-file rather than loading everything into memory.

3. **Declarative Sorting**: COBOL's `SORT` and `MERGE` statements are declarative - you specify what to sort, not how to sort it.

## Extending This Example

To handle larger datasets more efficiently:
- Use `Files.lines()` with lazy evaluation instead of loading all records into memory
- Implement external sorting algorithms for datasets larger than available memory
- Consider using Apache Commons CSV or similar libraries for more complex file formats
- Add error handling for malformed records

## Related COBOL Example

See `../merge_sort_test.cbl` for the original COBOL implementation.
