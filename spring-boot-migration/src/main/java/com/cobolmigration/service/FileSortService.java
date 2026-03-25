package com.cobolmigration.service;

import com.cobolmigration.model.CustomerRecord;
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
 * Service replacing COBOL SORT and MERGE operations in merge_sort/merge_sort_test.cbl.
 * Uses CSV files instead of COBOL fixed-length record files.
 */
@Service
public class FileSortService {

    private static final String CSV_DELIMITER = ",";

    /**
     * Merges two sorted lists of CustomerRecord by customerId ascending.
     * Replaces MERGE fd-sorting-file in merge_sort_test.cbl (lines 107-110).
     *
     * @param file1 first sorted list
     * @param file2 second sorted list
     * @return merged and sorted list
     */
    public List<CustomerRecord> mergeFiles(List<CustomerRecord> file1, List<CustomerRecord> file2) {
        List<CustomerRecord> merged = new ArrayList<>(file1.size() + file2.size());
        int i = 0, j = 0;

        while (i < file1.size() && j < file2.size()) {
            if (file1.get(i).getCustomerId() <= file2.get(j).getCustomerId()) {
                merged.add(file1.get(i++));
            } else {
                merged.add(file2.get(j++));
            }
        }

        while (i < file1.size()) {
            merged.add(file1.get(i++));
        }
        while (j < file2.size()) {
            merged.add(file2.get(j++));
        }

        return merged;
    }

    /**
     * Sorts a list of CustomerRecord by contractId in descending order.
     * Replaces SORT fd-sorting-file ON DESCENDING KEY f-customer-contract-id
     * in merge_sort_test.cbl (lines 142-145).
     *
     * @param records list to sort
     * @return sorted list (new list, original is not modified)
     */
    public List<CustomerRecord> sortByContractIdDesc(List<CustomerRecord> records) {
        List<CustomerRecord> sorted = new ArrayList<>(records);
        sorted.sort(Comparator.comparingInt(CustomerRecord::getContractId).reversed());
        return sorted;
    }

    /**
     * Reads CustomerRecord entries from a CSV file.
     * Replaces COBOL file READ operations on fd-test-file-1 and fd-test-file-2.
     * CSV format: customerId,lastName,firstName,contractId,comment
     *
     * @param filePath path to the CSV file
     * @return list of CustomerRecord
     * @throws IOException if file cannot be read
     */
    public List<CustomerRecord> readFromCsv(Path filePath) throws IOException {
        List<CustomerRecord> records = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(CSV_DELIMITER, -1);
                if (parts.length >= 5) {
                    records.add(CustomerRecord.builder()
                            .customerId(Integer.parseInt(parts[0].trim()))
                            .lastName(parts[1].trim())
                            .firstName(parts[2].trim())
                            .contractId(Integer.parseInt(parts[3].trim()))
                            .comment(parts[4].trim())
                            .build());
                }
            }
        }
        return records;
    }

    /**
     * Writes CustomerRecord entries to a CSV file.
     * Replaces COBOL file WRITE operations.
     * CSV format: customerId,lastName,firstName,contractId,comment
     *
     * @param filePath path to the output CSV file
     * @param records  list of CustomerRecord to write
     * @throws IOException if file cannot be written
     */
    public void writeToCsv(Path filePath, List<CustomerRecord> records) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (CustomerRecord record : records) {
                writer.write(String.format("%d,%s,%s,%d,%s",
                        record.getCustomerId(),
                        record.getLastName(),
                        record.getFirstName(),
                        record.getContractId(),
                        record.getComment()));
                writer.newLine();
            }
        }
    }
}
