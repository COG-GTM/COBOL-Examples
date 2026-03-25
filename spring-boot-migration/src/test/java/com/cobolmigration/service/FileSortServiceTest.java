package com.cobolmigration.service;

import com.cobolmigration.model.CustomerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for FileSortService.
 * Verifies merge and sort operations produce the same ordering as COBOL SORT/MERGE
 * in merge_sort/merge_sort_test.cbl.
 */
class FileSortServiceTest {

    private FileSortService fileSortService;

    // Test data matching merge_sort_test.cbl create-test-data paragraph (lines 173-338)
    private List<CustomerRecord> file1Records; // "East" file
    private List<CustomerRecord> file2Records; // "West" file

    @BeforeEach
    void setUp() {
        fileSortService = new FileSortService();

        // fd-test-file-1 records (lines 185-259)
        file1Records = Arrays.asList(
                CustomerRecord.builder().customerId(1).lastName("last-1").firstName("first-1")
                        .contractId(5423).comment("comment-1").build(),
                CustomerRecord.builder().customerId(5).lastName("last-5").firstName("first-5")
                        .contractId(12323).comment("comment-5").build(),
                CustomerRecord.builder().customerId(10).lastName("last-10").firstName("first-10")
                        .contractId(653).comment("comment-10").build(),
                CustomerRecord.builder().customerId(50).lastName("last-50").firstName("first-50")
                        .contractId(5050).comment("comment-50").build(),
                CustomerRecord.builder().customerId(25).lastName("last-25").firstName("first-25")
                        .contractId(7725).comment("comment-25").build(),
                CustomerRecord.builder().customerId(75).lastName("last-75").firstName("first-75")
                        .contractId(1175).comment("comment-75").build()
        );

        // fd-test-file-2 records (lines 272-334)
        file2Records = Arrays.asList(
                CustomerRecord.builder().customerId(999).lastName("last-999").firstName("first-999")
                        .contractId(1610).comment("comment-99").build(),
                CustomerRecord.builder().customerId(3).lastName("last-03").firstName("first-03")
                        .contractId(3331).comment("comment-03").build(),
                CustomerRecord.builder().customerId(30).lastName("last-30").firstName("first-30")
                        .contractId(8765).comment("comment-30").build(),
                CustomerRecord.builder().customerId(85).lastName("last-85").firstName("first-85")
                        .contractId(4567).comment("comment-85").build(),
                CustomerRecord.builder().customerId(24).lastName("last-24").firstName("first-24")
                        .contractId(247).comment("comment-24").build()
        );
    }

    @Test
    void mergeFiles_mergesByCustomerIdAscending() {
        // Pre-sort each file by customerId (COBOL MERGE does this)
        List<CustomerRecord> sorted1 = file1Records.stream()
                .sorted((a, b) -> Integer.compare(a.getCustomerId(), b.getCustomerId()))
                .toList();
        List<CustomerRecord> sorted2 = file2Records.stream()
                .sorted((a, b) -> Integer.compare(a.getCustomerId(), b.getCustomerId()))
                .toList();

        List<CustomerRecord> merged = fileSortService.mergeFiles(sorted1, sorted2);

        assertEquals(11, merged.size());

        // Verify ascending order by customerId
        for (int i = 1; i < merged.size(); i++) {
            assertTrue(merged.get(i).getCustomerId() >= merged.get(i - 1).getCustomerId(),
                    "Records should be in ascending customerId order");
        }

        // Verify expected order: 1, 3, 5, 10, 24, 25, 30, 50, 75, 85, 999
        assertEquals(1, merged.get(0).getCustomerId());
        assertEquals(3, merged.get(1).getCustomerId());
        assertEquals(5, merged.get(2).getCustomerId());
        assertEquals(10, merged.get(3).getCustomerId());
        assertEquals(24, merged.get(4).getCustomerId());
        assertEquals(25, merged.get(5).getCustomerId());
        assertEquals(30, merged.get(6).getCustomerId());
        assertEquals(50, merged.get(7).getCustomerId());
        assertEquals(75, merged.get(8).getCustomerId());
        assertEquals(85, merged.get(9).getCustomerId());
        assertEquals(999, merged.get(10).getCustomerId());
    }

    @Test
    void sortByContractIdDesc_sortsDescending() {
        // Combine all records (simulates merged output)
        List<CustomerRecord> allRecords = new java.util.ArrayList<>(file1Records);
        allRecords.addAll(file2Records);

        List<CustomerRecord> sorted = fileSortService.sortByContractIdDesc(allRecords);

        assertEquals(11, sorted.size());

        // Verify descending order by contractId
        for (int i = 1; i < sorted.size(); i++) {
            assertTrue(sorted.get(i).getContractId() <= sorted.get(i - 1).getContractId(),
                    "Records should be in descending contractId order");
        }

        // Highest contractId should be first: 12323 (customer 5)
        assertEquals(12323, sorted.get(0).getContractId());
        // Lowest contractId should be last: 247 (customer 24)
        assertEquals(247, sorted.get(sorted.size() - 1).getContractId());
    }

    @Test
    void mergeFiles_handlesEmptyFirstList() {
        List<CustomerRecord> result = fileSortService.mergeFiles(
                List.of(),
                List.of(CustomerRecord.builder().customerId(1).lastName("a").firstName("b")
                        .contractId(1).comment("c").build())
        );

        assertEquals(1, result.size());
    }

    @Test
    void mergeFiles_handlesEmptySecondList() {
        List<CustomerRecord> result = fileSortService.mergeFiles(
                List.of(CustomerRecord.builder().customerId(1).lastName("a").firstName("b")
                        .contractId(1).comment("c").build()),
                List.of()
        );

        assertEquals(1, result.size());
    }

    @Test
    void mergeFiles_handlesBothEmpty() {
        List<CustomerRecord> result = fileSortService.mergeFiles(List.of(), List.of());

        assertTrue(result.isEmpty());
    }

    @Test
    void csvReadWrite_roundTrip(@TempDir Path tempDir) throws IOException {
        Path csvFile = tempDir.resolve("test.csv");
        List<CustomerRecord> records = List.of(
                CustomerRecord.builder().customerId(1).lastName("last-1").firstName("first-1")
                        .contractId(100).comment("test").build(),
                CustomerRecord.builder().customerId(2).lastName("last-2").firstName("first-2")
                        .contractId(200).comment("test2").build()
        );

        fileSortService.writeToCsv(csvFile, records);
        List<CustomerRecord> readBack = fileSortService.readFromCsv(csvFile);

        assertEquals(2, readBack.size());
        assertEquals(1, readBack.get(0).getCustomerId());
        assertEquals("last-1", readBack.get(0).getLastName());
        assertEquals(2, readBack.get(1).getCustomerId());
        assertEquals(200, readBack.get(1).getContractId());
    }
}
