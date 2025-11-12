# COBOL to Java Migration

This directory contains Java implementations of COBOL example programs from the COBOL-Examples repository. Each Java program preserves the logic and functionality of its corresponding COBOL program while using idiomatic Java constructs.

## Overview

This migration demonstrates how to convert COBOL programs to Java, including:
- Data structure conversions (COBOL records to Java POJOs)
- File I/O operations
- Sorting and merging algorithms
- Command-line argument handling
- Report generation with pagination

## Programs

### 1. MergeSortExample.java
**Migrated from:** `merge_sort/merge_sort_test.cbl`

Demonstrates file I/O, merging sorted files, and sorting data.

**Key Features:**
- Creates test data files with customer records
- Merges two sorted files on ascending customer ID
- Sorts merged file on descending contract ID
- Displays results at each step

**Run:**
```bash
javac -d bin src/main/java/com/cobol/examples/*.java
java -cp bin com.cobol.examples.MergeSortExample
```

### 2. CommandLineArgsExample.java
**Migrated from:** `read_command_args/read_specific_cmd_line_args.cbl`

Demonstrates command-line argument handling.

**Key Features:**
- Accepts command-line arguments
- Iterates through all arguments
- Displays each argument value

**Run:**
```bash
javac -d bin src/main/java/com/cobol/examples/*.java
java -cp bin com.cobol.examples.CommandLineArgsExample arg1 arg2 arg3
```

### 3. ReportWriterExample.java
**Migrated from:** `report_writer/report_test.cbl`

Demonstrates report generation with formatting and pagination.

**Key Features:**
- Reads input file with student records
- Generates formatted report with headers
- Implements pagination (66 lines per page)
- Displays page numbers

**Run:**
```bash
javac -d bin src/main/java/com/cobol/examples/*.java
java -cp bin com.cobol.examples.ReportWriterExample
```

## COBOL to Java Mappings

### Data Structure Conversions

#### COBOL Records → Java POJOs

**COBOL:**
```cobol
01  f-customer-record.
    05  f-customer-id                       pic 9(5).
    05  f-customer-last-name                pic x(50).
    05  f-customer-first-name               pic x(50).
    05  f-customer-contract-id              pic 9(5).
    05  f-customer-comment                  pic x(25).
```

**Java:**
```java
public class CustomerRecord {
    private int customerId;           // pic 9(5)
    private String lastName;          // pic x(50)
    private String firstName;         // pic x(50)
    private int contractId;           // pic 9(5)
    private String comment;           // pic x(25)
    
    // Constructor, getters, setters...
}
```

#### PIC Clause Mappings

| COBOL PIC | Java Type | Notes |
|-----------|-----------|-------|
| `pic 9(n)` | `int` | Numeric field with n digits |
| `pic x(n)` | `String` | Alphanumeric field with n characters |
| `pic xxx` | `String` | Fixed-length alphanumeric |
| `pic 99` | `int` | Two-digit numeric |
| `pic 9(6)` | `int` | Six-digit numeric |

### File Operations

#### COBOL File I/O → Java BufferedReader/BufferedWriter

**COBOL:**
```cobol
select fd-test-file-1 assign to "test-file-1.txt"
organization is line sequential
file status is ws-fs-status-1.

open output fd-test-file-1
write f-customer-record-east
close fd-test-file-1
```

**Java:**
```java
try (BufferedWriter writer = new BufferedWriter(new FileWriter("test-file-1.txt"))) {
    for (CustomerRecord record : records) {
        writer.write(record.toString());
        writer.newLine();
    }
}
```

**COBOL:**
```cobol
open input fd-test-file-1
read fd-test-file-1
    at end
        set ws-eof to true
    not at end
        display f-customer-record-east
end-read
close fd-test-file-1
```

**Java:**
```java
try (BufferedReader reader = new BufferedReader(new FileReader("test-file-1.txt"))) {
    String line;
    while ((line = reader.readLine()) != null) {
        CustomerRecord record = CustomerRecord.fromString(line);
        System.out.println(record);
    }
}
```

### Sorting and Merging

#### COBOL MERGE → Java Manual Merge Algorithm

**COBOL:**
```cobol
merge fd-sorting-file
    on ascending key f-customer-id
    of f-customer-record-merged
    using fd-test-file-1 fd-test-file-2 
    giving fd-merged-file
```

**Java:**
```java
List<CustomerRecord> file1Records = readRecordsFromFile("test-file-1.txt");
List<CustomerRecord> file2Records = readRecordsFromFile("test-file-2.txt");

file1Records.sort(Comparator.comparingInt(CustomerRecord::getCustomerId));
file2Records.sort(Comparator.comparingInt(CustomerRecord::getCustomerId));

List<CustomerRecord> mergedRecords = mergeSortedLists(file1Records, file2Records);
writeRecordsToFile("merge-output.txt", mergedRecords);
```

