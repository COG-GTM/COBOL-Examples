package com.cobolmigration.service;

import com.cobolmigration.model.CustomerRecord;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Replaces merge_sort/merge_sort_test.cbl.
 * Uses Java collections and streams for merge and sort operations
 * instead of COBOL MERGE and SORT file operations.
 */
@Service
public class MergeSortService {

    /**
     * Reads two files, merges them, and sorts by customer ID ascending.
     * Replaces: MERGE fd-sorting-file ON ASCENDING KEY f-customer-id
     *           USING fd-test-file-1 fd-test-file-2 GIVING fd-merged-file
     * Source: merge_sort_test.cbl lines 103-134
     *
     * @param file1 path to first input file
     * @param file2 path to second input file
     * @return merged and sorted list of CustomerRecords
     */
    public List<CustomerRecord> mergeAndSort(Path file1, Path file2) throws IOException {
        List<CustomerRecord> records1 = readFile(file1);
        List<CustomerRecord> records2 = readFile(file2);

        List<CustomerRecord> merged = new ArrayList<>();
        merged.addAll(records1);
        merged.addAll(records2);

        merged.sort(Comparator.comparingInt(CustomerRecord::getCustomerId));
        return merged;
    }

    /**
     * Sorts records by contract ID descending.
     * Replaces: SORT fd-sorting-file ON DESCENDING KEY f-customer-contract-id
     * Source: merge_sort_test.cbl lines 138-169
     */
    public List<CustomerRecord> sortByContractIdDescending(List<CustomerRecord> records) {
        List<CustomerRecord> sorted = new ArrayList<>(records);
        sorted.sort(Comparator.comparingInt(CustomerRecord::getContractId).reversed());
        return sorted;
    }

    /**
     * Writes records to a file in fixed-width format.
     */
    public void writeToFile(List<CustomerRecord> records, Path outputFile) throws IOException {
        List<String> lines = records.stream()
                .map(CustomerRecord::toFixedWidth)
                .toList();
        Files.write(outputFile, lines);
    }

    /**
     * Reads a fixed-width file into CustomerRecord objects.
     */
    public List<CustomerRecord> readFile(Path file) throws IOException {
        List<CustomerRecord> records = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    records.add(CustomerRecord.fromFixedWidth(line));
                }
            }
        }
        return records;
    }

    /**
     * Creates test data matching the COBOL program's create-test-data paragraph.
     * Source: merge_sort_test.cbl lines 173-338
     */
    public void createTestData(Path file1, Path file2) throws IOException {
        // File 1 - East region records
        List<CustomerRecord> eastRecords = List.of(
                new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"),
                new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"),
                new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"),
                new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"),
                new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"),
                new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75")
        );
        writeToFile(eastRecords, file1);

        // File 2 - West region records
        List<CustomerRecord> westRecords = List.of(
                new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"),
                new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"),
                new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"),
                new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"),
                new CustomerRecord(24, "last-24", "first-24", 247, "comment-24")
        );
        writeToFile(westRecords, file2);
    }
}
