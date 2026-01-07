# Merge Sort Example - Java Implementation

This is a Java implementation of the COBOL `merge_sort_test.cbl` program.

## Original COBOL Program

The original COBOL program (`../merge_sort/merge_sort_test.cbl`) demonstrates the COBOL SORT and MERGE statements for sorting and merging customer records from multiple files.

## Purpose

This Java program replicates the exact functionality of the COBOL program:

1. **Create Test Data**: Creates two test files with customer records
   - `test-file-1.txt` (East region): 6 customer records
   - `test-file-2.txt` (West region): 5 customer records

2. **Merge and Sort**: Merges both files and sorts by customer ID (ascending)
   - Output: `merge-output.txt`

3. **Sort by Contract ID**: Takes the merged file and sorts by contract ID (descending)
   - Output: `sorted-contract-id.txt`

## Data Structure

Each customer record contains:
- Customer ID (5 digits)
- Last Name (50 characters)
- First Name (50 characters)
- Contract ID (5 digits)
- Comment (25 characters)

## Compilation and Execution

```bash
# Compile
javac MergeSortExample.java

# Run
java MergeSortExample
```

## Expected Output

```
Creating test data files...
Merging and sorting files...
00001last-1                                             first-1                                            05423comment-1                
00003last-03                                            first-03                                           03331comment-03               
00005last-5                                             first-5                                            12323comment-5                
00010last-10                                            first-10                                           00653comment-10               
00024last-24                                            first-24                                           00247comment-24               
00025last-25                                            first-25                                           07725comment-25               
00030last-30                                            first-30                                           08765comment-30               
00050last-50                                            first-50                                           05050comment-50               
00075last-75                                            first-75                                           01175comment-75               
00085last-85                                            first-85                                           04567comment-85               
00999last-999                                           first-999                                          01610comment-99               
Sorting merged file on descending contract id....
00005last-5                                             first-5                                            12323comment-5                
00030last-30                                            first-30                                           08765comment-30               
00025last-25                                            first-25                                           07725comment-25               
00001last-1                                             first-1                                            05423comment-1                
00050last-50                                            first-50                                           05050comment-50               
00085last-85                                            first-85                                           04567comment-85               
00003last-03                                            first-03                                           03331comment-03               
00999last-999                                           first-999                                          01610comment-99               
00075last-75                                            first-75                                           01175comment-75               
00010last-10                                            first-10                                           00653comment-10               
00024last-24                                            first-24                                           00247comment-24               
Done.
```

## Comparison with COBOL

| COBOL Feature | Java Equivalent |
|---------------|-----------------|
| `MERGE ... ON ASCENDING KEY` | `Collections.sort()` with `Comparator` |
| `SORT ... ON DESCENDING KEY` | `Collections.sort()` with reversed `Comparator` |
| `PIC 9(5)` | `int` with `String.format("%05d", ...)` |
| `PIC X(50)` | `String` with padding |
| File I/O with `READ`/`WRITE` | `BufferedReader`/`PrintWriter` |
| `WORKING-STORAGE SECTION` | Class instance variables |
