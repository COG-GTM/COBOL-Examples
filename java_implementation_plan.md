# Java Implementation Plan for COBOL SORT/MERGE

## Overview
Convert the COBOL merge_sort_test.cbl program to Java, replicating the exact functionality and output format.

## Key Requirements Analysis
From the COBOL program execution, I observed:
- Fixed-width output format with zero-padded numbers and space-padded strings
- Merge operation combines two files by ascending customer ID
- Sort operation re-sorts merged data by descending contract ID
- Same file naming conventions must be maintained

## Implementation Plan

### 1. Customer Class
Create a Customer class with fields matching the COBOL record structure:
- `customerId` (int) - 5-digit number, zero-padded in output
- `lastName` (String) - 50 characters, space-padded in output  
- `firstName` (String) - 50 characters, space-padded in output
- `contractId` (int) - 5-digit number, zero-padded in output
- `comment` (String) - 25 characters, space-padded in output

Include methods for:
- Parsing from fixed-width string format
- Formatting to fixed-width string format (matching COBOL output)
- Comparators for sorting by customer ID and contract ID

### 2. File I/O Methods
Create utility methods for:
- Reading Customer records from text files
- Writing Customer records to text files in fixed-width format
- Creating the same test data as the COBOL program

### 3. Merge Functionality
Implement merge operation that:
- Reads from two sorted input files (test-file-1.txt, test-file-2.txt)
- Merges by ascending customer ID
- Writes result to merge-output.txt
- Displays merged records to console

### 4. Sort Functionality  
Implement sort operation that:
- Reads from merged file (merge-output.txt)
- Sorts by descending contract ID
- Writes result to sorted-contract-id.txt
- Displays sorted records to console

### 5. Main Program Flow
Replicate the exact COBOL program flow:
1. Create test data files
2. Merge files and display results
3. Sort merged file and display results
4. Display "Done."

### 6. File Naming Conventions
Maintain exact same file names:
- Input: test-file-1.txt, test-file-2.txt
- Output: merge-output.txt, sorted-contract-id.txt

### 7. Output Format Matching
Ensure Java output exactly matches COBOL output:
- Customer ID: 5-digit zero-padded (e.g., "00001")
- Last Name: 50-character space-padded
- First Name: 50-character space-padded  
- Contract ID: 5-digit zero-padded (e.g., "05423")
- Comment: 25-character space-padded

## Implementation Files
- `MergeSortExample.java` - Main class with program logic
- `Customer.java` - Customer record class
- `FileUtils.java` - File I/O utility methods

## Testing Strategy
- Run Java program and compare output with COBOL program output
- Verify all generated files match expected format
- Ensure merge and sort operations produce identical results
