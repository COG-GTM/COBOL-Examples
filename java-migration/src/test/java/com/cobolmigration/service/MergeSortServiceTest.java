package com.cobolmigration.service;

import com.cobolmigration.model.CustomerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for MergeSortService - validates migration of merge_sort/merge_sort_test.cbl.
 */
class MergeSortServiceTest {

    private MergeSortService service;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        service = new MergeSortService();
    }

    @Test
    void mergeAndSort_sortsById() throws IOException {
        Path file1 = tempDir.resolve("test-file-1.txt");
        Path file2 = tempDir.resolve("test-file-2.txt");

        service.createTestData(file1, file2);

        List<CustomerRecord> merged = service.mergeAndSort(file1, file2);

        assertEquals(11, merged.size());
        assertEquals(1, merged.get(0).getCustomerId());
        assertEquals(3, merged.get(1).getCustomerId());
        assertEquals(5, merged.get(2).getCustomerId());
        assertEquals(10, merged.get(3).getCustomerId());
        assertEquals(999, merged.get(10).getCustomerId());
    }

    @Test
    void mergeAndSort_preservesAllRecords() throws IOException {
        Path file1 = tempDir.resolve("test-file-1.txt");
        Path file2 = tempDir.resolve("test-file-2.txt");

        service.createTestData(file1, file2);

        List<CustomerRecord> merged = service.mergeAndSort(file1, file2);
        assertEquals(11, merged.size()); // 6 east + 5 west
    }

    @Test
    void sortByContractIdDescending_sortsCorrectly() throws IOException {
        Path file1 = tempDir.resolve("test-file-1.txt");
        Path file2 = tempDir.resolve("test-file-2.txt");

        service.createTestData(file1, file2);

        List<CustomerRecord> merged = service.mergeAndSort(file1, file2);
        List<CustomerRecord> sorted = service.sortByContractIdDescending(merged);

        assertEquals(11, sorted.size());
        // First should have highest contract ID
        assertTrue(sorted.get(0).getContractId() >= sorted.get(1).getContractId());
        // Last should have lowest
        assertTrue(sorted.get(9).getContractId() >= sorted.get(10).getContractId());
        // Verify first is 12323 (highest contract ID)
        assertEquals(12323, sorted.get(0).getContractId());
    }

    @Test
    void writeAndReadFile_roundTrips() throws IOException {
        Path outputFile = tempDir.resolve("output.txt");

        List<CustomerRecord> records = List.of(
                new CustomerRecord(1, "Smith", "John", 100, "test"),
                new CustomerRecord(2, "Doe", "Jane", 200, "test2")
        );

        service.writeToFile(records, outputFile);
        List<CustomerRecord> readBack = service.readFile(outputFile);

        assertEquals(2, readBack.size());
        assertEquals(1, readBack.get(0).getCustomerId());
        assertEquals(2, readBack.get(1).getCustomerId());
    }

    @Test
    void createTestData_createsCorrectFiles() throws IOException {
        Path file1 = tempDir.resolve("east.txt");
        Path file2 = tempDir.resolve("west.txt");

        service.createTestData(file1, file2);

        List<CustomerRecord> eastRecords = service.readFile(file1);
        List<CustomerRecord> westRecords = service.readFile(file2);

        assertEquals(6, eastRecords.size());
        assertEquals(5, westRecords.size());
    }
}
