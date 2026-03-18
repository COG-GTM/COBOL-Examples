package com.migration.batch;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Phase 4: File Processing - Merge/Sort Service
 *
 * Migrates COBOL MERGE and SORT operations from merge_sort/merge_sort_test.cbl.
 *
 * COBOL:
 *   MERGE fd-sorting-file
 *       ON ASCENDING KEY f-customer-id
 *       USING fd-test-file-1 fd-test-file-2 GIVING fd-merged-file
 *
 * Java equivalent: Read both files, merge into a single sorted list,
 * write to output file.
 *
 * COBOL:
 *   SORT fd-sorting-file
 *       ON DESCENDING KEY f-customer-contract-id
 *       USING fd-merged-file GIVING fd-sorted-contract-id
 *
 * Java equivalent: Read file, sort with Comparator, write to output.
 */
@Service
public class FileMergeService {

    /**
     * Merges two input files into a single output file, sorted by customer ID ascending.
     * Equivalent to COBOL: MERGE ... ON ASCENDING KEY f-customer-id USING file1 file2 GIVING output
     */
    public List<CustomerRecord> mergeFiles(Path file1, Path file2) throws IOException {
        List<CustomerRecord> records1 = readRecords(file1);
        List<CustomerRecord> records2 = readRecords(file2);

        List<CustomerRecord> merged = new ArrayList<>(records1.size() + records2.size());
        merged.addAll(records1);
        merged.addAll(records2);

        merged.sort(Comparator.comparingInt(CustomerRecord::customerId));
        return merged;
    }

    /**
     * Merges two files and writes the result to an output file.
     */
    public void mergeFilesToOutput(Path file1, Path file2, Path outputFile) throws IOException {
        List<CustomerRecord> merged = mergeFiles(file1, file2);
        writeRecords(outputFile, merged);
    }

    /**
     * Sorts records from an input file by contract ID descending and writes to output.
     * Equivalent to COBOL: SORT ... ON DESCENDING KEY f-customer-contract-id
     */
    public List<CustomerRecord> sortByContractIdDescending(Path inputFile) throws IOException {
        List<CustomerRecord> records = readRecords(inputFile);
        records.sort(Comparator.comparingInt(CustomerRecord::contractId).reversed());
        return records;
    }

    /**
     * Sorts and writes to output file.
     */
    public void sortToOutput(Path inputFile, Path outputFile) throws IOException {
        List<CustomerRecord> sorted = sortByContractIdDescending(inputFile);
        writeRecords(outputFile, sorted);
    }

    /**
     * Reads customer records from a fixed-width text file.
     * Each line corresponds to one COBOL record (135 characters wide).
     */
    public List<CustomerRecord> readRecords(Path file) throws IOException {
        List<CustomerRecord> records = new ArrayList<>();
        try (Stream<String> lines = Files.lines(file)) {
            lines.filter(line -> !line.isBlank())
                    .forEach(line -> records.add(CustomerRecord.fromFixedWidth(line)));
        }
        return records;
    }

    /**
     * Writes customer records to a fixed-width text file.
     */
    public void writeRecords(Path file, List<CustomerRecord> records) throws IOException {
        List<String> lines = records.stream()
                .map(CustomerRecord::toFixedWidth)
                .toList();
        Files.write(file, lines);
    }
}
