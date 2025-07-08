# Java Implementation of COBOL Merge Sort

This directory contains a Java implementation of the COBOL merge sort functionality from `merge_sort/merge_sort_test.cbl`, with comprehensive testing that covers error handling, edge cases, performance testing, and regression testing.

## Overview

The Java implementation maintains the same two-step process as the original COBOL code:
1. **Merge** two data sets (East and West regions) by ascending customer ID
2. **Sort** the merged result by descending contract ID

## Project Structure

```
src/
├── main/java/com/example/mergesort/
│   ├── CustomerRecord.java          # Customer record data structure
│   ├── MergeSortProcessor.java      # Core merge sort implementation
│   ├── TestDataFactory.java        # Test data creation matching COBOL
│   └── MergeSortApplication.java    # Main application class
└── test/java/com/example/mergesort/
    ├── CustomerRecordTest.java      # Unit tests for CustomerRecord
    ├── MergeSortProcessorTest.java  # Core functionality tests
    ├── TestDataFactoryTest.java     # Test data validation
    ├── PerformanceTest.java         # Performance benchmarking
    └── RegressionTest.java          # Regression tests vs COBOL
```

## Customer Record Structure

The `CustomerRecord` class matches the COBOL record structure:

| Field | COBOL Type | Java Type | Description |
|-------|------------|-----------|-------------|
| Customer ID | `pic 9(5)` | `int` | 5-digit customer identifier |
| Last Name | `pic x(50)` | `String` | Customer last name (50 chars) |
| First Name | `pic x(50)` | `String` | Customer first name (50 chars) |
| Contract ID | `pic 9(5)` | `int` | 5-digit contract identifier |
| Comment | `pic x(25)` | `String` | Customer comment (25 chars) |

## Test Data

The implementation uses the exact same test data as the COBOL version:

**East Region (6 records):**
- Customer IDs: 1, 5, 10, 25, 50, 75
- Contract IDs: 5423, 12323, 653, 7725, 5050, 1175

**West Region (5 records):**
- Customer IDs: 3, 24, 30, 85, 999
- Contract IDs: 3331, 247, 8765, 4567, 1610

## Building and Running

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

### Build the Project
```bash
mvn compile
```

### Run Tests
```bash
mvn test
```

### Run the Application
```bash
# In-memory processing (default)
mvn exec:java

# File-based processing (matches COBOL approach)
mvn exec:java -Dexec.args="file"
```

### Run Specific Test Categories
```bash
# Performance tests only
mvn test -Dtest=PerformanceTest

# Regression tests only
mvn test -Dtest=RegressionTest
```

## Algorithm Implementation

### Merge Process
The merge operation combines two sorted lists by customer ID in ascending order:
1. Combine all records from East and West regions
2. Sort by customer ID (ascending)
3. Result: merged list ordered by customer ID

### Sort Process
The sort operation reorders the merged list by contract ID in descending order:
1. Take the merged list
2. Sort by contract ID (descending)
3. Result: final sorted list ordered by contract ID

## Testing Coverage

### Normal Operation Tests
- Merge functionality with standard test data
- Sort functionality with various data sets
- Complete workflow integration
- File I/O operations

### Error Handling Tests
- Null input handling
- Empty list processing
- File operation failures
- Invalid data scenarios

### Edge Case Tests
- Single record processing
- Duplicate customer IDs
- Empty data sets
- Boundary conditions

### Performance Tests
- Small data set timing (11 records)
- Medium data set timing (1,000 records)
- Large data set timing (10,000 records)
- Multiple run analysis
- Scalability analysis

### Regression Tests
- Exact COBOL test data verification
- Output format matching
- Sort stability verification
- Result consistency checks

## Performance Characteristics

The implementation includes timing measurements similar to the COBOL `display_timing.cbl` approach:

- Uses `System.nanoTime()` for precise timing
- Measures processing time in nanoseconds, milliseconds, and seconds
- Provides performance analysis across different data sizes
- Includes multiple-run averaging for consistent results

## Differences from COBOL Implementation

1. **Memory Management**: Java uses garbage collection vs COBOL's manual memory management
2. **Data Types**: Java uses objects and collections vs COBOL's fixed-length records
3. **Error Handling**: Java uses exceptions vs COBOL's file status codes
4. **Testing**: Comprehensive JUnit test suite vs basic COBOL display output
5. **Performance**: Java provides nanosecond precision vs COBOL's centisecond timing

## Expected Output

When run with the standard test data, the application produces:

```
Creating test data...
Merging and sorting files...
Merged records (sorted by ascending customer ID):
00001last-1                                           first-1                                          05423comment-1               
00003last-03                                          first-03                                         03331comment-03              
00005last-5                                           first-5                                          12323comment-5               
...

Sorting merged file on descending contract id....
Final sorted records (by descending contract ID):
00005last-5                                           first-5                                          12323comment-5               
00030last-30                                          first-30                                         08765comment-30              
00025last-25                                          first-25                                         07725comment-25              
...

Processing completed in 2.456 milliseconds
Done.
```

## Contributing

When modifying this implementation:

1. Maintain compatibility with the original COBOL behavior
2. Add corresponding tests for any new functionality
3. Update performance benchmarks if algorithm changes are made
4. Ensure regression tests continue to pass
5. Follow Java coding conventions and add Javadoc comments

## License

This implementation follows the same MIT license as the parent COBOL-Examples repository.
