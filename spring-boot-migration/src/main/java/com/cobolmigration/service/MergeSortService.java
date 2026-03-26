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
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service for merge and sort operations on fixed-width customer record files.
 * Replaces the COBOL MERGE and SORT statements from merge_sort/merge_sort_test.cbl:
 *
 *   MERGE fd-sorting-file ON ASCENDING KEY f-customer-id
 *       USING fd-test-file-1 fd-test-file-2 GIVING fd-merged-file
 *
 *   SORT fd-sorting-file ON DESCENDING KEY f-customer-contract-id
 *       USING fd-merged-file GIVING fd-sorted-contract-id
 */
@Service
public class MergeSortService {

    /**
     * Read customer records from a fixed-width file.
     * Replaces COBOL file READ operations with AT END / NOT AT END handling.
     */
    public List<CustomerRecord> readRecords(Path file) throws IOException {
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
     * Write customer records to a fixed-width file.
     * Replaces COBOL WRITE statements for output files.
     */
    public void writeRecords(Path file, List<CustomerRecord> records) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            for (CustomerRecord record : records) {
                writer.write(record.toFixedWidth());
                writer.newLine();
            }
        }
    }

    /**
     * Merge two files and sort the combined records.
     * Replaces: MERGE fd-sorting-file ON ASCENDING KEY f-customer-id
     *           USING fd-test-file-1 fd-test-file-2 GIVING fd-merged-file
     */
    public List<CustomerRecord> mergeFiles(Path file1, Path file2,
                                           Comparator<CustomerRecord> comparator) throws IOException {
        List<CustomerRecord> records1 = readRecords(file1);
        List<CustomerRecord> records2 = readRecords(file2);
        return Stream.concat(records1.stream(), records2.stream())
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    /**
     * Sort records from a single file.
     * Replaces: SORT fd-sorting-file ON DESCENDING KEY f-customer-contract-id
     *           USING fd-merged-file GIVING fd-sorted-contract-id
     */
    public List<CustomerRecord> sortFile(Path file,
                                         Comparator<CustomerRecord> comparator) throws IOException {
        return readRecords(file).stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    /**
     * Merge and sort in-memory record lists.
     */
    public List<CustomerRecord> mergeAndSort(List<CustomerRecord> list1,
                                             List<CustomerRecord> list2,
                                             Comparator<CustomerRecord> comparator) {
        return Stream.concat(list1.stream(), list2.stream())
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}
