# Java Migration Plan for COBOL Merge Sort

## Overview
Migrate the COBOL merge sort functionality to Java while maintaining exact same functionality and file formats.

## 1. Customer Class Structure
```java
public class Customer {
    private int customerId;        // pic 9(5) - 5 digit number
    private String lastName;       // pic x(50) - 50 character string
    private String firstName;      // pic x(50) - 50 character string  
    private int contractId;        // pic 9(5) - 5 digit number
    private String comment;        // pic x(25) - 25 character string
    
    // Constructor, getters, setters, toString
}
```

## 2. File Structure (Same as COBOL)
- `test-file-1.txt` - Input file 1
- `test-file-2.txt` - Input file 2  
- `merge-output.txt` - Merged file sorted by customer ID ascending
- `sorted-contract-id.txt` - Final file sorted by contract ID descending

## 3. Test Data Creation
### File 1 Records:
- Customer 1: last-1, first-1, contract 5423, comment-1
- Customer 5: last-5, first-5, contract 12323, comment-5
- Customer 10: last-10, first-10, contract 653, comment-10
- Customer 50: last-50, first-50, contract 5050, comment-50
- Customer 25: last-25, first-25, contract 7725, comment-25
- Customer 75: last-75, first-75, contract 1175, comment-75

### File 2 Records:
- Customer 999: last-999, first-999, contract 1610, comment-99
- Customer 3: last-03, first-03, contract 3331, comment-03
- Customer 30: last-30, first-30, contract 8765, comment-30
- Customer 85: last-85, first-85, contract 4567, comment-85
- Customer 24: last-24, first-24, contract 247, comment-24

## 4. Main Program Flow
```java
public static void main(String[] args) {
    createTestData();
    mergeAndDisplayFiles();
    sortAndDisplayFile();
    System.out.println("Done.");
}
```

## 5. Implementation Strategy

### File I/O Operations
- Use `BufferedWriter` for writing test data files
- Use `BufferedReader` for reading files
- Parse each line into Customer objects
- Format output to match COBOL record format

### Merge Functionality
- Read both input files into separate lists
- Implement manual merge algorithm comparing customer IDs
- Sort by ascending customer ID using `Collections.sort()`
- Write merged results to `merge-output.txt`
- Display each merged record

### Sort Functionality  
- Read merged file into list of Customer objects
- Sort by descending contract ID using `Collections.sort()` with custom comparator
- Write sorted results to `sorted-contract-id.txt`
- Display each sorted record

## 6. Key Challenges
- COBOL uses fixed-width fields, Java needs to handle variable-length strings
- COBOL's built-in MERGE/SORT vs Java's manual implementation
- Exact format matching for file output
- Proper string padding/trimming to match COBOL behavior

## 7. File Format Considerations
- Each record should be on a single line
- Fields should be space-separated or formatted consistently
- Need to handle string padding to match COBOL's fixed-width behavior
