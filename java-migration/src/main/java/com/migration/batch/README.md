# Phase 4: File Processing (Merge/Sort)

## COBOL Sources
- `merge_sort/merge_sort_test.cbl` - MERGE and SORT with sequential files

## COBOL-to-Java Mapping

### File Declarations
| COBOL | Java |
|-------|------|
| `SELECT fd-test-file-1 ASSIGN TO "test-file-1.txt"` | `Path.of("test-file-1.txt")` |
| `FD fd-test-file-1 RECORDING MODE F` | `FlatFileItemReader` / `Files.readAllLines()` |
| `SD fd-sorting-file` | Sort work file (not needed in Java) |

### Merge Operation
| COBOL | Java |
|-------|------|
| `MERGE fd-sorting-file ON ASCENDING KEY f-customer-id USING file1 file2 GIVING output` | `fileMergeService.mergeFiles(file1, file2)` with `Comparator.comparingInt()` |

### Sort Operation
| COBOL | Java |
|-------|------|
| `SORT fd-sorting-file ON DESCENDING KEY f-contract-id USING input GIVING output` | `list.sort(Comparator.reversed())` |

### Record Structure
```
COBOL (135 chars fixed-width):     Java CustomerRecord:
f-customer-id        PIC 9(5)  ->  int customerId
f-customer-last-name PIC X(50) ->  String lastName
f-customer-first-name PIC X(50)->  String firstName
f-customer-contract-id PIC 9(5)->  int contractId
f-customer-comment   PIC X(25) ->  String comment
```

## Implementation Approaches

Two approaches are provided:

1. **FileMergeService** (simple): Reads files into lists, merges with `Stream.sorted()`, writes output. Suitable for small-to-medium files.

2. **MergeSortJobConfig** (Spring Batch): Configures a batch job with merge and sort steps. Suitable for large-scale processing with monitoring and restart capabilities.

## Behavioral Differences

1. **Sort work files**: COBOL requires an explicit SD (sort description) work file. Java sorts in memory.

2. **Fixed-width records**: COBOL files use fixed-width records (135 chars). Java uses `toFixedWidth()`/`fromFixedWidth()` for compatibility.

3. **File status codes**: COBOL checks `ws-fs-status` after each file operation. Java uses exceptions (`IOException`).
