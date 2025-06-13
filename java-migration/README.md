# Java Migration of COBOL Merge Sort

This Java project replicates the functionality of the COBOL merge sort program found in `../merge_sort/merge_sort_test.cbl`.

## Project Structure
```
java-migration/
├── src/main/java/com/example/mergesort/
│   ├── Customer.java          # Customer record class
│   └── MergeSortExample.java  # Main program
└── README.md
```

## Compilation and Execution

### Compile
```bash
cd java-migration
javac -d . src/main/java/com/example/mergesort/*.java
```

### Run
```bash
java com.example.mergesort.MergeSortExample
```

## Functionality

The program performs the same operations as the original COBOL program:

1. **Create Test Data**: Creates `test-file-1.txt` and `test-file-2.txt` with predefined customer records
2. **Merge and Sort**: Merges both files and sorts by customer ID (ascending) → `merge-output.txt`
3. **Sort by Contract**: Sorts merged file by contract ID (descending) → `sorted-contract-id.txt`

## Output Files

- `test-file-1.txt` - First input file with 6 customer records
- `test-file-2.txt` - Second input file with 5 customer records  
- `merge-output.txt` - Merged file sorted by customer ID ascending
- `sorted-contract-id.txt` - Final file sorted by contract ID descending

## Customer Record Format

Each customer record contains:
- Customer ID (5 digits)
- Last Name (50 characters, padded)
- First Name (50 characters, padded)
- Contract ID (5 digits)
- Comment (25 characters, padded)

Total record length: 135 characters per line
