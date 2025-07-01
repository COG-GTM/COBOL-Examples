# COBOL to Java Merge Sort Conversion

This directory contains both the original COBOL merge sort program and its Java equivalent that preserves the exact same file-based sorting behavior.

## Files

### COBOL Implementation
- `merge_sort_test.cbl` - Original COBOL program demonstrating merge and sort operations

### Java Implementation  
- `CustomerRecord.java` - Java class representing the customer record structure
- `MergeSortExample.java` - Main Java program that replicates COBOL functionality
- `README.md` - This documentation file

## Java Implementation Overview

The Java implementation maintains the same functionality as the COBOL program while preserving these key behaviors:

### Record Structure
- **Customer ID**: 5 digits, zero-padded (e.g., "00001")
- **Last Name**: 50 characters, right-padded with spaces
- **First Name**: 50 characters, right-padded with spaces  
- **Contract ID**: 5 digits, zero-padded (e.g., "05423")
- **Comment**: 25 characters, right-padded with spaces

### File-Based Operations
- **No in-memory collections**: Uses file-based merge and external sorting to avoid loading entire datasets into memory
- **Same file structure**: Uses identical input/output files (`test-file-1.txt`, `test-file-2.txt`, `merge-output.txt`, `sorted-contract-id.txt`)
- **Streaming operations**: Processes records one at a time during merge operations

### Functional Equivalence
- **Test data generation**: Creates identical customer records as the COBOL program
- **Merge operation**: Combines two sorted files by customer ID in ascending order
- **Sort operation**: Reorders merged file by contract ID in descending order
- **Console output**: Displays identical messages and record formatting

## How to Build and Run

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Command line access

### Compilation
```bash
javac *.java
```

### Execution
```bash
java MergeSortExample
```

## Expected Output

The Java program produces identical output to the COBOL version:

```
Creating test data files...
Merging and sorting files...
00001last-1                                            first-1                                           05423comment-1                
00003last-03                                           first-03                                          03331comment-03               
00005last-5                                            first-5                                           12323comment-5                
00010last-10                                           first-10                                          00653comment-10               
00024last-24                                           first-24                                          00247comment-24               
00025last-25                                           first-25                                          07725comment-25               
00030last-30                                           first-30                                          08765comment-30               
00050last-50                                           first-50                                          05050comment-50               
00075last-75                                           first-75                                          01175comment-75               
00085last-85                                           first-85                                          04567comment-85               
00999last-999                                          first-999                                         01610comment-99               
Sorting merged file on descending contract id....
00005last-5                                            first-5                                           12323comment-5                
00030last-30                                           first-30                                          08765comment-30               
00025last-25                                           first-25                                          07725comment-25               
00001last-1                                            first-1                                           05423comment-1                
00050last-50                                           first-50                                          05050comment-50               
00085last-85                                           first-85                                          04567comment-85               
00003last-03                                           first-03                                          03331comment-03               
00999last-999                                          first-999                                         01610comment-99               
00075last-75                                           first-75                                          01175comment-75               
00010last-10                                           first-10                                          00653comment-10               
00024last-24                                           first-24                                          00247comment-24               
Done.
```

## Technical Implementation Details

### External Sorting Algorithm
The Java implementation uses external sorting for the contract ID sort operation:
1. Reads input file in chunks of 1000 records
2. Sorts each chunk in memory and writes to temporary files
3. Merges temporary files maintaining descending order by contract ID
4. Cleans up temporary files after completion

### File-Based Merge Algorithm  
The merge operation processes two sorted input files:
1. Opens both input files simultaneously
2. Reads one record at a time from each file
3. Compares customer IDs and writes the smaller one first
4. Continues until all records are processed

This approach ensures memory usage remains constant regardless of file size, exactly matching the COBOL program's behavior.

## Comparison with COBOL

| Aspect | COBOL | Java |
|--------|-------|------|
| Record Structure | Fixed-width PIC clauses | String formatting with padding |
| File Operations | Built-in MERGE/SORT | Custom file-based algorithms |
| Memory Usage | File-based (no loading) | File-based (external sorting) |
| Output Format | Native COBOL display | Formatted string output |
| Error Handling | File status codes | IOException handling |

The Java implementation successfully preserves all functional aspects of the original COBOL program while using modern Java idioms and best practices.
