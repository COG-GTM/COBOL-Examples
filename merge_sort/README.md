# Java Merge Sort Implementation

This directory contains a Java implementation that replicates the functionality of the COBOL merge sort example (`merge_sort_test.cbl`).

## Overview

The Java implementation performs the same operations as the COBOL version:

1. **Test Data Generation**: Creates two input files with customer records
2. **File Merging**: Combines the two files and sorts by ascending customer ID
3. **File Sorting**: Takes the merged file and sorts by descending contract ID

## Files

- `CustomerRecord.java` - Java class representing the customer record structure
- `MergeSortExample.java` - Main program that replicates the COBOL functionality
- `README.md` - This documentation file

## Customer Record Structure

Each customer record contains five fields:
- Customer ID (5-digit integer)
- Last Name (up to 50 characters)
- First Name (up to 50 characters)
- Contract ID (5-digit integer)
- Comment (up to 25 characters)

## Usage

### Compilation

```bash
javac *.java
```

### Execution

```bash
java MergeSortExample
```

## Output Files

The program generates the following files:
- `test-file-1.txt` - First input file with customer records
- `test-file-2.txt` - Second input file with customer records
- `merge-output.txt` - Merged file sorted by ascending customer ID
- `sorted-contract-id.txt` - Final file sorted by descending contract ID

## Comparison with COBOL Implementation

| Operation | COBOL | Java |
|-----------|-------|------|
| Data Structure | COBOL record with PIC clauses | Java class with fields |
| File I/O | COBOL file handling | Java NIO Files API |
| Merging | `MERGE` statement | Java streams + sorting |
| Sorting | `SORT` statement | `Collections.sort()` with comparators |
| Test Data | Programmatic record creation | Java collections and arrays |

## Test Data

The implementation uses the same test data as the COBOL version:

**File 1 (East):**
- Customer IDs: 1, 5, 10, 50, 25, 75
- Contract IDs: 5423, 12323, 653, 5050, 7725, 1175

**File 2 (West):**
- Customer IDs: 999, 3, 30, 85, 24
- Contract IDs: 1610, 3331, 8765, 4567, 247

## Expected Output

1. **Merged by Customer ID (ascending):**
   - 1, 3, 5, 10, 24, 25, 30, 50, 75, 85, 999

2. **Sorted by Contract ID (descending):**
   - 12323, 8765, 7725, 5423, 5050, 4567, 3331, 1610, 1175, 653, 247
