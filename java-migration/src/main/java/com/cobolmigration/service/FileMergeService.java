package com.cobolmigration.service;

import com.cobolmigration.model.CustomerRecord;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Service replacing the SORT/MERGE logic from merge_sort/merge_sort_test.cbl.
 *
 * <p>COBOL operation mapping:
 * <ul>
 *   <li>MERGE fd-sorting-file ON ASCENDING KEY f-customer-id
 *       USING fd-test-file-1 fd-test-file-2 GIVING fd-merged-file
 *       &rarr; {@link #mergeFiles(Path, Path, Path)}</li>
 *   <li>SORT fd-sorting-file ON DESCENDING KEY f-customer-contract-id
 *       USING fd-merged-file GIVING fd-sorted-contract-id
 *       &rarr; {@link #sortByContractIdDescending(Path, Path)}</li>
 * </ul>
 *
 * @see merge_sort/merge_sort_test.cbl
 */
@Service
public class FileMergeService {

    /**
     * Merges two input files and sorts by customer ID ascending.
     * Replaces the COBOL MERGE statement (merge_sort_test.cbl lines 107-110).
     *
     * @param inputFile1 path to the first input file (East region)
     * @param inputFile2 path to the second input file (West region)
     * @param outputFile path to the merged output file
     * @return the list of merged and sorted records
     * @throws IOException if file I/O fails
     */
    public List<CustomerRecord> mergeFiles(Path inputFile1, Path inputFile2, Path outputFile)
            throws IOException {
        List<CustomerRecord> records1 = readRecords(inputFile1);
        List<CustomerRecord> records2 = readRecords(inputFile2);

        List<CustomerRecord> merged = new ArrayList<>();
        merged.addAll(records1);
        merged.addAll(records2);

        // Sort ascending by customer ID (matches COBOL: ON ASCENDING KEY f-customer-id)
        merged.sort(Comparator.comparingInt(CustomerRecord::getCustomerId));

        writeRecords(outputFile, merged);
        return merged;
    }

    /**
     * Sorts a file by contract ID in descending order.
     * Replaces the COBOL SORT statement (merge_sort_test.cbl lines 142-145).
     *
     * @param inputFile  path to the input file (merged output)
     * @param outputFile path to the sorted output file
     * @return the list of sorted records
     * @throws IOException if file I/O fails
     */
    public List<CustomerRecord> sortByContractIdDescending(Path inputFile, Path outputFile)
            throws IOException {
        List<CustomerRecord> records = readRecords(inputFile);

        // Sort descending by contract ID (matches COBOL: ON DESCENDING KEY f-customer-contract-id)
        records.sort(Comparator.comparingInt(CustomerRecord::getContractId).reversed());

        writeRecords(outputFile, records);
        return records;
    }

    /**
     * Reads customer records from a fixed-width file.
     */
    public List<CustomerRecord> readRecords(Path filePath) throws IOException {
        return Files.readAllLines(filePath).stream()
                .filter(line -> !line.isBlank())
                .map(CustomerRecord::fromFixedWidth)
                .collect(Collectors.toList());
    }

    /**
     * Writes customer records to a fixed-width file.
     */
    public void writeRecords(Path filePath, List<CustomerRecord> records) throws IOException {
        List<String> lines = records.stream()
                .map(CustomerRecord::toString)
                .collect(Collectors.toList());
        Files.write(filePath, lines);
    }
}
