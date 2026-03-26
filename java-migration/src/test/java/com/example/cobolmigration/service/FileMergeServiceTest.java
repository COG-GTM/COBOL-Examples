package com.example.cobolmigration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.cobolmigration.model.CustomerRecord;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests using temp files to verify merge/sort output order.
 * Maps merge_sort/merge_sort_test.cbl behavior.
 */
class FileMergeServiceTest {

    private FileMergeService service;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        service = new FileMergeService();
    }

    @Test
    void createTestFiles_createsNonEmptyFiles() throws IOException {
        Path file1 = tempDir.resolve("test-file-1.txt");
        Path file2 = tempDir.resolve("test-file-2.txt");

        service.createTestFiles(file1, file2);

        assertTrue(Files.exists(file1));
        assertTrue(Files.exists(file2));
        assertFalse(Files.readAllLines(file1).isEmpty());
        assertFalse(Files.readAllLines(file2).isEmpty());
    }

    @Test
    void createTestFiles_correctRecordCounts() throws IOException {
        Path file1 = tempDir.resolve("test-file-1.txt");
        Path file2 = tempDir.resolve("test-file-2.txt");

        service.createTestFiles(file1, file2);

        // File 1 has 6 east records, file 2 has 5 west records
        assertEquals(6, Files.readAllLines(file1).size());
        assertEquals(5, Files.readAllLines(file2).size());
    }

    @Test
    void mergeAndSort_ascendingByCustomerId() throws IOException {
        Path file1 = tempDir.resolve("test-file-1.txt");
        Path file2 = tempDir.resolve("test-file-2.txt");

        service.createTestFiles(file1, file2);
        List<CustomerRecord> merged = service.mergeAndSort(file1, file2);

        // Total records: 6 + 5 = 11
        assertEquals(11, merged.size());

        // Verify ascending order by customerId
        for (int i = 1; i < merged.size(); i++) {
            assertTrue(merged.get(i).getCustomerId() >= merged.get(i - 1).getCustomerId(),
                    "Records should be sorted ascending by customerId");
        }

        // First should be customer 1, last should be customer 999
        assertEquals(1, merged.get(0).getCustomerId());
        assertEquals(999, merged.get(merged.size() - 1).getCustomerId());
    }

    @Test
    void sortDescending_descendingByContractId() throws IOException {
        Path file1 = tempDir.resolve("test-file-1.txt");
        Path file2 = tempDir.resolve("test-file-2.txt");
        Path mergedFile = tempDir.resolve("merged.txt");

        service.createTestFiles(file1, file2);
        List<CustomerRecord> merged = service.mergeAndSort(file1, file2);
        service.writeRecords(merged, mergedFile);

        List<CustomerRecord> sorted = service.sortDescending(mergedFile);

        // Verify descending order by contractId
        assertEquals(11, sorted.size());
        for (int i = 1; i < sorted.size(); i++) {
            assertTrue(sorted.get(i).getContractId() <= sorted.get(i - 1).getContractId(),
                    "Records should be sorted descending by contractId");
        }

        // First should be highest contractId (12323), last should be lowest (247)
        assertEquals(12323, sorted.get(0).getContractId());
        assertEquals(247, sorted.get(sorted.size() - 1).getContractId());
    }

    @Test
    void writeRecords_andReadRecords_roundTrip() throws IOException {
        Path file = tempDir.resolve("roundtrip.txt");
        List<CustomerRecord> original = List.of(
                new CustomerRecord(1, "Smith", "John", 100, "test"),
                new CustomerRecord(2, "Doe", "Jane", 200, "test2")
        );

        service.writeRecords(original, file);
        List<CustomerRecord> read = service.readRecords(file);

        assertEquals(original.size(), read.size());
        assertEquals(original.get(0).getCustomerId(), read.get(0).getCustomerId());
        assertEquals(original.get(0).getLastName(), read.get(0).getLastName());
        assertEquals(original.get(1).getCustomerId(), read.get(1).getCustomerId());
        assertEquals(original.get(1).getFirstName(), read.get(1).getFirstName());
    }

    @Test
    void mergeAndSort_specificRecordOrder() throws IOException {
        // Verify the exact expected order from the COBOL merge output
        Path file1 = tempDir.resolve("test-file-1.txt");
        Path file2 = tempDir.resolve("test-file-2.txt");

        service.createTestFiles(file1, file2);
        List<CustomerRecord> merged = service.mergeAndSort(file1, file2);

        int[] expectedOrder = {1, 3, 5, 10, 24, 25, 30, 50, 75, 85, 999};
        assertEquals(expectedOrder.length, merged.size());
        for (int i = 0; i < expectedOrder.length; i++) {
            assertEquals(expectedOrder[i], merged.get(i).getCustomerId(),
                    "Customer at index " + i + " should have id " + expectedOrder[i]);
        }
    }
}
