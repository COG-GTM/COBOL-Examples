# Merge Sort Test - Java Implementation

This is a Java implementation of the COBOL `merge_sort_test.cbl` program.

## Overview

The program demonstrates merge and sort operations on fixed-width customer record files:

1. Creates two test input files with sample customer data
2. Merges them by customerID (ascending order)
3. Sorts the merged output by contractID (descending order)

## Files

### Source Files
- `CustomerRecord.java` - Represents a customer record with fixed-width format support
- `FixedWidthFileIO.java` - Handles reading/writing fixed-width files
- `MergeSortTest.java` - Main class with test data generation, merge, and sort logic

### Generated Files (at runtime)
- `test-file-1.txt` - First input file (East region customers)
- `test-file-2.txt` - Second input file (West region customers)
- `merge-output.txt` - Merged output (sorted by customerID ascending)
- `sorted-contract-id.txt` - Final output (sorted by contractID descending)

## Fixed-Width Record Format

Each record is 135 characters with the following fields:

| Field | Type | Length | Position |
|-------|------|--------|----------|
| customerID | Numeric | 5 | 1-5 |
| lastName | Text | 50 | 6-55 |
| firstName | Text | 50 | 56-105 |
| contractID | Numeric | 5 | 106-110 |
| comment | Text | 25 | 111-135 |

## Compilation and Execution

```bash
cd src
javac *.java
java MergeSortTest
```

## Original COBOL Program

The original COBOL program is located at `../merge_sort/merge_sort_test.cbl`.

Author: Erik Eriksen (2021-09-19)
