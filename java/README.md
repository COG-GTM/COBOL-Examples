# Java Implementation of COBOL Merge Sort

This directory contains a Java implementation of the COBOL merge sort example found in `../merge_sort/merge_sort_test.cbl`.

## Overview

The Java implementation converts COBOL's file-based merge and sort operations to in-memory operations using Java collections and a custom merge sort algorithm. The functionality remains identical to the original COBOL program.

## COBOL to Java Conversion

### Data Structure Mapping

| COBOL Field | COBOL Type | Java Field | Java Type |
|-------------|------------|------------|-----------|
| `f-customer-id` | `pic 9(5)` | `customerId` | `int` |
| `f-customer-last-name` | `pic x(50)` | `lastName` | `String` |
| `f-customer-first-name` | `pic x(50)` | `firstName` | `String` |
| `f-customer-contract-id` | `pic 9(5)` | `contractId` | `int` |
| `f-customer-comment` | `pic x(25)` | `comment` | `String` |

### Operation Mapping

| COBOL Operation | Java Equivalent |
|-----------------|-----------------|
| `MERGE fd-sorting-file ON ASCENDING KEY f-customer-id` | `MergeSort.mergeTwoSortedLists()` with `CustomerIdAscendingComparator` |
| `SORT fd-sorting-file ON DESCENDING KEY f-customer-contract-id` | `MergeSort.mergeSort()` with `ContractIdDescendingComparator` |
| File I/O operations | In-memory `List<Customer>` operations |

## Classes

### Customer.java
- Represents a customer record with all fields from the COBOL structure
- Implements `Comparable<Customer>` for natural ordering by customer ID
- Provides proper `toString()`, `equals()`, and `hashCode()` methods

### MergeSort.java
- Generic merge sort implementation that works with any `Comparator`
- Provides both single-list sorting and two-list merging functionality
- Replaces COBOL's built-in `MERGE` and `SORT` statements

### CustomerIdAscendingComparator.java
- Sorts customers by customer ID in ascending order
- Equivalent to COBOL's `ON ASCENDING KEY f-customer-id`

### ContractIdDescendingComparator.java
- Sorts customers by contract ID in descending order
- Equivalent to COBOL's `ON DESCENDING KEY f-customer-contract-id`

### MergeSortExample.java
- Main class that replicates the exact flow of the COBOL program
- Creates identical test data to the COBOL implementation
- Demonstrates both merge and sort operations

## Compilation and Execution

```bash
# Compile all Java files
cd java
javac -d . com/cobol/examples/mergesort/*.java

# Run the example
java com.cobol.examples.mergesort.MergeSortExample
```

## Expected Output

The program produces the same logical output as the COBOL version:

1. **Merge Phase**: Combines east and west customer data, sorted by ascending customer ID
2. **Sort Phase**: Re-sorts the merged data by descending contract ID

## Key Differences from COBOL

1. **Memory vs File**: Java uses in-memory collections instead of file operations
2. **Type Safety**: Java provides compile-time type checking
3. **Object-Oriented**: Uses classes and objects instead of COBOL's procedural approach
4. **Comparators**: Uses Java's `Comparator` interface instead of COBOL's key clauses
5. **Exception Handling**: Java's exception model vs COBOL's file status checking

## Educational Value

This conversion demonstrates:
- How legacy COBOL sorting algorithms can be modernized
- Translation of procedural file-based operations to object-oriented in-memory operations
- Implementation of merge sort algorithm from scratch
- Use of Java's `Comparator` interface for flexible sorting criteria
