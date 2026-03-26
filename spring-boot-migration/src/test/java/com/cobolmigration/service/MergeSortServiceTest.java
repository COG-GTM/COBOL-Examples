package com.cobolmigration.service;

import com.cobolmigration.model.CustomerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for MergeSortService with sample fixed-width files.
 * Verifies behavior matching merge_sort/merge_sort_test.cbl.
 */
class MergeSortServiceTest {

    private MergeSortService mergeSortService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        mergeSortService = new MergeSortService();
    }

    private Path createTestFile1() throws IOException {
        Path file = tempDir.resolve("test-file-1.txt");
        List<CustomerRecord> records = Arrays.asList(
                new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"),
                new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"),
                new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"),
                new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"),
                new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"),
                new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75")
        );
        List<String> lines = records.stream().map(CustomerRecord::toFixedWidth).toList();
        Files.write(file, lines);
        return file;
    }

    private Path createTestFile2() throws IOException {
        Path file = tempDir.resolve("test-file-2.txt");
        List<CustomerRecord> records = Arrays.asList(
                new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"),
                new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"),
                new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"),
                new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"),
                new CustomerRecord(24, "last-24", "first-24", 247, "comment-24")
        );
        List<String> lines = records.stream().map(CustomerRecord::toFixedWidth).toList();
        Files.write(file, lines);
        return file;
    }

    @Test
    void mergeFiles_shouldMergeAndSortByCustomerId() throws IOException {
        Path file1 = createTestFile1();
        Path file2 = createTestFile2();
        Comparator<CustomerRecord> byId = Comparator.comparingInt(CustomerRecord::getCustomerId);

        List<CustomerRecord> merged = mergeSortService.mergeFiles(file1, file2, byId);

        assertEquals(11, merged.size());
        // Verify ascending order by customer ID
        for (int i = 1; i < merged.size(); i++) {
            assertTrue(merged.get(i).getCustomerId() >= merged.get(i - 1).getCustomerId());
        }
        assertEquals(1, merged.get(0).getCustomerId());
        assertEquals(999, merged.get(merged.size() - 1).getCustomerId());
    }

    @Test
    void sortFile_shouldSortByContractIdDescending() throws IOException {
        Path file1 = createTestFile1();
        Comparator<CustomerRecord> byContractDesc =
                Comparator.comparingInt(CustomerRecord::getContractId).reversed();

        List<CustomerRecord> sorted = mergeSortService.sortFile(file1, byContractDesc);

        assertEquals(6, sorted.size());
        for (int i = 1; i < sorted.size(); i++) {
            assertTrue(sorted.get(i).getContractId() <= sorted.get(i - 1).getContractId());
        }
    }

    @Test
    void readAndWriteRecords_shouldPreserveData() throws IOException {
        Path file = createTestFile1();
        List<CustomerRecord> records = mergeSortService.readRecords(file);

        Path output = tempDir.resolve("output.txt");
        mergeSortService.writeRecords(output, records);
        List<CustomerRecord> readBack = mergeSortService.readRecords(output);

        assertEquals(records.size(), readBack.size());
        for (int i = 0; i < records.size(); i++) {
            assertEquals(records.get(i).getCustomerId(), readBack.get(i).getCustomerId());
            assertEquals(records.get(i).getLastName(), readBack.get(i).getLastName());
            assertEquals(records.get(i).getContractId(), readBack.get(i).getContractId());
        }
    }

    @Test
    void mergeAndSort_inMemory_shouldWork() {
        List<CustomerRecord> list1 = Arrays.asList(
                new CustomerRecord(1, "A", "B", 100, "c1"),
                new CustomerRecord(5, "C", "D", 200, "c2")
        );
        List<CustomerRecord> list2 = Arrays.asList(
                new CustomerRecord(3, "E", "F", 150, "c3")
        );

        List<CustomerRecord> result = mergeSortService.mergeAndSort(list1, list2,
                Comparator.comparingInt(CustomerRecord::getCustomerId));

        assertEquals(3, result.size());
        assertEquals(1, result.get(0).getCustomerId());
        assertEquals(3, result.get(1).getCustomerId());
        assertEquals(5, result.get(2).getCustomerId());
    }
}
