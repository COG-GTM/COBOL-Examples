# COBOL Merge/Sort Program - Java Migration

This is a Java implementation of the COBOL merge and sort program from `merge_sort_test.cbl`.

## Overview

The program demonstrates file merging and sorting operations:
1. Creates two test data files (east and west customer records)
2. Merges them sorted by customer ID (ascending)
3. Sorts the merged file by contract ID (descending)

## Record Structure

Each record is 135 characters in fixed-width format:
- Customer ID: 5 characters (zero-padded)
- Last Name: 50 characters (space-padded)
- First Name: 50 characters (space-padded)
- Contract ID: 5 characters (zero-padded)
- Comment: 25 characters (space-padded)

## Requirements

- Java 11 or higher
- Maven 3.6 or higher

## Compilation and Execution

### Using Maven:

```bash
# Compile the project
mvn clean compile

# Run the program
mvn exec:java -Dexec.mainClass="com.cobol.examples.MergeSortProgram"

# Or use the shorter form
mvn exec:java
```

### Using javac directly:

```bash
cd src/main/java
javac com/cobol/examples/*.java
cd ../../..
java -cp src/main/java com.cobol.examples.MergeSortProgram
```

## Testing

Run the test suite:

```bash
mvn test
```

The tests verify:
- Test data is created correctly (6 east records, 5 west records)
- Merge produces 11 records sorted by customer ID ascending
- Sort produces 11 records sorted by contract ID descending
- Fixed-width formatting is correct (135 characters per line)
- Output matches expected results

## Verification Against COBOL

To verify the Java implementation produces identical output:

1. Run the COBOL program:
```bash
cd ..
cobc -x merge_sort_test.cbl -o merge_sort_test
./merge_sort_test
cp merge-output.txt java/merge-output-cobol.txt
cp sorted-contract-id.txt java/sorted-contract-id-cobol.txt
```

2. Run the Java program:
```bash
cd java
mvn exec:java
```

3. Compare outputs:
```bash
diff merge-output.txt merge-output-cobol.txt
diff sorted-contract-id.txt sorted-contract-id-cobol.txt
```

Both diffs should show no differences.

## File Output

The program creates the following files:
- `test-file-1.txt`: East customer records (6 records)
- `test-file-2.txt`: West customer records (5 records)
- `merge-output.txt`: Merged records sorted by customer ID ascending (11 records)
- `sorted-contract-id.txt`: Merged records sorted by contract ID descending (11 records)

## Implementation Details

### CustomerRecord.java
Data model class representing a customer record with:
- Fixed-width parsing from 135-character strings
- Fixed-width formatting for output
- Getters and setters for all fields

### FixedWidthFileIO.java
Utility class for file operations:
- `readRecords()`: Reads all records from a file
- `writeRecords()`: Writes records to a file with proper formatting

### MergeSortProgram.java
Main program implementing:
- `createTestData()`: Generates test-file-1.txt and test-file-2.txt
- `mergeAndDisplayFiles()`: Merges files sorted by customer ID ascending
- `sortAndDisplayFile()`: Sorts merged file by contract ID descending

## Test Data

### East File (test-file-1.txt)
- Customer ID 1, Contract ID 5423
- Customer ID 5, Contract ID 12323
- Customer ID 10, Contract ID 653
- Customer ID 50, Contract ID 5050
- Customer ID 25, Contract ID 7725
- Customer ID 75, Contract ID 1175

### West File (test-file-2.txt)
- Customer ID 999, Contract ID 1610
- Customer ID 3, Contract ID 3331
- Customer ID 30, Contract ID 8765
- Customer ID 85, Contract ID 4567
- Customer ID 24, Contract ID 247

### Expected Merge Output (ascending customer ID)
1, 3, 5, 10, 24, 25, 30, 50, 75, 85, 999

### Expected Sort Output (descending contract ID)
12323, 8765, 7725, 5423, 5050, 4567, 3331, 1610, 1175, 653, 247
