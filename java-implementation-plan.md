# Java Implementation Plan for COBOL Merge Sort Migration

## Overview
This document outlines the migration of COBOL merge and sort functionality to Java using proper object-oriented design principles.

## COBOL Analysis Summary

### Data Structure
- Customer record with 5 fields:
  - customer-id: 5-digit integer
  - last-name: 50-character string
  - first-name: 50-character string
  - contract-id: 5-digit integer
  - comment: 25-character string

### Workflow
1. Create test data files (test-file-1.txt, test-file-2.txt)
2. Merge files sorted by ascending customer ID → merge-output.txt
3. Sort merged file by descending contract ID → sorted-contract-id.txt
4. Display results after each operation

### Test Data
- **File 1**: 6 records with customer IDs: 1, 5, 10, 50, 25, 75
- **File 2**: 5 records with customer IDs: 999, 3, 30, 85, 24

## Java OOP Design

### 1. Customer Class (Data Model)
```java
public class Customer {
    private int customerId;
    private String lastName;
    private String firstName;
    private int contractId;
    private String comment;
    
    // Constructor, getters, setters, toString, equals, hashCode
    // Validation for field lengths and constraints
}
```

### 2. FileProcessor Service
```java
public class FileProcessor {
    public void writeCustomersToFile(List<Customer> customers, String filename)
    public List<Customer> readCustomersFromFile(String filename)
    public void displayCustomers(List<Customer> customers, String operation)
}
```

### 3. MergeSortService
```java
public class MergeSortService {
    public List<Customer> mergeFilesSortedByCustomerId(String file1, String file2)
    public List<Customer> sortByContractIdDescending(List<Customer> customers)
}
```

### 4. TestDataGenerator
```java
public class TestDataGenerator {
    public void createTestFiles()
    // Creates the exact same test data as COBOL program
}
```

### 5. Main Application
```java
public class MergeSortApplication {
    public static void main(String[] args) {
        // Orchestrates the complete workflow
        // 1. Generate test data
        // 2. Merge and sort by customer ID
        // 3. Sort by contract ID descending
        // 4. Display results
    }
}
```

## Key Design Principles

### Avoiding "JOBOL" Anti-patterns
- ✅ Use Java Collections (List, Stream) instead of arrays
- ✅ Use proper encapsulation with private fields and public methods
- ✅ Use Java Streams for sorting instead of procedural loops
- ✅ Use proper exception handling instead of status codes
- ✅ Use dependency injection patterns where appropriate
- ✅ Follow Java naming conventions (camelCase)

### Modern Java Features
- Use Java 8+ Streams for sorting and filtering
- Use Comparator.comparing() for clean sorting logic
- Use try-with-resources for file handling
- Use Optional for null safety where appropriate

## File Structure
```
src/
├── main/
│   └── java/
│       └── com/
│           └── cognition/
│               └── cobol/
│                   └── migration/
│                       ├── model/
│                       │   └── Customer.java
│                       ├── service/
│                       │   ├── FileProcessor.java
│                       │   ├── MergeSortService.java
│                       │   └── TestDataGenerator.java
│                       └── MergeSortApplication.java
└── test/
    └── java/
        └── com/
            └── cognition/
                └── cobol/
                    └── migration/
                        ├── model/
                        │   └── CustomerTest.java
                        ├── service/
                        │   ├── FileProcessorTest.java
                        │   ├── MergeSortServiceTest.java
                        │   └── TestDataGeneratorTest.java
                        └── MergeSortApplicationTest.java
```

## Expected Output Verification
The Java implementation should produce identical results to the COBOL program:
1. Same test data files created
2. Same merge results (sorted by ascending customer ID)
3. Same final sort results (sorted by descending contract ID)
4. Same display format for verification

## Testing Strategy
- Unit tests for each service class
- Integration test for the complete workflow
- File content verification tests
- Performance comparison with COBOL (optional)
