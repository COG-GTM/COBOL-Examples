# Merge Sort Example - Java Implementation

This directory contains both COBOL and Java implementations of a merge sort example that demonstrates file-based sorting and merging operations.

## COBOL Implementation
- `merge_sort_test.cbl` - Original COBOL implementation using SORT and MERGE statements

## Java Implementation  
- `Customer.java` - Customer record class
- `MergeSortExample.java` - Main program implementing merge and sort operations

## Functionality
1. Creates test data for East and West regions
2. Merges files sorted by ascending customer ID
3. Sorts merged file by descending contract ID
4. Outputs results to files and console

## Running the Java Version
```bash
javac *.java
java MergeSortExample
```

## Output Files
- `test-file-1.txt` - East region customer data
- `test-file-2.txt` - West region customer data  
- `merge-output.txt` - Merged file sorted by customer ID
- `sorted-contract-id.txt` - Final output sorted by contract ID

## Customer Record Structure
Each customer record contains:
- Customer ID (5-digit numeric)
- Last Name (50 characters)
- First Name (50 characters)
- Contract ID (5-digit numeric)
- Comment (25 characters)

## Test Data
The implementation creates hardcoded test data matching the original COBOL version:

**East Region (test-file-1.txt):**
- 6 customer records with IDs: 1, 5, 10, 25, 50, 75

**West Region (test-file-2.txt):**
- 5 customer records with IDs: 3, 24, 30, 85, 999

## Processing Steps
1. **Create Test Data**: Generates two input files with predefined customer records
2. **Merge Operation**: Combines both files and sorts by ascending customer ID (equivalent to COBOL MERGE statement)
3. **Sort Operation**: Takes merged file and sorts by descending contract ID (equivalent to COBOL SORT statement)
4. **Display Results**: Shows sorted data on console and writes to output files

## Comparison with COBOL Version
The Java implementation preserves the exact same business logic as the COBOL version:
- Same customer record structure with identical field sizes and types
- Same hardcoded test data for both East and West regions
- Same two-step process: merge by customer ID, then sort by contract ID
- Same output file names and format
- Equivalent error handling and status reporting
