# COBOL Examples - Java Migration

This document describes the Java migration of the COBOL example programs. All 5 standalone COBOL programs have been migrated to Java while maintaining functional equivalence.

## Project Structure

```
src/
├── main/java/com/cobolexamples/
│   ├── args/CommandArgsExample.java          (from read_command_args)
│   ├── conversion/NumvalTest.java            (from numval_test)
│   ├── timing/DisplayTiming.java             (from display_timing)
│   ├── sorting/
│   │   ├── CustomerRecord.java
│   │   └── MergeSortExample.java             (from merge_sort)
│   └── reporting/ReportTest.java             (from report_writer)
├── test/java/
└── resources/
```

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

## Building the Project

To compile all Java programs:

```bash
mvn clean compile
```

To create JAR files:

```bash
mvn clean package
```

## Running the Programs

### 1. CommandArgsExample (Command Line Arguments)

**Original COBOL:** `read_command_args/read_specific_cmd_line_args.cbl`

**Description:** Reads and displays command line arguments.

**How to run:**

```bash
# Using Maven
mvn exec:java -Dexec.mainClass="com.cobolexamples.args.CommandArgsExample" -Dexec.args="arg1 arg2 arg3"

# Using compiled classes
java -cp target/classes com.cobolexamples.args.CommandArgsExample arg1 arg2 arg3
```

**Example output:**
```
arg1
arg2
arg3
```

### 2. NumvalTest (Numeric Value Conversion)

**Original COBOL:** `numval_test/numval_test.cbl`

**Description:** Converts string input to numeric values and performs arithmetic operations.

**How to run:**

```bash
# Using Maven
mvn exec:java -Dexec.mainClass="com.cobolexamples.conversion.NumvalTest"

# Using compiled classes
java -cp target/classes com.cobolexamples.conversion.NumvalTest
```

**Example interaction:**
```
Enter first number: 123.45
Enter second number: 67.89
Total: 191.34
```

### 3. DisplayTiming (Performance Timing)

**Original COBOL:** `display_timing/display_timing.cbl`

**Description:** Measures execution time with millisecond precision for display operations.

**How to run:**

```bash
# Using Maven
mvn exec:java -Dexec.mainClass="com.cobolexamples.timing.DisplayTiming"

# Using compiled classes
java -cp target/classes com.cobolexamples.timing.DisplayTiming
```

**Example output:**
```
Press enter to start...
Running first timing test (using simple display)...
Run 1: 1234 ms
Run 2: 1198 ms
...
Average time: 1215.50 ms
```

### 4. MergeSortExample (File Merging and Sorting)

**Original COBOL:** `merge_sort/merge_sort_test.cbl`

**Description:** Creates test data files, merges them sorted by customer ID, then sorts by contract ID.

**How to run:**

```bash
# Using Maven
mvn exec:java -Dexec.mainClass="com.cobolexamples.sorting.MergeSortExample"

# Using compiled classes
java -cp target/classes com.cobolexamples.sorting.MergeSortExample
```

**Example output:**
```
Creating test data files...
Merging and sorting files...
1    last-1    first-1    5423 comment-1
3    last-03   first-03   3331 comment-03
...
Sorting merged file on descending contract id....
30   last-30   first-30   8765 comment-30
...
Done.
```

**Generated files:**
- `test-file-1.txt` - East region customer data
- `test-file-2.txt` - West region customer data
- `merge-output.txt` - Merged and sorted by customer ID
- `sorted-contract-id.txt` - Sorted by contract ID (descending)

### 5. ReportTest (Report Generation)

**Original COBOL:** `report_writer/report_test.cbl`

**Description:** Reads student records from an input file and generates a formatted report.

**Prerequisites:** Create an `input.txt` file with student records in the following format:
```
<6-digit student ID><20-char name><3-char major><2-digit num courses>
```

**Example input.txt:**
```
123456John Doe            CSC12
234567Jane Smith          MAT15
345678Bob Johnson         PHY10
```

**How to run:**

```bash
# Using Maven
mvn exec:java -Dexec.mainClass="com.cobolexamples.reporting.ReportTest"

# Using compiled classes
java -cp target/classes com.cobolexamples.reporting.ReportTest
```

**Example output:**
```
Starting test report program.
Init test report.
Generate report line.
Generate report line.
Terminate report.
Done.
```

**Generated files:**
- `report.txt` - Formatted report with headers and page numbers

## Data Type Mappings

The following mappings were used during migration:

| COBOL Type | Java Type |
|------------|-----------|
| PIC 9(n) | int or long |
| PIC X(n) | String |
| COMP-1 | float |
| COMP-2 | double |

## Control Flow Mappings

| COBOL Construct | Java Construct |
|-----------------|----------------|
| PERFORM UNTIL | while or for loops |
| IF...END-IF | if statements |
| Paragraphs | Methods |

## File Operations Mappings

| COBOL Operation | Java Operation |
|-----------------|----------------|
| OPEN/CLOSE | try-with-resources |
| READ/WRITE | BufferedReader/BufferedWriter |
| File status codes | Exception handling |

## Notes

- Each Java program maintains functional equivalence with its COBOL counterpart
- Fixed-width record formats from COBOL are preserved in the Java implementations
- File I/O operations use Java's standard BufferedReader/BufferedWriter classes
- All programs are independent and can be run separately
- The Java implementations use standard Java libraries without external dependencies

## Testing

To verify all programs compile successfully:

```bash
mvn clean compile
```

To run all programs sequentially (requires appropriate input files):

```bash
# Run each program individually as shown in the sections above
```

## License

This project maintains the same license as the original COBOL-Examples repository.
