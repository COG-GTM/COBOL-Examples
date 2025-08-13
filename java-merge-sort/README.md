# Java Merge Sort Implementation

This is a Java conversion of the COBOL merge sort example found in `../merge_sort/merge_sort_test.cbl`.

## Overview

The implementation provides equivalent functionality to the original COBOL program:

1. **CustomerRecord Class**: Represents customer data with fields matching the COBOL structure
2. **MergeSortProcessor**: Handles file operations, merging, and sorting
3. **MergeSortMain**: Main application that orchestrates the workflow

## Features

- Creates test data programmatically (matching COBOL test data)
- Merges two sorted files by customer ID in ascending order
- Sorts merged file by contract ID in descending order
- Displays results after each operation
- Proper file I/O with exception handling

## Data Structure

The `CustomerRecord` class implements `Comparable<CustomerRecord>` and contains:
- `customerId` (int) - 5-digit customer identifier
- `lastName` (String) - Customer last name (up to 50 characters)
- `firstName` (String) - Customer first name (up to 50 characters)
- `contractId` (int) - 5-digit contract identifier
- `comment` (String) - Comment field (up to 25 characters)

## Usage

### Compilation
```bash
javac -d . src/main/java/com/example/mergesort/*.java
```

### Execution
```bash
java com.example.mergesort.MergeSortMain
```

## Workflow

1. **Create Test Data**: Generates two test files with sample customer records
2. **Merge Files**: Merges the two files, sorting by customer ID ascending
3. **Sort by Contract ID**: Sorts the merged file by contract ID descending
4. **Display Results**: Shows the contents after each operation

## Files Generated

- `test-file-1.txt` - First test data file
- `test-file-2.txt` - Second test data file  
- `merge-output.txt` - Merged and sorted output
- `sorted-contract-id.txt` - Final sorted output by contract ID

## Differences from COBOL Version

- Uses in-memory collections instead of direct file processing
- Leverages Java's built-in sorting capabilities
- Implements proper object-oriented design patterns
- Uses modern Java I/O APIs for file handling

## Test Data

The implementation creates the same test data as the COBOL version:

**File 1**: Customer IDs 1, 5, 10, 25, 50, 75
**File 2**: Customer IDs 3, 24, 30, 85, 999

After merging and sorting by customer ID: 1, 3, 5, 10, 24, 25, 30, 50, 75, 85, 999

After sorting by contract ID (descending): Records ordered by contract ID from highest to lowest.
