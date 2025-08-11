# Java Merge Sort Implementation

This is a Java implementation of the COBOL merge sort program found in `merge_sort/merge_sort_test.cbl`. It maintains the same file-based sorting behavior and data structure as the original COBOL program.

## Overview

The program demonstrates file-level sorting operations using Java's file I/O classes, preserving the original COBOL functionality:

1. **Create test data**: Generates two input files with customer records
2. **Merge operation**: Combines two sorted input files into a single file sorted by customer ID (ascending)
3. **Sort operation**: Sorts the merged file by customer contract ID (descending)

## Data Structure

The `CustomerRecord` class maintains the same fields as the original COBOL program:
- `customerId` (5-digit integer)
- `customerLastName` (50-character string)
- `customerFirstName` (50-character string)
- `customerContractId` (5-digit integer)
- `customerComment` (25-character string)

## Classes

### CustomerRecord
- Represents a customer record with all required fields
- Provides methods for file serialization/deserialization
- Maintains fixed-width formatting similar to COBOL PIC clauses

### FileMergeSorter
- Implements file-based merge and sort operations
- Creates test data files with the same records as the COBOL program
- Performs external sorting for large datasets (file-based approach)

### MergeSortExample
- Main class that orchestrates the entire process
- Follows the same execution flow as the original COBOL program

## Usage

### Compilation
```bash
cd java-merge-sort
javac -d . src/main/java/com/example/mergesort/*.java
```

### Execution
```bash
java com.example.mergesort.MergeSortExample
```

## Output Files

The program creates the following files:
- `test-file-1.txt`: East region customer data
- `test-file-2.txt`: West region customer data  
- `merge-output.txt`: Merged file sorted by customer ID (ascending)
- `sorted-contract-id.txt`: Final file sorted by contract ID (descending)

## Key Features

- **File-based operations**: Uses Java's `BufferedReader`/`BufferedWriter` instead of in-memory collections for large dataset handling
- **External sorting**: Implements file-based sorting similar to COBOL's `SORT` and `MERGE` statements
- **Fixed-width format**: Maintains the same record format as the original COBOL program
- **Error handling**: Includes proper exception handling for file operations

## Comparison with COBOL

| COBOL Feature | Java Equivalent |
|---------------|-----------------|
| `SELECT` statements | File path strings |
| `FD` file descriptors | `BufferedReader`/`BufferedWriter` |
| `MERGE` statement | `mergeFiles()` method |
| `SORT` statement | `sortFile()` method |
| `PIC` clauses | Fixed-width string formatting |
| File status variables | Exception handling |

This implementation preserves the file-based approach of the original COBOL program rather than using simple in-memory sorting, making it suitable for processing large datasets that don't fit in memory.
