# Java Implementation of COBOL Merge Sort Program

This directory contains a Java implementation of the COBOL merge sort program found in `merge_sort_test.cbl`. The Java version replicates the exact functionality of the original COBOL program, including customer record structure, file operations, merge/sort logic, and test data generation.

## Program Structure

### CustomerRecord.java
- Represents a customer record with 5 fields matching the COBOL structure:
  - `customerId` (5 digits, equivalent to COBOL `pic 9(5)`)
  - `lastName` (50 characters, equivalent to COBOL `pic x(50)`)
  - `firstName` (50 characters, equivalent to COBOL `pic x(50)`)
  - `contractId` (5 digits, equivalent to COBOL `pic 9(5)`)
  - `comment` (25 characters, equivalent to COBOL `pic x(25)`)
- Includes comparators for sorting by customer ID (ascending) and contract ID (descending)

### FileIOUtils.java
- Utility class for reading and writing customer records to fixed-width text files
- Maintains exact compatibility with COBOL's PIC clause formatting (135 characters per record)
- Handles proper padding and trimming to match COBOL behavior

### MergeSortExample.java
- Main class that replicates the COBOL program flow:
  1. `createTestData()` - Generates the same test data as the COBOL program
  2. `mergeAndDisplayFiles()` - Merges two input files and sorts by customer ID ascending
  3. `sortAndDisplayFile()` - Sorts merged file by contract ID descending

## Compilation and Execution

### Prerequisites
- Java 11 or higher (uses `String.repeat()` method)

### Compile the Java code:
```bash
cd /path/to/COBOL-Examples/merge_sort/java
javac -d . com/cobol/examples/mergesort/*.java
```

### Run the program:
```bash
java com.cobol.examples.mergesort.MergeSortExample
```

## Output Files
The program creates the following files (same as COBOL version):
- `test-file-1.txt` - First test data file (6 customer records)
- `test-file-2.txt` - Second test data file (5 customer records)  
- `merge-output.txt` - Merged and sorted by customer ID (ascending)
- `sorted-contract-id.txt` - Final output sorted by contract ID (descending)

## Conversion Notes

### COBOL to Java Mappings
- COBOL `pic 9(5)` → Java `int` with 5-digit zero-padded formatting
- COBOL `pic x(50)` → Java `String` with right-padding to 50 characters
- COBOL `MERGE` statement → Java `Collections.sort()` with custom comparator
- COBOL `SORT` statement → Java `Collections.sort()` with reversed comparator
- COBOL file status checking → Java exception handling

### Key Differences
- Java uses object-oriented design vs COBOL's procedural approach
- Java Collections framework provides efficient sorting vs COBOL's declarative MERGE/SORT
- Java exception handling vs COBOL's file status codes
- Java camelCase naming vs COBOL hyphenated naming

## Expected Output
The program should produce identical results to the COBOL version:
1. Display "Creating test data files..."
2. Display "Merging and sorting files..." followed by 11 customer records sorted by ID
3. Display "Sorting merged file on descending contract id...." followed by the same 11 records sorted by contract ID (descending)
4. Display "Done."

## Test Data
The program generates the same hardcoded test data as the COBOL version:
- **test-file-1.txt**: Customer IDs 1, 5, 10, 50, 25, 75
- **test-file-2.txt**: Customer IDs 999, 3, 30, 85, 24
