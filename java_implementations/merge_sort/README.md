# Java Merge Sort Implementation

This directory contains a Java implementation of the merge sort algorithm using a divide-and-conquer approach. This implementation provides a true merge sort algorithm, unlike the COBOL version in `../../merge_sort/merge_sort_test.cbl` which uses built-in `MERGE` and `SORT` statements.

## Files

### CustomerRecord.java
A Java class that represents the customer data structure, mirroring the COBOL `f-customer-record-sort` structure:

- `customerId` (int) - 5-digit customer ID (maps to `f-customer-id`)
- `customerLastName` (String) - Customer's last name, max 50 characters (maps to `f-customer-last-name`)
- `customerFirstName` (String) - Customer's first name, max 50 characters (maps to `f-customer-first-name`)
- `customerContractId` (int) - 5-digit contract ID (maps to `f-customer-contract-id`)
- `customerComment` (String) - Customer comment, max 25 characters (maps to `f-customer-comment`)

The class implements `Comparable<CustomerRecord>` to enable natural ordering by customer ID, with additional fields used as tie-breakers.

### MergeSort.java
The main merge sort implementation featuring:

- **Divide-and-Conquer Algorithm**: True merge sort implementation that recursively divides the array and merges sorted subarrays
- **Time Complexity**: O(n log n) in all cases (best, average, worst)
- **Space Complexity**: O(n) for temporary arrays during merging
- **Multiple Sorting Options**: Can sort by different fields (ID, last name, first name, contract ID)
- **Utility Methods**: Array validation, sorted checking, and printing functions

### MergeSortDemo.java
A demonstration program that:

- Creates test data matching the COBOL example
- Shows sorting by different fields
- Demonstrates performance with larger datasets
- Validates that arrays are properly sorted

## Algorithm Explanation

The merge sort algorithm works through three main steps:

### 1. Divide
The array is recursively divided into two halves until each subarray contains only one element (which is inherently sorted).

### 2. Conquer
Each pair of single-element arrays is merged back together in sorted order.

### 3. Combine
The merging process continues up the recursion tree, combining larger and larger sorted subarrays until the entire array is sorted.

### Key Advantages
- **Stable Sort**: Equal elements maintain their relative order
- **Predictable Performance**: Always O(n log n), regardless of input data
- **Parallelizable**: The divide step can be parallelized for better performance on multi-core systems

## Comparison with COBOL Implementation

| Aspect | COBOL Version | Java Version |
|--------|---------------|--------------|
| **Algorithm** | Built-in `MERGE` and `SORT` statements | True divide-and-conquer merge sort |
| **Implementation** | Language-provided functionality | Manual recursive implementation |
| **Data Structure** | File-based records | In-memory object arrays |
| **Sorting Keys** | Specified in `MERGE`/`SORT` statements | Configurable through `Comparable` interface |
| **Performance** | Optimized by COBOL runtime | O(n log n) with manual optimization opportunities |

## Usage

### Compile the Java files:
```bash
javac *.java
```

### Run the demonstration:
```bash
java MergeSortDemo
```

### Example Output:
```
Java Merge Sort Implementation Demo
===================================

Original Unsorted Data:
=======================
CustomerRecord{id=1, lastName='last-1', firstName='first-1', contractId=5423, comment='comment-1'}
CustomerRecord{id=5, lastName='last-5', firstName='first-5', contractId=12323, comment='comment-5'}
...

Sorting by Customer ID (ascending)...
Sorted by Customer ID:
======================
CustomerRecord{id=1, lastName='last-1', firstName='first-1', contractId=5423, comment='comment-1'}
CustomerRecord{id=3, lastName='last-03', firstName='first-03', contractId=3331, comment='comment-03'}
...
```

## Educational Value

This implementation demonstrates:

1. **Classic Algorithm Implementation**: Shows how to implement merge sort from scratch
2. **Object-Oriented Design**: Proper use of classes, interfaces, and encapsulation
3. **Comparative Analysis**: Highlights differences between built-in language features and manual implementation
4. **Performance Analysis**: Includes timing and validation utilities
5. **Data Structure Mapping**: Shows how to translate between different programming paradigms (COBOL records → Java objects)

## Extensions

The implementation can be extended to include:

- Generic type support for sorting any `Comparable` objects
- Custom `Comparator` support for more flexible sorting criteria
- Parallel merge sort implementation using Java's `ForkJoinPool`
- In-place merge sort variant to reduce space complexity
- Iterative (non-recursive) merge sort implementation
