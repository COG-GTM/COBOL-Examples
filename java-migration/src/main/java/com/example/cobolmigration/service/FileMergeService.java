package com.example.cobolmigration.service;

import com.example.cobolmigration.model.CustomerRecord;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Spring Service mapping merge_sort/merge_sort_test.cbl.
 * Handles file-based merge and sort operations on customer records.
 */
@Service
public class FileMergeService {

    /**
     * Writes the same hardcoded test customer records as the COBOL program's
     * create-test-data paragraph.
     */
    public void createTestFiles(Path file1, Path file2) throws IOException {
        // File 1 — "East" records (from fd-test-file-1)
        List<CustomerRecord> eastRecords = List.of(
                new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"),
                new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"),
                new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"),
                new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"),
                new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"),
                new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75")
        );
        writeRecords(eastRecords, file1);

        // File 2 — "West" records (from fd-test-file-2)
        List<CustomerRecord> westRecords = List.of(
                new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"),
                new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"),
                new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"),
                new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"),
                new CustomerRecord(24, "last-24", "first-24", 247, "comment-24")
        );
        writeRecords(westRecords, file2);
    }

    /**
     * Reads both files, merges all records, sorts ascending by customerId.
     * Maps {@code MERGE ON ASCENDING KEY f-customer-id}.
     */
    public List<CustomerRecord> mergeAndSort(Path file1, Path file2) throws IOException {
        List<CustomerRecord> records = new ArrayList<>();
        records.addAll(readRecords(file1));
        records.addAll(readRecords(file2));
        records.sort(CustomerRecord.byCustomerIdAsc());
        return records;
    }

    /**
     * Reads file, sorts descending by contractId.
     * Maps {@code SORT ON DESCENDING KEY f-customer-contract-id}.
     */
    public List<CustomerRecord> sortDescending(Path inputFile) throws IOException {
        List<CustomerRecord> records = new ArrayList<>(readRecords(inputFile));
        records.sort(CustomerRecord.byContractIdDesc());
        return records;
    }

    /**
     * Writes records to file in fixed-width format.
     */
    public void writeRecords(List<CustomerRecord> records, Path outputFile) throws IOException {
        List<String> lines = records.stream()
                .map(CustomerRecord::toFixedWidth)
                .toList();
        Files.write(outputFile, lines);
    }

    /**
     * Reads records from a fixed-width file.
     */
    public List<CustomerRecord> readRecords(Path file) throws IOException {
        return Files.readAllLines(file).stream()
                .filter(line -> !line.isBlank())
                .map(CustomerRecord::fromFixedWidth)
                .toList();
    }
}