**Merge Algorithm:**
```java
private List<CustomerRecord> mergeSortedLists(List<CustomerRecord> list1, List<CustomerRecord> list2) {
    List<CustomerRecord> merged = new ArrayList<>();
    int i = 0, j = 0;
    
    while (i < list1.size() && j < list2.size()) {
        if (list1.get(i).getCustomerId() <= list2.get(j).getCustomerId()) {
            merged.add(list1.get(i++));
        } else {
            merged.add(list2.get(j++));
        }
    }
    
    while (i < list1.size()) merged.add(list1.get(i++));
    while (j < list2.size()) merged.add(list2.get(j++));
    
    return merged;
}
```

#### COBOL SORT → Java Collections.sort()

**COBOL:**
```cobol
sort fd-sorting-file
    on descending key f-customer-contract-id
    of f-customer-record-sorted-contract-id
    using fd-merged-file 
    giving fd-sorted-contract-id
```

**Java:**
```java
List<CustomerRecord> records = readRecordsFromFile("merge-output.txt");
records.sort(Comparator.comparingInt(CustomerRecord::getContractId).reversed());
writeRecordsToFile("sorted-contract-id.txt", records);
```

### Command-Line Arguments

#### COBOL ACCEPT FROM ARGUMENT → Java args[]

**COBOL:**
```cobol
accept ws-num-args from argument-number

perform varying ws-counter
from 1 by 1 until ws-counter > ws-num-args
    display ws-counter upon argument-number
    accept ws-cmd-args from argument-value
    display ws-cmd-args
end-perform
```

**Java:**
```java
int numArgs = args.length;

for (int counter = 0; counter < numArgs; counter++) {
    String cmdArg = args[counter];
    System.out.println(cmdArg);
}
```

### Report Generation

#### COBOL Report Writer → Java Manual Formatting

**COBOL:**
```cobol
report section.
    rd  r-test-report
    page limit is 66
    heading is 1
    first detail 6
    last detail 42
    footing 52.

01  report-header type report heading.
    05  line 1 column 44
        pic x(21) value "Customer Order Report".
    05  line 2.
        10  column 100
            pic x(4) value "PAGE".
        10  column 105
            pic zz9 source page-counter.

01  report-line type detail line plus 1.
    05  column 4  pic 9(6) source f-test-student-id.
    05  column 15 pic x(20) source f-test-student-name.
    05  column 40 pic xxx source f-test-major.
    05  column 46 pic 99 source f-test-num-courses.
```

**Java:**
```java
private static final int PAGE_LIMIT = 66;
private static final int HEADING_LINE = 1;
private static final int FIRST_DETAIL = 6;
private static final int LAST_DETAIL = 42;
private static final int FOOTING_LINE = 52;

private int currentLine;
private int pageCounter;

private void writePageHeader() throws IOException {
    currentLine = HEADING_LINE;
    
    String headerTitle = "Customer Order Report";
    int titleColumn = 44;
    String titleLine = padLeft("", titleColumn - 1) + headerTitle;
    reportWriter.write(titleLine);
    reportWriter.newLine();
    currentLine++;
    
    String pageLabel = "PAGE";
    int pageLabelColumn = 100;
    int pageNumberColumn = 105;
    String pageNumber = String.format("%3d", pageCounter);
    
    String pageLine = padLeft("", pageLabelColumn - 1) + pageLabel + 
                     padLeft("", pageNumberColumn - pageLabelColumn - pageLabel.length()) + pageNumber;
    reportWriter.write(pageLine);
    reportWriter.newLine();
    currentLine++;
    
    while (currentLine < FIRST_DETAIL) {
        reportWriter.newLine();
        currentLine++;
    }
}

private void generateReportLine(StudentRecord record) throws IOException {
    if (currentLine > LAST_DETAIL) {
        advanceToNextPage();
    }
    
    String detailLine = String.format("   %6d          %-20s     %-3s  %2d",
            record.getStudentId(),
            padRight(record.getStudentName(), 20),
            padRight(record.getMajor(), 3),
            record.getNumCourses());
    
    reportWriter.write(detailLine);
    reportWriter.newLine();
    currentLine++;
}
```

### Control Flow

#### COBOL PERFORM → Java Methods and Loops

**COBOL:**
```cobol
perform create-test-data
perform merge-and-display-files
perform sort-and-display-file

create-test-data.
    display "Creating test data files..."
    * ... logic ...
    exit paragraph.
```

**Java:**
```java
public void run() throws IOException {
    createTestData();
    mergeAndDisplayFiles();
    sortAndDisplayFile();
    System.out.println("Done.");
}

private void createTestData() throws IOException {
    System.out.println("Creating test data files...");
    // ... logic ...
}
```

#### COBOL PERFORM VARYING → Java for Loop

