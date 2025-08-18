# COBOL to Java Merge Sort Conversion

This directory contains both the original COBOL merge sort implementation and its Java conversion.

## Files

### Original COBOL Implementation
- `merge_sort_test.cbl` - Original COBOL program that demonstrates merge and sort operations

### Java Implementation  
- `Customer.java` - Java class representing customer records with fixed-width formatting
- `MergeSortJava.java` - Main Java program that replicates COBOL functionality

## Program Flow

Both implementations follow the same logic:

1. **Create Test Data**: Generate two input files with customer records
   - `test-file-1.txt` - East region customers (6 records)
   - `test-file-2.txt` - West region customers (5 records)

2. **Merge Operation**: Merge both input files by ascending customer ID
   - Output: `merge-output.txt` (11 records sorted by customer ID)

3. **Sort Operation**: Sort merged file by descending contract ID
   - Output: `sorted-contract-id.txt` (11 records sorted by contract ID)

## Record Structure

Each customer record contains:
- Customer ID (5 digits with leading zeros)
- Last Name (50 characters, left-aligned, space-padded)
- First Name (50 characters, left-aligned, space-padded)  
- Contract ID (5 digits with leading zeros)
- Comment (25 characters, left-aligned, space-padded)

Total record length: 135 characters

## Test Data

### East Region (test-file-1.txt)
- Customer ID 1: last-1, first-1, contract 5423, comment-1
- Customer ID 5: last-5, first-5, contract 12323, comment-5
- Customer ID 10: last-10, first-10, contract 653, comment-10
- Customer ID 50: last-50, first-50, contract 5050, comment-50
- Customer ID 25: last-25, first-25, contract 7725, comment-25
- Customer ID 75: last-75, first-75, contract 1175, comment-75

### West Region (test-file-2.txt)
- Customer ID 999: last-999, first-999, contract 1610, comment-99
- Customer ID 3: last-03, first-03, contract 3331, comment-03
- Customer ID 30: last-30, first-30, contract 8765, comment-30
- Customer ID 85: last-85, first-85, contract 4567, comment-85
- Customer ID 24: last-24, first-24, contract 247, comment-24

## Running the Programs

### COBOL
```bash
cobc -x merge_sort_test.cbl -o merge_sort_test
./merge_sort_test
```

### Java
```bash
javac *.java
java MergeSortJava
```

## Output Verification

Both programs produce identical output:

1. Console output showing merge and sort operations
2. Generated files with identical content and formatting
3. Same record ordering in both merge and sort phases

The Java implementation successfully replicates the COBOL program's behavior with character-for-character identical output.
