# Java Implementation of COBOL merge_sort_test.cbl

This directory contains a Java implementation that replicates the exact functionality of the COBOL `merge_sort_test.cbl` program.

## Overview

The Java implementation consists of four main classes that work together to replicate the COBOL program's behavior:

- **CustomerRecord**: Represents the customer data structure with exact field sizes matching COBOL specifications
- **FileReader**: Handles reading fixed-width records from files
- **FileWriter**: Handles writing fixed-width records to files  
- **MergeSortProcessor**: Main class implementing the complete workflow

## Customer Record Structure

The CustomerRecord class maintains the exact same fixed-width format as the COBOL program:

- customerID: 5 digits (pic 9(5))
- lastName: 50 characters (pic x(50))
- firstName: 50 characters (pic x(50))
- contractID: 5 digits (pic 9(5))
- comment: 25 characters (pic x(25))

Total record length: 135 characters

## Program Workflow

The Java implementation follows the exact same workflow as the COBOL program:

1. **Generate Test Data**: Creates two input files with predefined test records
   - `test-file-1.txt`: Contains 6 customer records
   - `test-file-2.txt`: Contains 5 customer records

2. **Merge Operation**: Combines both input files and sorts by ascending customerID
   - Output: `merge-output.txt`

3. **Sort Operation**: Takes merged file and sorts by descending contractID
   - Output: `sorted-contract-id.txt`

## Usage

### Compilation
```bash
javac *.java
```

### Execution
```bash
java MergeSortProcessor
```

### Expected Output Files
- `test-file-1.txt` - First test data file
- `test-file-2.txt` - Second test data file
- `merge-output.txt` - Merged records sorted by ascending customerID
- `sorted-contract-id.txt` - Final output sorted by descending contractID

## Test Data

The implementation uses the exact same test data as the original COBOL program:

### test-file-1.txt records:
- Customer ID 1, Contract ID 5423
- Customer ID 5, Contract ID 12323
- Customer ID 10, Contract ID 653
- Customer ID 50, Contract ID 5050
- Customer ID 25, Contract ID 7725
- Customer ID 75, Contract ID 1175

### test-file-2.txt records:
- Customer ID 999, Contract ID 1610
- Customer ID 3, Contract ID 3331
- Customer ID 30, Contract ID 8765
- Customer ID 85, Contract ID 4567
- Customer ID 24, Contract ID 247

## Fixed-Width Format

All records maintain strict fixed-width formatting to match COBOL's behavior:
- Fields are padded with spaces to their exact lengths
- No delimiters are used between fields
- Each record is exactly 135 characters long

## Error Handling

The Java implementation includes proper error handling for:
- File not found errors
- Invalid record format errors
- I/O exceptions during file operations

## Compatibility

This Java implementation produces identical output to the original COBOL program, maintaining:
- Same file names
- Same record format
- Same sorting behavior
- Same test data
