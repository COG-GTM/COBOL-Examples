package com.migration.batch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Phase 4: File Processing / Batch Tests
 *
 * Test cases mirror the COBOL merge_sort/merge_sort_test.cbl:
 * - Merge two sorted files into one (ascending customer ID)
 * - Sort merged file by contract ID descending
 */
class FileMergeTest {

    private FileMergeService fileMergeService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fileMergeService = new FileMergeService();
    }

    @Test
    @DisplayName("Merge two files sorted by customer ID ascending")
    void mergeFiles() throws IOException {
        // Create test data matching COBOL create-test-data paragraph
        // File 1 (East): IDs 1, 5, 10, 25, 50, 75
        Path file1 = createTestFile1();
        // File 2 (West): IDs 3, 24, 30, 85, 999
        Path file2 = createTestFile2();

        List<CustomerRecord> merged = fileMergeService.mergeFiles(file1, file2);

        // Verify merge is sorted by customer ID ascending
        assertEquals(11, merged.size());
        assertEquals(1, merged.get(0).customerId());
        assertEquals(3, merged.get(1).customerId());
        assertEquals(5, merged.get(2).customerId());
        assertEquals(10, merged.get(3).customerId());
        assertEquals(24, merged.get(4).customerId());
        assertEquals(25, merged.get(5).customerId());
        assertEquals(30, merged.get(6).customerId());
        assertEquals(50, merged.get(7).customerId());
        assertEquals(75, merged.get(8).customerId());
        assertEquals(85, merged.get(9).customerId());
        assertEquals(999, merged.get(10).customerId());

        // Verify ascending order
        for (int i = 1; i < merged.size(); i++) {
            assertTrue(merged.get(i).customerId() >= merged.get(i - 1).customerId(),
                    "Records should be sorted ascending by customer ID");
        }
    }

    @Test
    @DisplayName("Sort merged file by contract ID descending")
    void sortByContractIdDescending() throws IOException {
        // First merge
        Path file1 = createTestFile1();
        Path file2 = createTestFile2();
        Path mergedFile = tempDir.resolve("merge-output.txt");
        fileMergeService.mergeFilesToOutput(file1, file2, mergedFile);

        // Then sort by contract ID descending
        List<CustomerRecord> sorted = fileMergeService.sortByContractIdDescending(mergedFile);

        // Verify descending order by contract ID
        assertEquals(11, sorted.size());
        for (int i = 1; i < sorted.size(); i++) {
            assertTrue(sorted.get(i).contractId() <= sorted.get(i - 1).contractId(),
                    "Records should be sorted descending by contract ID");
        }

        // First record should have highest contract ID (12323)
        assertEquals(12323, sorted.get(0).contractId());
    }

    @Test
    @DisplayName("Write and read records round-trip")
    void writeAndReadRoundTrip() throws IOException {
        Path outputFile = tempDir.resolve("output.txt");

        List<CustomerRecord> original = List.of(
                new CustomerRecord(1, "Smith", "John", 100, "test"),
                new CustomerRecord(2, "Doe", "Jane", 200, "test2")
        );

        fileMergeService.writeRecords(outputFile, original);
        List<CustomerRecord> readBack = fileMergeService.readRecords(outputFile);

        assertEquals(2, readBack.size());
        assertEquals(1, readBack.get(0).customerId());
        assertEquals(2, readBack.get(1).customerId());
        assertEquals("Smith", readBack.get(0).lastName().strip());
        assertEquals("Doe", readBack.get(1).lastName().strip());
    }

    @Test
    @DisplayName("CustomerRecord fixed-width format matches COBOL layout")
    void fixedWidthFormat() {
        CustomerRecord record = new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1");
        String fixedWidth = record.toFixedWidth();

        // Total width should be 135 characters
        assertEquals(135, fixedWidth.length());
        // ID at positions 0-4
        assertTrue(fixedWidth.startsWith("1    "));
    }

    @Test
    @DisplayName("Merge and sort to output files (full COBOL flow)")
    void fullMergeSortFlow() throws IOException {
        Path file1 = createTestFile1();
        Path file2 = createTestFile2();
        Path mergedFile = tempDir.resolve("merge-output.txt");
        Path sortedFile = tempDir.resolve("sorted-contract-id.txt");

        // Step 1: Merge (COBOL: merge-and-display-files)
        fileMergeService.mergeFilesToOutput(file1, file2, mergedFile);
        assertTrue(Files.exists(mergedFile));

        // Step 2: Sort (COBOL: sort-and-display-file)
        fileMergeService.sortToOutput(mergedFile, sortedFile);
        assertTrue(Files.exists(sortedFile));

        List<CustomerRecord> finalRecords = fileMergeService.readRecords(sortedFile);
        assertEquals(11, finalRecords.size());
    }

    private Path createTestFile1() throws IOException {
        Path file = tempDir.resolve("test-file-1.txt");
        List<CustomerRecord> records = List.of(
                new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"),
                new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"),
                new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"),
                new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"),
                new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"),
                new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75")
        );
        fileMergeService.writeRecords(file, records);
        return file;
    }

    private Path createTestFile2() throws IOException {
        Path file = tempDir.resolve("test-file-2.txt");
        List<CustomerRecord> records = List.of(
                new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"),
                new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"),
                new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"),
                new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"),
                new CustomerRecord(24, "last-24", "first-24", 247, "comment-24")
        );
        fileMergeService.writeRecords(file, records);
        return file;
    }
}
