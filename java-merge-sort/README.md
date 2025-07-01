# Java Merge Sort Implementation

This is a Java conversion of the COBOL merge sort example from `merge_sort/merge_sort_test.cbl`.

## Overview

This Java implementation maintains the same file-based sorting functionality as the original COBOL program:

1. Creates two test data files with customer records
2. Merges these files based on ascending customer ID
3. Sorts the merged file by descending contract ID
4. Displays results at each stage

## Customer Record Structure

The `CustomerRecord` class mirrors the COBOL record structure:
- Customer ID (5-digit integer)
- Last name (50-character string)
- First name (50-character string)
- Contract ID (5-digit integer)
- Comment (25-character string)

## File Operations

The implementation uses file-based operations that mirror the COBOL functionality:
- Reads from and writes to text files in fixed-width format
- Merges two input files into one sorted output file (by customer ID ascending)
- Sorts the merged file by contract ID in descending order
- Maintains the same workflow as the original COBOL program

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

## Files Generated

- `data/test-file-1.txt` - First test data file (East region records)
- `data/test-file-2.txt` - Second test data file (West region records)
- `data/merge-output.txt` - Merged file sorted by customer ID ascending
- `data/sorted-contract-id.txt` - Final file sorted by contract ID descending

## Test Data

The program creates the same test data as the COBOL version:

**File 1 (East):**
- Customer 1, 5, 10, 50, 25, 75 with various contract IDs

**File 2 (West):**
- Customer 999, 3, 30, 85, 24 with various contract IDs

## Workflow

1. **Create Test Data**: Generates two files with sample customer records
2. **Merge Files**: Combines both files and sorts by customer ID (ascending)
3. **Sort by Contract**: Takes merged file and sorts by contract ID (descending)
4. **Display Results**: Shows contents at each stage

This maintains the exact same functionality and workflow as the original COBOL `merge_sort_test.cbl` program.
