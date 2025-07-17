# Java Implementation of COBOL Merge Sort

This is a Java implementation of the COBOL merge sort program found in `merge_sort_test.cbl`.

## Overview

This implementation recreates the exact functionality of the original COBOL program:

1. **Data Structure**: `CustomerRecord` class with fixed-width fields matching COBOL specifications:
   - customerID: 5 digits
   - lastName: 50 characters
   - firstName: 50 characters
   - contractID: 5 digits
   - comment: 25 characters

2. **File Operations**: Fixed-width record I/O matching COBOL's recording mode F

3. **Processing Workflow**:
   - Generate test data into `test-file-1.txt` and `test-file-2.txt`
   - Merge files on ascending customerID into `merge-output.txt`
   - Sort merged results on descending contractID into `sorted-contract-id.txt`

## Classes

- **CustomerRecord**: Represents the customer data structure with exact field sizes
- **FixedWidthFileReader**: Handles reading fixed-width records from files
- **FixedWidthFileWriter**: Handles writing fixed-width records to files
- **MergeSortProcessor**: Main class implementing the merge and sort operations

## Usage

```bash
cd java-merge-sort/src/main/java
javac com/example/mergesort/*.java
java com.example.mergesort.MergeSortProcessor
```

## Test Data

The program generates the same test data as the original COBOL program:

**test-file-1.txt**:
- Customer 1, 5, 10, 50, 25, 75

**test-file-2.txt**:
- Customer 999, 3, 30, 85, 24

## Output Files

- `merge-output.txt`: Merged records sorted by customer ID (ascending)
- `sorted-contract-id.txt`: Final output sorted by contract ID (descending)

## Ticket Reference

This implementation addresses ticket MBA-18.
