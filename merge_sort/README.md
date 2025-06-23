# COBOL to Java Merge Sort Conversion

This directory contains both the original COBOL merge sort implementation (`merge_sort_test.cbl`) and its Java equivalent.

## Original COBOL Program
The COBOL program demonstrates file-level sorting operations using:
- `MERGE` statement to combine two input files sorted by customer ID (ascending)
- `SORT` statement to sort the merged file by contract ID (descending)

## Java Implementation
The Java version replicates the same functionality using three classes:

### Customer.java
- Represents the customer record structure with 5 fields
- Provides fixed-width string formatting to match COBOL record format
- Fields: customer ID (5 digits), last name (50 chars), first name (50 chars), contract ID (5 digits), comment (25 chars)

### FileHandler.java
- Handles file I/O operations for reading and writing customer records
- Parses fixed-width format to maintain compatibility with COBOL output
- Provides display functionality for console output

### MergeSortExample.java
- Main program that replicates the COBOL program flow
- Creates the same test data as the original COBOL program
- Performs merge operation (sort by customer ID ascending)
- Performs sort operation (sort by contract ID descending)

## Running the Java Program
```bash
javac *.java
java MergeSortExample
```

## Output Files
- `test-file-1.txt` - First input file with customer records
- `test-file-2.txt` - Second input file with customer records  
- `merge-output.txt` - Merged file sorted by customer ID (ascending)
- `sorted-contract-id.txt` - Final output sorted by contract ID (descending)

## Design Decisions
- Used Java's `Collections.sort()` for efficiency rather than implementing custom sorting algorithms
- Maintained fixed-width file format for compatibility with COBOL record structure
- Separated concerns into multiple classes for better maintainability
- Preserved exact test data and output behavior from original COBOL program

## Conversion Notes
The Java implementation maintains the exact same functionality as the COBOL program:
- Creates identical test data in two input files
- Merges files with ascending sort by customer ID
- Sorts merged data with descending sort by contract ID
- Outputs to the same file names with compatible format
- Displays results to console

The fixed-width format ensures compatibility between COBOL and Java file outputs. The separation into multiple classes follows Java best practices while maintaining the original program's logic flow.
