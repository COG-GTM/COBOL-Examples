# Merge Sort Example - COBOL to Java Conversion

This directory contains both a COBOL program (`merge_sort_test.cbl`) and its functionally equivalent Java conversion (`MergeSortExample.java`).

## Overview

The program demonstrates file merging and sorting operations by:
1. Creating two test data files with customer records
2. Merging the files sorted by customer ID (ascending)
3. Sorting the merged file by contract ID (descending)

## Files

- **merge_sort_test.cbl** - Original COBOL program
- **MergeSortExample.java** - Java conversion with identical functionality
- **Output Files** (generated when running the program):
  - `test-file-1.txt` - First test data file (6 customer records)
  - `test-file-2.txt` - Second test data file (5 customer records)
  - `merge-output.txt` - Merged and sorted by customer ID (ascending)
  - `sorted-contract-id.txt` - Final output sorted by contract ID (descending)

## Customer Record Structure

Each record contains 135 characters with the following fixed-width fields:

| Field | Type | Width | Format |
|-------|------|-------|--------|
| Customer ID | Numeric | 5 | Zero-padded (e.g., `00001`) |
| Last Name | String | 50 | Right-padded with spaces |
| First Name | String | 50 | Right-padded with spaces |
| Contract ID | Numeric | 5 | Zero-padded (e.g., `05423`) |
| Comment | String | 25 | Right-padded with spaces |

**Example record:**
```
00001last-1                                            first-1                                           05423comment-1                
```

## Running the COBOL Program

### Prerequisites
- GnuCOBOL compiler (`cobc`)

### Compilation and Execution
```bash
cobc -x merge_sort_test.cbl
./merge_sort_test
```

## Running the Java Program

### Prerequisites
- Java Development Kit (JDK) 8 or higher

### Compilation and Execution
```bash
javac MergeSortExample.java
java MergeSortExample
```

## Program Flow

Both programs follow the same three-step process:

### 1. Create Test Data
Creates two files with hardcoded customer records:

**test-file-1.txt** (6 records):
- Customer IDs: 1, 5, 10, 50, 25, 75

**test-file-2.txt** (5 records):
- Customer IDs: 999, 3, 30, 85, 24

### 2. Merge and Display Files
- Reads both test files
- Merges and sorts by customer ID in ascending order
- Writes to `merge-output.txt`
- Displays all merged records to console

**Expected output:** 11 records sorted by customer ID (1, 3, 5, 10, 24, 25, 30, 50, 75, 85, 999)

### 3. Sort and Display File
- Reads the merged file
- Sorts by contract ID in descending order
- Writes to `sorted-contract-id.txt`
- Displays all sorted records to console

**Expected output:** 11 records sorted by contract ID descending (12323, 8765, 7725, 5423, 5050, 4567, 3331, 1610, 1175, 653, 247)

## Java Implementation Details

### Class Structure

```java
public class MergeSortExample {
    // CustomerRecord: Inner class representing a single customer record
    static class CustomerRecord {
        - int customerId
        - String lastName
        - String firstName
        - int contractId
        - String comment
        
        + toFixedWidthString(): String         // Formats to 135-char line
        + fromFixedWidthString(String): CustomerRecord  // Parses line
    }
    
    // Main workflow methods
    - createTestData()           // Creates test-file-1.txt and test-file-2.txt
    - mergeAndDisplayFiles()     // Merges files, sorts by customer ID, displays
    - sortAndDisplayFile()       // Sorts by contract ID, displays
    + main(String[] args)        // Entry point, calls all three methods
}
```

### Key Implementation Choices

1. **Fixed-Width Formatting**: Uses `String.format()` with padding specifiers to match COBOL's PIC clauses
   - Numbers: `%05d` (5-digit zero-padded)
   - Strings: `%-50s` (50-char right-padded with spaces)

2. **Sorting**: Uses Java Collections framework
   - `Comparator.comparingInt()` for ascending sort
   - `.reversed()` for descending sort

3. **File I/O**: Uses `BufferedReader`/`BufferedWriter` for line-by-line operations
   - Matches COBOL's "line sequential" organization
   - One record per line

4. **Error Handling**: Uses try-catch with IOException
   - Prints error messages to stderr
   - Calls `System.exit(1)` on errors (equivalent to COBOL's STOP RUN)

## Verification

To verify functional equivalence between COBOL and Java versions:

```bash
# Run COBOL version
cobc -x merge_sort_test.cbl
./merge_sort_test > cobol_output.txt

# Save COBOL output files
mkdir cobol_output
mv test-file-1.txt test-file-2.txt merge-output.txt sorted-contract-id.txt cobol_output/

# Run Java version
javac MergeSortExample.java
java MergeSortExample > java_output.txt

# Save Java output files
mkdir java_output
mv test-file-1.txt test-file-2.txt merge-output.txt sorted-contract-id.txt java_output/

# Compare outputs
diff cobol_output.txt java_output.txt
diff cobol_output/test-file-1.txt java_output/test-file-1.txt
diff cobol_output/test-file-2.txt java_output/test-file-2.txt
diff cobol_output/merge-output.txt java_output/merge-output.txt
diff cobol_output/sorted-contract-id.txt java_output/sorted-contract-id.txt
```

All files and console output should be identical.

## JIRA Ticket

This conversion was completed for ticket **MBA-18**.

## Notes

- The Java version maintains exact functional equivalence with the COBOL program
- No modernization or refactoring was performed beyond what's necessary for language conversion
- Both programs produce identical output files when executed
- The fixed-width format ensures compatibility with legacy systems expecting COBOL-formatted data
