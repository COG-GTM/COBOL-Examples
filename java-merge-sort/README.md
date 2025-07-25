# Java Implementation of COBOL Merge Sort

This Java implementation replicates the functionality of the COBOL merge sort program found in `../merge_sort/merge_sort_test.cbl`.

## Overview

The program performs the following operations:
1. Creates test data files (`test-file-1.txt` and `test-file-2.txt`)
2. Merges the two files and sorts by customer ID (ascending)
3. Sorts the merged file by contract ID (descending)
4. Displays results and creates output files

## Customer Record Structure

Each customer record contains:
- Customer ID (5 digits)
- Last Name (up to 50 characters)
- First Name (up to 50 characters)
- Contract ID (5 digits)
- Comment (up to 25 characters)

## Files Generated

- `test-file-1.txt` - First input file with 6 customer records
- `test-file-2.txt` - Second input file with 5 customer records
- `merge-output.txt` - Merged file sorted by customer ID (ascending)
- `sorted-contract-id.txt` - Final file sorted by contract ID (descending)

## Running the Program

```bash
javac com/cognition/cobol/mergesort/*.java
java com.cognition.cobol.mergesort.MergeSortExample
```

## Test Data

### File 1 Records:
- Customer ID 1: last-1, first-1, Contract 5423, comment-1
- Customer ID 5: last-5, first-5, Contract 12323, comment-5
- Customer ID 10: last-10, first-10, Contract 653, comment-10
- Customer ID 25: last-25, first-25, Contract 7725, comment-25
- Customer ID 50: last-50, first-50, Contract 5050, comment-50
- Customer ID 75: last-75, first-75, Contract 1175, comment-75

### File 2 Records:
- Customer ID 3: last-03, first-03, Contract 3331, comment-03
- Customer ID 24: last-24, first-24, Contract 247, comment-24
- Customer ID 30: last-30, first-30, Contract 8765, comment-30
- Customer ID 85: last-85, first-85, Contract 4567, comment-85
- Customer ID 999: last-999, first-999, Contract 1610, comment-99