**COBOL:**
```cobol
perform varying ws-counter
from 1 by 1 until ws-counter > ws-num-args
    display ws-counter upon argument-number
    accept ws-cmd-args from argument-value
    display ws-cmd-args
end-perform
```

**Java:**
```java
for (int counter = 0; counter < numArgs; counter++) {
    String cmdArg = args[counter];
    System.out.println(cmdArg);
}
```

#### COBOL PERFORM UNTIL → Java while Loop

**COBOL:**
```cobol
set ws-not-eof to true

perform until ws-eof
    read fd-merged-file
        at end
            set ws-eof to true
        not at end
            display f-customer-record-merged
    end-read
end-perform
```

**Java:**
```java
try (BufferedReader reader = new BufferedReader(new FileReader("merge-output.txt"))) {
    String line;
    while ((line = reader.readLine()) != null) {
        CustomerRecord record = CustomerRecord.fromString(line);
        System.out.println(record);
    }
}
```

## Testing

The migration includes comprehensive unit and integration tests.

### Running Tests

```bash
# Compile all source and test files
javac -cp .:junit-platform-console-standalone-1.9.3.jar -d bin \
    src/main/java/com/cobol/examples/*.java \
    src/test/java/com/cobol/examples/*.java

# Run all tests
java -jar junit-platform-console-standalone-1.9.3.jar \
    --class-path bin \
    --scan-class-path
```

### Test Coverage

#### Unit Tests
- **CustomerRecordTest**: Tests data structure conversions, field access, formatting, and parsing
- **StudentRecordTest**: Tests data structure conversions, field access, formatting, and parsing
- **MergeSortExampleTest**: Tests file creation, merge logic, sort logic, and data integrity
- **CommandLineArgsExampleTest**: Tests argument handling with various inputs
- **ReportWriterExampleTest**: Tests report generation, pagination, and formatting

#### Integration Tests
- **IntegrationTest**: End-to-end tests verifying complete workflows match COBOL behavior
  - Merge and sort complete workflow
  - Data integrity across operations
  - Command-line argument handling
  - Report generation with pagination
  - Empty file handling
  - Record format consistency

## Key Differences Between COBOL and Java

### 1. No Direct MERGE Statement
COBOL has a built-in `MERGE` statement that combines sorted files. Java requires implementing the merge algorithm manually or using library functions.

### 2. No Direct Report Writer
COBOL has a Report Writer feature with declarative formatting. Java requires manual formatting using `String.format()` and line counting for pagination.

### 3. File Organization
COBOL uses file descriptors (FD) and working storage. Java uses object-oriented file I/O with try-with-resources for automatic resource management.

### 4. Fixed-Width Fields
COBOL uses fixed-width fields (PIC clauses). Java uses dynamic strings that must be padded to match COBOL output format.

### 5. Array Indexing
COBOL arrays are 1-indexed. Java arrays are 0-indexed.

### 6. Error Handling
COBOL uses file status codes. Java uses exceptions (IOException, IllegalArgumentException, etc.).

## Building and Running

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- JUnit 5 for running tests

### Compile All Programs
```bash
mkdir -p bin
javac -d bin src/main/java/com/cobol/examples/*.java
```

### Run Individual Programs

**Merge Sort Example:**
```bash
java -cp bin com.cobol.examples.MergeSortExample
```

**Command Line Args Example:**
```bash
java -cp bin com.cobol.examples.CommandLineArgsExample arg1 arg2 arg3
```

**Report Writer Example:**
```bash
# First create an input.txt file or copy from report_writer directory
cp ../report_writer/input.txt .
java -cp bin com.cobol.examples.ReportWriterExample
```

## Output Files

### MergeSortExample
- `test-file-1.txt`: First test data file (6 records)
- `test-file-2.txt`: Second test data file (5 records)
- `merge-output.txt`: Merged and sorted by customer ID (11 records, ascending)
- `sorted-contract-id.txt`: Sorted by contract ID (11 records, descending)

### ReportWriterExample
- `report.txt`: Formatted report with pagination (66 lines per page)

## Notes

### Fixed-Width Format
All output files use fixed-width formatting to match COBOL's PIC clause behavior:
- Customer ID: 5 digits, zero-padded
- Last Name: 50 characters, right-padded with spaces
- First Name: 50 characters, right-padded with spaces
- Contract ID: 5 digits, zero-padded
- Comment: 25 characters, right-padded with spaces
- Student ID: 6 digits, zero-padded
- Student Name: 20 characters, right-padded with spaces
- Major: 3 characters, right-padded with spaces
- Number of Courses: 2 digits, zero-padded

### Compatibility
The Java implementations produce output that is functionally equivalent to the COBOL programs, though exact byte-for-byte output may differ due to:
- Line ending differences (CRLF vs LF)
- Trailing space handling
- Numeric formatting edge cases

The integration tests verify that the logic and data integrity match the COBOL implementations.
