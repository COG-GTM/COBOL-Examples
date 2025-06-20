# Java Implementation of COBOL Merge Sort

This directory contains a Java implementation of the COBOL program `merge_sort_test.cbl` from the `merge_sort` directory.

## Overview

This Java implementation replicates the exact functionality of the original COBOL program, including:
- Fixed-width record format matching COBOL's field specifications
- Same test data generation as the original program
- Merge operation on ascending customerID
- Sort operation on descending contractID
- Same file names and output format

## Classes

### CustomerRecord
Represents the customer data structure with exact field sizes:
- `customerID`: 5 digits
- `lastName`: 50 characters
- `firstName`: 50 characters  
- `contractID`: 5 digits
- `comment`: 25 characters

### FileReader
Handles reading fixed-width customer records from files, matching COBOL's file I/O behavior.

### FileWriter
Handles writing fixed-width customer records to files and displaying records to console.

### MergeSortProcessor
Main class implementing the complete workflow:
1. Generate test data into `test-file-1.txt` and `test-file-2.txt`
2. Merge files on ascending customerID into `merge-output.txt`
3. Sort merged results on descending contractID into `sorted-contract-id.txt`

## Usage

```bash
# Compile the Java files
javac *.java

# Run the program
java MergeSortProcessor
```

## Output Files

The program generates the following files with fixed-width records:
- `test-file-1.txt`: First test data file
- `test-file-2.txt`: Second test data file  
- `merge-output.txt`: Merged results sorted by ascending customerID
- `sorted-contract-id.txt`: Final results sorted by descending contractID

## Field Format

Each record is exactly 135 characters:
- Characters 1-5: Customer ID (5 digits, zero-padded)
- Characters 6-55: Last Name (50 characters, space-padded)
- Characters 56-105: First Name (50 characters, space-padded)
- Characters 106-110: Contract ID (5 digits, zero-padded)
- Characters 111-135: Comment (25 characters, space-padded)

## Test Data

The program uses the same hardcoded test data as the original COBOL program to ensure identical output and behavior.
