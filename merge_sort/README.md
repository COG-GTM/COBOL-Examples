# COBOL to Java Migration: Merge Sort Example

## Overview

This directory contains a Java migration of the COBOL merge sort functionality from `merge_sort_test.cbl`. The Java implementation maintains the same file-based sorting behavior as the original COBOL program.

## Files

- `merge_sort_test.cbl` - Original COBOL implementation
- `Customer.java` - Java Customer class with comparable interface
- `MergeSortExample.java` - Main Java implementation
- `README.md` - This documentation file

## COBOL Program Analysis

The original COBOL program performs these operations:

1. **Creates test data files** with customer records:
   - `test-file-1.txt` (East region customers)
   - `test-file-2.txt` (West region customers)

2. **Merges two pre-sorted input files** using COBOL's built-in `MERGE` statement:
   - Sorts by customer ID in ascending order
   - Outputs to `merge-output.txt`

3. **Sorts the merged file** using COBOL's `SORT` statement:
   - Sorts by contract ID in descending order
   - Outputs to `sorted-contract-id.txt`

4. **Displays results** after each operation

## Java Implementation

### Customer Class
- Implements `Comparable<Customer>` interface
- Contains fields: customerId, lastName, firstName, contractId, comment
- Provides proper toString() formatting to match COBOL output
- Includes fromString() method for file parsing

### MergeSortExample Class
- `createTestData()` - Creates the same test data as COBOL version
- `mergeFiles()` - Implements two-pointer merge algorithm
- `mergeAndDisplayFiles()` - Merges files and displays results
- `sortAndDisplayFile()` - Sorts by contract ID descending and displays results

## Validation Results

Both implementations produce identical output:

### Test Data Creation
Creates two files with customer records in the exact same format and order as the COBOL version.

### Merge Operation
Merges the two input files, sorting by customer ID in ascending order:
```
00001last-1                                            first-1                                           05423comment-1                
00003last-03                                           first-03                                          03331comment-03               
00005last-5                                            first-5                                           12323comment-5                
...
```

### Sort Operation
Sorts the merged file by contract ID in descending order:
```
00005last-5                                            first-5                                           12323comment-5                
00030last-30                                           first-30                                          08765comment-30               
00025last-25                                           first-25                                          07725comment-25               
...
```

## Running the Programs

### COBOL Version
```bash
cobc -x merge_sort_test.cbl -o merge_sort_test
./merge_sort_test
```

### Java Version
```bash
javac *.java
java MergeSortExample
```

## Verification

The outputs have been verified to be identical using diff comparison:
```bash
diff cobol_output.txt java_output.txt
# No differences found - outputs are identical
```

## Key Implementation Details

1. **File Format Compatibility**: The Java version uses the same fixed-width format as COBOL (135 characters per record)
2. **Merge Algorithm**: Implements proper two-pointer merge for sorted lists
3. **Sorting**: Uses Java's built-in sort with custom comparators
4. **Display Format**: Matches COBOL's exact output formatting
5. **Error Handling**: Includes proper file I/O error handling

This migration successfully preserves the exact behavior and output of the original COBOL program while providing a modern Java implementation.
