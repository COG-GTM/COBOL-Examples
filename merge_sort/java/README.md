# Java Migration of COBOL merge_sort_test.cbl

This directory contains a Java migration of the COBOL `merge_sort_test.cbl` program. The Java implementation replicates the functionality of the original COBOL program, which performs file merging and sorting operations on customer records.

## Overview

The COBOL program performs three main operations:
1. **Creates test data** - Generates two input files with customer records (East and West regions)
2. **Merges files** - Combines two sorted input files into one sorted output file
3. **Sorts merged file** - Sorts the merged output by a different key (contract ID in descending order)

## Java Classes

The migration consists of 6 Java classes organized in the `com.example.mergesort` package:

### 1. CustomerRecord.java (Data Model)
A POJO representing a customer record with the following fields:
- `customerId` (int, 5 digits)
- `lastName` (String, 50 characters)
- `firstName` (String, 50 characters)
- `contractId` (int, 5 digits)
- `comment` (String, 25 characters)

The class includes:
- Getters and setters for all fields
- Constructor for creating records
- `toString()` method for formatting records (matches COBOL fixed-width format)
- `fromString()` static method for parsing records from file
- Automatic padding/truncation to match COBOL field lengths

### 2. FileManager.java (I/O Operations)
Handles reading and writing customer records to files. Includes:
- `writeRecords(List<CustomerRecord> records, String filename)` - writes records to a file
- `readRecords(String filename)` - returns `List<CustomerRecord>` by reading from a file
- `FileStatus` inner class - mirrors COBOL's `ws-fs-status-*` variables for error handling
- `checkFileStatus(FileStatus status)` - validates file operation status

### 3. TestDataGenerator.java
Creates sample customer data matching the COBOL program's test data:
- `generateEastRegionData()` - returns 6 customer records for the East region
- `generateWestRegionData()` - returns 5 customer records for the West region

The test data exactly matches the COBOL program's hardcoded test records.

### 4. FileMerger.java
Merges two sorted lists into one sorted list:
- `merge(List<CustomerRecord> file1, List<CustomerRecord> file2, Comparator<CustomerRecord> comparator)` - returns merged list
- Uses a two-pointer algorithm for efficient merging
- Accepts a `Comparator` for flexible sorting criteria

### 5. FileSorter.java
Sorts records by specified key:
- `sort(List<CustomerRecord> records, Comparator<CustomerRecord> comparator)` - returns sorted list
- Uses Java's built-in `Collections.sort()` for efficient sorting
- Accepts a `Comparator` for flexible sorting criteria

### 6. MergeSortExample.java (Main Class)
Orchestrates the workflow with the following methods:
- `createTestData()` - generates and writes test files
- `mergeAndDisplayFiles()` - merges files sorted by customer ID (ascending) and displays results
- `sortAndDisplayFile()` - sorts merged file by contract ID (descending) and displays results
- `main()` - executes the complete workflow

## Compilation and Execution

### Compile
```bash
cd merge_sort/java
javac com/example/mergesort/*.java
```

### Run
```bash
java com.example.mergesort.MergeSortExample
```

## Output Files

The program generates the following files:
- `test-file-1.txt` - East region customer records
- `test-file-2.txt` - West region customer records
- `merge-output.txt` - Merged records sorted by customer ID (ascending)
- `sorted-contract-id.txt` - Merged records sorted by contract ID (descending)

## Key Differences from COBOL

1. **Sort Descriptor**: The COBOL program uses a Sort Descriptor (`sd fd-sorting-file`) as temporary working storage. The Java implementation uses in-memory `List<CustomerRecord>` collections instead.

2. **EOF Handling**: The COBOL program uses an EOF switch pattern (`ws-eof-sw`). Java replaces this with iterator-based file reading and try-with-resources blocks.

3. **File Status**: COBOL uses file status codes (`ws-fs-status-*`). Java uses a `FileStatus` class with similar functionality for error handling.

4. **Fixed-Width Records**: COBOL uses fixed-width fields (PIC clauses). Java implements padding/truncation in the `CustomerRecord` class to maintain compatibility.

5. **Sorting**: COBOL uses built-in `SORT` and `MERGE` statements. Java uses `Collections.sort()` and a custom merge algorithm with `Comparator` interfaces.

## Migration Notes

- The Java implementation maintains the same logical flow as the COBOL program
- Test data is identical to the COBOL version
- File formats are compatible (fixed-width records)
- Error handling mirrors COBOL's file status checking
- Output format matches COBOL's display format
