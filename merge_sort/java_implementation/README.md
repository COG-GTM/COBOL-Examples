# Java Implementation of COBOL Merge Sort Example

This directory contains a Java implementation of the COBOL program `merge_sort_test.cbl`, which demonstrates file merging and sorting operations on customer records.

## Files

- `CustomerRecord.java` - Data class representing customer records
- `MergeSortExample.java` - Main program implementing merge and sort operations

## Customer Record Structure

The `CustomerRecord` class represents a customer record with the following fields:
- `customerId` (int) - 5-digit customer identifier
- `customerLastName` (String) - Customer's last name (50 characters)
- `customerFirstName` (String) - Customer's first name (50 characters)
- `customerContractId` (int) - 5-digit contract identifier
- `customerComment` (String) - Comment field (25 characters)

## Program Flow

The main program performs three operations in sequence:

1. **createTestData()** - Generates test data and writes to two input files:
   - `test-file-1.txt` - 6 customer records (IDs: 1, 5, 10, 25, 50, 75)
   - `test-file-2.txt` - 5 customer records (IDs: 3, 24, 30, 85, 999)

2. **mergeAndDisplayFiles()** - Merges the two input files:
   - Reads records from both files
   - Sorts by customer ID in ascending order
   - Writes merged results to `merge-output.txt`
   - Displays merged records to console

3. **sortAndDisplayFile()** - Sorts the merged file:
   - Reads records from `merge-output.txt`
   - Sorts by contract ID in descending order
   - Writes sorted results to `sorted-contract-id.txt`
   - Displays sorted records to console

## Compilation and Execution

To compile the program:
```bash
javac *.java
```

To run the program:
```bash
java MergeSortExample
```

## Key Implementation Details

### File Operations
- Uses `BufferedReader` and `BufferedWriter` with try-with-resources for file I/O
- Replaces COBOL file status variables with Java exception handling

### Merge Logic
- Combines records from both files into a single `List<CustomerRecord>`
- Uses `Comparator.comparing(CustomerRecord::getCustomerId)` for ascending sort

### Sort Logic
- Uses `Comparator.comparing(CustomerRecord::getCustomerContractId).reversed()` for descending sort

### Data Format
- Records are formatted to match COBOL field widths
- `toString()` method uses `String.format()` with proper field padding
- `fromString()` static method parses fixed-width format records

## Differences from COBOL Implementation

1. **In-Memory Collections** - Uses `List<CustomerRecord>` instead of temporary sort descriptor (SD) files
2. **Simplified Test Data** - Test data creation uses Java collections instead of sequential write operations
3. **Exception Handling** - Uses try-catch blocks instead of COBOL file status variables
4. **Stream Processing** - Leverages Java Streams API where appropriate

## Output Files Generated

After running the program, the following files are created:
- `test-file-1.txt` - First input file with test data
- `test-file-2.txt` - Second input file with test data
- `merge-output.txt` - Merged records sorted by customer ID (ascending)
- `sorted-contract-id.txt` - Final output sorted by contract ID (descending)

## Testing

### Unit Tests

The implementation includes comprehensive JUnit tests:

- **CustomerRecordTest.java** - Tests for the CustomerRecord class:
  - Constructor and getter/setter methods
  - toString() formatting
  - fromString() parsing
  - Round-trip serialization/deserialization
  - Invalid input handling

- **MergeSortExampleTest.java** - Integration tests for the main program:
  - Test data file creation
  - Merge operation (ascending customer ID sort)
  - Sort operation (descending contract ID sort)
  - Data consistency between merged and sorted files

To run the unit tests:
```bash
./run_tests.sh
```

All 10 unit tests pass successfully.

### COBOL vs Java Comparison Test

To verify that the Java implementation produces identical output to the original COBOL program:

```bash
./compare_cobol_java.sh
```

This test:
1. Compiles and runs the COBOL program
2. Runs the Java program
3. Compares the output files to ensure they are byte-for-byte identical

**Note**: Requires GnuCOBOL (`cobc`) to be installed.
