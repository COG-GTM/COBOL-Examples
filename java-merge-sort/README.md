# Java Implementation of COBOL Merge Sort

This directory contains a Java implementation that replicates the functionality of the COBOL merge sort program found in `../merge_sort/merge_sort_test.cbl`.

## Overview

The Java implementation performs the same file-based sorting and merging operations as the original COBOL program:

1. **Test Data Creation**: Generates two input files with customer records
   - `test-file-1.txt`: 6 customer records
   - `test-file-2.txt`: 5 customer records

2. **Merge Operation**: Merges the two input files and sorts by customer ID in ascending order
   - Output: `merge-output.txt`

3. **Sort Operation**: Sorts the merged file by customer contract ID in descending order
   - Output: `sorted-contract-id.txt`

## Files

- `CustomerRecord.java`: Java class representing the customer record structure
- `MergeSortProgram.java`: Main program implementing the merge and sort logic
- `README.md`: This documentation file

## CustomerRecord Structure

The `CustomerRecord` class matches the COBOL data structure exactly:

```java
public class CustomerRecord {
    private int customerId;           // 5-digit customer ID
    private String customerLastName;  // 50-character last name
    private String customerFirstName; // 50-character first name  
    private int customerContractId;   // 5-digit contract ID
    private String customerComment;   // 25-character comment
}
```

## File Format

The program uses fixed-width file format matching the COBOL implementation:
- Customer ID: 5 digits, zero-padded
- Last Name: 50 characters, left-aligned
- First Name: 50 characters, left-aligned
- Contract ID: 5 digits, zero-padded
- Comment: 25 characters, left-aligned

Total record length: 135 characters per line

## Technical Implementation

### File-Based Operations
The Java implementation uses file-based operations rather than loading all data into memory:
- `BufferedReader` for sequential file reading
- `BufferedWriter` for file output
- Try-with-resources blocks for proper resource management

### Sorting Logic
- **Merge operation**: Uses `Comparator.comparingInt(CustomerRecord::getCustomerId)` for ascending customer ID sort
- **Sort operation**: Uses `Comparator.comparingInt(CustomerRecord::getCustomerContractId).reversed()` for descending contract ID sort

### External Sorting Capability
The implementation can handle larger datasets through:
- Sequential file processing
- Memory-efficient record-by-record reading
- Separate intermediate files for each operation

## Usage

### Compilation and Execution
```bash
javac *.java
java MergeSortProgram
```

### Expected Output
```
Creating test data files...
Merging and sorting files...
[11 customer records sorted by customer ID ascending]
Sorting merged file on descending contract id....
[11 customer records sorted by contract ID descending]
Done.
```

## Verification

The Java implementation has been verified to produce identical output to the COBOL program:

```bash
# Compare all output files
diff test-file-1.txt ../merge_sort/test-file-1.txt     # ✓ Match
diff test-file-2.txt ../merge_sort/test-file-2.txt     # ✓ Match  
diff merge-output.txt ../merge_sort/merge-output.txt   # ✓ Match
diff sorted-contract-id.txt ../merge_sort/sorted-contract-id.txt # ✓ Match
```

## Test Data

### File 1 (6 records):
- Customer IDs: 1, 5, 10, 50, 25, 75
- Contract IDs: 5423, 12323, 653, 5050, 7725, 1175

### File 2 (5 records):
- Customer IDs: 999, 3, 30, 85, 24
- Contract IDs: 1610, 3331, 8765, 4567, 247

### Final Sort Order (by Contract ID descending):
1. 12323 (Customer 5)
2. 8765 (Customer 30)
3. 7725 (Customer 25)
4. 5423 (Customer 1)
5. 5050 (Customer 50)
6. 4567 (Customer 85)
7. 3331 (Customer 3)
8. 1610 (Customer 999)
9. 1175 (Customer 75)
10. 653 (Customer 10)
11. 247 (Customer 24)

## Key Features

- **Exact COBOL Replication**: Produces identical output to the original COBOL program
- **File-Based Processing**: Uses external files rather than in-memory operations
- **Fixed-Width Format**: Maintains COBOL's fixed-width record format
- **Resource Management**: Proper file handle management with try-with-resources
- **Error Handling**: IOException handling for file operations
- **Extensible Design**: Can be easily modified for different record structures or sorting criteria
