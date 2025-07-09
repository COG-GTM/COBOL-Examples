# Java Implementation of COBOL Merge Sort

This Java implementation replicates the functionality of the COBOL merge sort program (`merge_sort_test.cbl`).

## Overview

The Java program performs the same operations as the COBOL version:
1. Creates test data files (`test-file-1.txt` and `test-file-2.txt`)
2. Merges the files sorted by ascending customer ID
3. Sorts the merged file by descending contract ID

## Key Components

### CustomerRecord Class
Represents the customer record structure with:
- `customerId` (5 digits)
- `customerLastName` (50 characters)
- `customerFirstName` (50 characters) 
- `customerContractId` (5 digits)
- `customerComment` (25 characters)

### File Operations
- **Input files**: `test-file-1.txt`, `test-file-2.txt`
- **Output files**: `merge-output.txt`, `sorted-contract-id.txt`
- Fixed-width format matching COBOL PICTURE clauses

### Operations
1. **Merge**: Combines two input files and sorts by ascending customer ID
2. **Sort**: Takes merged file and sorts by descending contract ID

## Usage

```bash
# Compile
javac MergeSortExample.java

# Run
java MergeSortExample
```

## Test Data

The program generates the same test data as the COBOL version:

**test-file-1.txt** (East region):
- Customer IDs: 1, 5, 10, 50, 25, 75
- Contract IDs: 5423, 12323, 653, 5050, 7725, 1175

**test-file-2.txt** (West region):
- Customer IDs: 999, 3, 30, 85, 24
- Contract IDs: 1610, 3331, 8765, 4567, 247

## Output Files

1. **merge-output.txt**: Records merged and sorted by ascending customer ID
2. **sorted-contract-id.txt**: Records sorted by descending contract ID

## File Format

Each record is 135 characters:
- Customer ID: positions 1-5 (numeric, zero-padded)
- Last Name: positions 6-55 (50 characters, space-padded)
- First Name: positions 56-105 (50 characters, space-padded)
- Contract ID: positions 106-110 (numeric, zero-padded)
- Comment: positions 111-135 (25 characters, space-padded)

This matches the COBOL PICTURE clauses exactly.
