# Java Merge Sort Implementation

This project is a Java conversion of the COBOL merge sort functionality from the `merge_sort_test.cbl` file.

## Overview

The original COBOL program implements file-based sorting and merging operations using two main procedures:
- A merge operation that combines two sorted input files by customer ID
- A sort operation that sorts the merged file by contract ID in descending order

## Java Implementation

### CustomerRecord Class
Represents a customer record with the following fields (matching COBOL structure):
- `customerId` (int) - Equivalent to f-customer-id (pic 9(5))
- `lastName` (String) - Equivalent to f-customer-last-name (pic x(50))
- `firstName` (String) - Equivalent to f-customer-first-name (pic x(50))
- `contractId` (int) - Equivalent to f-customer-contract-id (pic 9(5))
- `comment` (String) - Equivalent to f-customer-comment (pic x(25))

### MergeSortExample Class
Main class implementing the conversion logic:

#### Key Methods:
- `mergeFiles()` - Implements two-pointer merge on pre-sorted lists by customer ID (replaces COBOL MERGE statement)
- `sortAndDisplayFile()` - Sorts records by contract ID in descending order using Java Collections
- `createTestDataFile1()` and `createTestDataFile2()` - Convert COBOL test data creation procedures
- `mergeAndDisplayFiles()` - Displays merged results
- `main()` - Implements the main program flow

## Compilation and Execution

```bash
# Compile
javac -d . src/main/java/com/example/mergesort/*.java

# Run
java com.example.mergesort.MergeSortExample
```

## Key Differences from COBOL

1. **File Operations**: Replaced COBOL file I/O with in-memory Java collections
2. **Sorting**: Uses Java's built-in `Collections.sort()` instead of external sort descriptors
3. **Merge Logic**: Implements explicit two-pointer merge algorithm instead of COBOL's MERGE statement
4. **Data Structure**: Uses Java objects instead of COBOL record layouts
5. **Memory Management**: In-memory processing instead of file-based operations

## Test Data

The program creates the same test data as the original COBOL version:

**File 1 (East)**: Customer IDs 1, 5, 10, 25, 50, 75
**File 2 (West)**: Customer IDs 3, 24, 30, 85, 999

The merge operation combines these by customer ID, and the sort operation orders by contract ID descending.
