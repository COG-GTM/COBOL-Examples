package com.example.migration.service;

import com.example.migration.model.CustomerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for FileSortService.
 * Verifies merge/sort output matches COBOL MERGE and SORT behavior
 * from merge_sort/merge_sort_test.cbl.
 *
 * COBOL test data:
 *   File 1 (East): IDs 1, 5, 10, 50, 25, 75
 *   File 2 (West): IDs 999, 3, 30, 85, 24
 *   Merged (ascending customer ID): 1, 3, 5, 10, 24, 25, 30, 50, 75, 85, 999
 *   Sorted (descending contract ID): ordered by contract-id desc
 */
class FileSortServiceTest {

    private FileSortService fileSortService;

    @BeforeEach
    void setUp() {
        fileSortService = new FileSortService();
    }

    @Test
    void mergeByCustomerId_mergesAndSortsAscending() {
        List<CustomerRecord> file1 = List.of(
                new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"),
                new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"),
                new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"),
                new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"),
                new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"),
                new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75")
        );

        List<CustomerRecord> file2 = List.of(
                new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"),
                new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"),
                new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"),
                new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"),
                new CustomerRecord(24, "last-24", "first-24", 247, "comment-24")
        );

        List<CustomerRecord> merged = fileSortService.mergeByCustomerId(file1, file2);

        assertEquals(11, merged.size());
        assertEquals(1, merged.get(0).getCustomerId());
        assertEquals(3, merged.get(1).getCustomerId());
        assertEquals(5, merged.get(2).getCustomerId());
        assertEquals(999, merged.get(10).getCustomerId());

        for (int i = 0; i < merged.size() - 1; i++) {
            assertTrue(merged.get(i).getCustomerId() <= merged.get(i + 1).getCustomerId());
        }
    }

    @Test
    void sortByContractIdDescending_sortsCorrectly() {
        List<CustomerRecord> records = List.of(
                new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"),
                new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"),
                new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"),
                new CustomerRecord(10, "last-10", "first-10", 653, "comment-10")
        );

        List<CustomerRecord> sorted = fileSortService.sortByContractIdDescending(records);

        assertEquals(4, sorted.size());
        assertEquals(12323, sorted.get(0).getContractId());
        assertEquals(5423, sorted.get(1).getContractId());
        assertEquals(3331, sorted.get(2).getContractId());
        assertEquals(653, sorted.get(3).getContractId());

        for (int i = 0; i < sorted.size() - 1; i++) {
            assertTrue(sorted.get(i).getContractId() >= sorted.get(i + 1).getContractId());
        }
    }

    @Test
    void writeAndReadRecords_roundTrips(@TempDir Path tempDir) throws IOException {
        Path testFile = tempDir.resolve("test-output.txt");

        List<CustomerRecord> original = List.of(
                new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"),
                new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5")
        );

        fileSortService.writeRecordsToFile(testFile, original);
        List<CustomerRecord> readBack = fileSortService.readRecordsFromFile(testFile);

        assertEquals(original.size(), readBack.size());
        assertEquals(original.get(0).getCustomerId(), readBack.get(0).getCustomerId());
        assertEquals(original.get(0).getLastName(), readBack.get(0).getLastName());
        assertEquals(original.get(1).getCustomerId(), readBack.get(1).getCustomerId());
    }
}
