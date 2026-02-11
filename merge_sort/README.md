# SORT and MERGE Example

This example demonstrates the COBOL `SORT` and `MERGE` verbs for sorting and merging sequential files.

`merge_sort_test.cbl` performs the following:

1. Creates two test data files (`test-file-1.txt` and `test-file-2.txt`) containing customer records with IDs, names, and contract numbers.
2. **Merges** both files into `merge-output.txt`, sorted in ascending order by customer ID using the `MERGE` verb.
3. **Sorts** the merged file into `sorted-contract-id.txt` in descending order by contract ID using the `SORT` verb.
4. Displays the contents of each output file.

## How to Compile

```bash
cobc -x merge_sort_test.cbl
```

## How to Run

```bash
./merge_sort_test
```

## Expected Output

```
Creating test data files...
Merging and sorting files...
00001last-1    [...]   comment-1
00003last-03   [...]   comment-03
00005last-5    [...]   comment-5
...
Sorting merged file on descending contract id....
00005last-5    [...]   comment-5
...
Done.
```

The program creates temporary working files in the current directory during execution.

## Prerequisites

None beyond GnuCOBOL (`cobc`). This example does not require user input.
