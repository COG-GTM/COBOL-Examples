package com.example.migration.service;

import com.example.migration.model.CustomerRecord;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Service replacing COBOL MERGE and SORT operations from merge_sort/merge_sort_test.cbl.
 *
 * COBOL operations replaced:
 *   MERGE fd-sorting-file ON ASCENDING KEY f-customer-id
 *       USING fd-test-file-1 fd-test-file-2 GIVING fd-merged-file
 *       (merge_sort_test.cbl lines 107-110)
 *
 *   SORT fd-sorting-file ON DESCENDING KEY f-customer-contract-id
 *       USING fd-merged-file GIVING fd-sorted-contract-id
 *       (merge_sort_test.cbl lines 142-145)
 *
 * In COBOL, MERGE reads two sorted files, merges them on a key, and writes output.
 * SORT reads a file, sorts on a key, and writes output.
 * This service replicates both operations using Java collections.
 */
@Service
public class FileSortService {

    /**
     * Merges two lists of CustomerRecords sorted by customer ID (ascending).
     * Replaces: MERGE fd-sorting-file ON ASCENDING KEY f-customer-id
     *           USING fd-test-file-1 fd-test-file-2 GIVING fd-merged-file
     */
    public List<CustomerRecord> mergeByCustomerId(List<CustomerRecord> file1Records,
                                                   List<CustomerRecord> file2Records) {
        List<CustomerRecord> merged = new ArrayList<>(file1Records.size() + file2Records.size());
        merged.addAll(file1Records);
        merged.addAll(file2Records);
        merged.sort(Comparator.comparingInt(CustomerRecord::getCustomerId));
        return merged;
    }

    /**
     * Sorts records by contract ID (descending).
     * Replaces: SORT fd-sorting-file ON DESCENDING KEY f-customer-contract-id
     *           USING fd-merged-file GIVING fd-sorted-contract-id
     */
    public List<CustomerRecord> sortByContractIdDescending(List<CustomerRecord> records) {
        List<CustomerRecord> sorted = new ArrayList<>(records);
        sorted.sort(Comparator.comparingInt(CustomerRecord::getContractId).reversed());
        return sorted;
    }

    /**
     * Reads customer records from a fixed-width text file.
     * Replaces COBOL file READ operations with fixed-width record format:
     *   f-customer-id(5) + f-customer-last-name(50) + f-customer-first-name(50)
     *   + f-customer-contract-id(5) + f-customer-comment(25)
     */
    public List<CustomerRecord> readRecordsFromFile(Path filePath) throws IOException {
        List<CustomerRecord> records = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() >= 10) {
                    CustomerRecord record = parseFixedWidthRecord(line);
                    records.add(record);
                }
            }
        }
        return records;
    }

    /**
     * Writes customer records to a fixed-width text file.
     * Replaces COBOL WRITE operations for file output.
     */
    public void writeRecordsToFile(Path filePath, List<CustomerRecord> records) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (CustomerRecord record : records) {
                writer.write(record.toFixedWidthString());
                writer.newLine();
            }
        }
    }

    private CustomerRecord parseFixedWidthRecord(String line) {
        String padded = String.format("%-135s", line);
        int customerId = parseIntSafe(padded.substring(0, 5).trim());
        String lastName = padded.substring(5, 55).trim();
        String firstName = padded.substring(55, 105).trim();
        int contractId = parseIntSafe(padded.substring(105, 110).trim());
        String comment = padded.substring(110, 135).trim();
        return new CustomerRecord(customerId, lastName, firstName, contractId, comment);
    }

    private int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
