# COBOL to Java Migration

This project contains Java implementations of 5 COBOL example programs from the COBOL-Examples repository. The programs have been migrated to demonstrate equivalent functionality using modern Java practices.

## Overview

The migration maintains the original functionality of each COBOL program while adapting to Java idioms and best practices. Each program has been implemented as a standalone Java class with proper logging, error handling, and documentation.

## Programs Migrated

### 1. NumvalTest (Simplest)
**Original COBOL:** `numval_test/numval_test.cbl`

**Functionality:** Accepts two numeric inputs from the user and displays their sum.

**Java Implementation:**
- Uses `Scanner` for user input
- Uses `Double.parseDouble()` for string-to-numeric conversion
- Includes proper error handling for invalid numeric input
- Implements SLF4J logging

**How to Run:**
```bash
mvn exec:java -Dexec.mainClass="com.cobol.migration.NumvalTest"
```

### 2. CommandLineArgs
**Original COBOL:** `read_command_args/read_specific_cmd_line_args.cbl`

**Functionality:** Loops through and displays all command-line arguments.

**Java Implementation:**
- Uses `String[] args` from main method
- Iterates through arguments using a for loop
- Logs each argument for debugging

**How to Run:**
```bash
mvn exec:java -Dexec.mainClass="com.cobol.migration.CommandLineArgs" -Dexec.args="arg1 arg2 arg3"
```

### 3. DisplayTiming
**Original COBOL:** `display_timing/display_timing.cbl`

**Functionality:** Measures execution time for display operations and calculates averages for performance measurement.

**Java Implementation:**
- Uses `System.nanoTime()` for high-precision timing measurements
- Uses `ArrayList` to store timing results
- Implements averaging calculations with proper formatting
- Compares two different display methods

**How to Run:**
```bash
mvn exec:java -Dexec.mainClass="com.cobol.migration.DisplayTiming"
```

### 4. MergeSortTest
**Original COBOL:** `merge_sort/merge_sort_test.cbl`

**Functionality:** Creates test data files with customer records, merges and sorts them by different criteria.

**Java Implementation:**
- Created `Customer` POJO class matching COBOL record structure
- Uses `BufferedReader`/`BufferedWriter` for file I/O
- Uses `Comparator` for sorting operations
- Implements both ascending (by customer ID) and descending (by contract ID) sorts
- Proper exception handling with descriptive error messages

**How to Run:**
```bash
mvn exec:java -Dexec.mainClass="com.cobol.migration.MergeSortTest"
```

**Output Files:**
- `test-file-1.txt` - East region customer data
- `test-file-2.txt` - West region customer data
- `merge-output.txt` - Merged and sorted by customer ID (ascending)
- `sorted-contract-id.txt` - Sorted by contract ID (descending)

### 5. ReportWriter
**Original COBOL:** `report_writer/report_test.cbl`

**Functionality:** Reads student data from an input file and generates a formatted report.

**Java Implementation:**
- Created `Student` POJO class for student records
- Uses `String.format()` for text report formatting
- Implements pagination with headers and footers
- Handles missing input files gracefully

**How to Run:**
```bash
# First, create an input file (or the program will handle missing file gracefully)
mvn exec:java -Dexec.mainClass="com.cobol.migration.ReportWriter"
```

**Input File Format (`input.txt`):**
Each line should contain: StudentID (6 digits), Name (20 chars), Major (3 chars), NumCourses (2 digits)

Example:
```
123456John Doe           CSC12
234567Jane Smith         MAT08
```

## Project Structure

```
java-migration/
├── pom.xml                          # Maven project configuration
├── README.md                        # This file
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── cobol/
        │           └── migration/
        │               ├── NumvalTest.java
        │               ├── CommandLineArgs.java
        │               ├── DisplayTiming.java
        │               ├── MergeSortTest.java
        │               ├── ReportWriter.java
        │               ├── Customer.java      # POJO for customer records
        │               └── Student.java       # POJO for student records
        └── resources/
            └── logback.xml              # Logging configuration
```

## Key COBOL-to-Java Mappings

The following mappings were used during migration:

| COBOL Concept | Java Equivalent |
|---------------|-----------------|
| `WORKING-STORAGE SECTION` | Class fields/instance variables |
| `PIC 9(n)` | `int`, `long`, or `BigDecimal` |
| `PIC X(n)` | `String` |
| `COMP-2` | `double` |
| `PERFORM` loops | `for`, `while`, or `do-while` loops |
| `ACCEPT`/`DISPLAY` | `Scanner`/`System.out.println()` |
| File operations | `java.nio.file` classes |
| `SORT`/`MERGE` | `Collections.sort()`, `Comparator` |
| File status codes | Java exceptions with try-catch |

## Dependencies

The project uses the following dependencies:

- **SLF4J 2.0.9** - Logging API
- **Logback 1.4.11** - Logging implementation
- **Apache Commons IO 2.15.0** - File operations utilities
- **JUnit Jupiter 5.10.0** - Testing framework (test scope)

## Building the Project

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

### Compile
```bash
mvn clean compile
```

### Package
```bash
mvn clean package
```

### Run Tests
```bash
mvn test
```

## Important Considerations

1. **Data Types**: The migration uses `double` for floating-point arithmetic. For financial calculations in production code, consider using `BigDecimal` for fixed-precision decimal arithmetic (as COBOL uses).

2. **File Handling**: COBOL's sequential file organization maps to Java's line-by-line reading with try-with-resources for proper resource management.

3. **Error Handling**: COBOL file status codes have been converted to Java exceptions with proper exception handling and logging.

4. **Screen Positioning**: COBOL's screen handling features have been simplified for Java console output, as Java doesn't have direct equivalents for COBOL's screen positioning capabilities.

## Logging

The project uses SLF4J with Logback for logging. Log configuration can be modified in `src/main/resources/logback.xml`.

Default log level is INFO. To see DEBUG logs, modify the logback.xml configuration.

## Author

Migrated from COBOL examples originally created by Erik Eriksen.

## License

This project maintains the same license as the original COBOL-Examples repository.
