# COBOL Merge Sort Migration to Java

This Java implementation migrates the merge sort functionality from the COBOL program `merge_sort_test.cbl` to Java while maintaining the exact same file-based processing behavior.

## Implementation Details

### Data Model
- `CustomerRecord.java`: Represents the customer record structure with fields matching the COBOL implementation:
  - Customer ID (5 digits)
  - Last Name (50 characters)
  - First Name (50 characters) 
  - Contract ID (5 digits)
  - Comment (25 characters)

### File Processing
- Maintains the file-based approach from COBOL
- Input files: `test-file-1.txt` and `test-file-2.txt`
- Output files: `merge-output.txt` and `sorted-contract-id.txt`
- Uses `BufferedReader` and `BufferedWriter` for file operations

### Processing Flow
1. **Create Test Data**: Generates the same test data as the COBOL program
2. **Merge Files**: Combines two input files sorted by ascending customer ID
3. **Sort File**: Sorts the merged file by descending contract ID
4. **Display Results**: Shows the processed records for validation

## Running the Implementation

```bash
cd java-implementation/src/main/java
javac com/example/mergesort/*.java
java com.example.mergesort.MergeSortProcessor
```

## Test Data

### test-file-1.txt (East region)
- Customer ID 1: last-1, first-1, contract 5423, comment-1
- Customer ID 5: last-5, first-5, contract 12323, comment-5
- Customer ID 10: last-10, first-10, contract 653, comment-10
- Customer ID 50: last-50, first-50, contract 5050, comment-50
- Customer ID 25: last-25, first-25, contract 7725, comment-25
- Customer ID 75: last-75, first-75, contract 1175, comment-75

### test-file-2.txt (West region)
- Customer ID 999: last-999, first-999, contract 1610, comment-99
- Customer ID 3: last-03, first-03, contract 3331, comment-03
- Customer ID 30: last-30, first-30, contract 8765, comment-30
- Customer ID 85: last-85, first-85, contract 4567, comment-85
- Customer ID 24: last-24, first-24, contract 247, comment-24

## Expected Output

The program will display the merged records sorted by customer ID, then the same records sorted by contract ID in descending order, matching the behavior of the original COBOL program.
