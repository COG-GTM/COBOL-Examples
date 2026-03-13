package com.cobolmigration.batch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for MergeSortBatchConfig merge and sort operations.
 * Verifies behavior matches COBOL merge_sort/merge_sort_test.cbl.
 */
class MergeSortBatchConfigTest {

    private MergeSortBatchConfig config;

    // Test data matching the COBOL create-test-data paragraph
    private List<CustomerRecord> eastRecords; // fd-test-file-1
    private List<CustomerRecord> westRecords; // fd-test-file-2

    @BeforeEach
    void setUp() {
        config = new MergeSortBatchConfig();

        // East region records (test-file-1.txt) - already sorted by customer-id
        eastRecords = List.of(
                new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"),
                new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"),
                new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"),
                new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"),
                new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"),
                new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75")
        );

        // West region records (test-file-2.txt) - already sorted by customer-id
        westRecords = List.of(
                new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"),
                new CustomerRecord(24, "last-24", "first-24", 247, "comment-24"),
                new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"),
                new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"),
                new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99")
        );
    }

    @Test
    void mergeByCustomerId_shouldMergeTwoListsInAscendingOrder() {
        // COBOL: MERGE fd-sorting-file ON ASCENDING KEY f-customer-id
        //        USING fd-test-file-1 fd-test-file-2 GIVING fd-merged-file
        List<CustomerRecord> merged = config.mergeByCustomerId(eastRecords, westRecords);

        assertEquals(11, merged.size());

        // Verify ascending order by customer-id
        for (int i = 1; i < merged.size(); i++) {
            assertTrue(merged.get(i).getCustomerId() >= merged.get(i - 1).getCustomerId(),
                    "Records should be sorted by customer-id ascending");
        }

        // Verify specific ordering matches expected merge output
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
    void sortByContractIdDescending_shouldSortCorrectly() {
        // COBOL: SORT fd-sorting-file ON DESCENDING KEY f-customer-contract-id
        //        USING fd-merged-file GIVING fd-sorted-contract-id
        List<CustomerRecord> merged = config.mergeByCustomerId(eastRecords, westRecords);
        List<CustomerRecord> sorted = config.sortByContractIdDescending(merged);

        assertEquals(11, sorted.size());

        // Verify descending order by contract-id
        for (int i = 1; i < sorted.size(); i++) {
            assertTrue(sorted.get(i).getContractId() <= sorted.get(i - 1).getContractId(),
                    "Records should be sorted by contract-id descending");
        }

        // First should be highest contract-id (12323)
        assertEquals(12323, sorted.get(0).getContractId());
        // Last should be lowest contract-id (247)
        assertEquals(247, sorted.get(sorted.size() - 1).getContractId());
    }

    @Test
    void mergeByCustomerId_shouldHandleEmptyLists() {
        List<CustomerRecord> merged = config.mergeByCustomerId(List.of(), westRecords);
        assertEquals(westRecords.size(), merged.size());

        merged = config.mergeByCustomerId(eastRecords, List.of());
        assertEquals(eastRecords.size(), merged.size());

        merged = config.mergeByCustomerId(List.of(), List.of());
        assertTrue(merged.isEmpty());
    }

    @Test
    void customerRecord_fixedWidthRoundTrip() {
        CustomerRecord original = new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1");
        String fixedWidth = original.toFixedWidthString();

        assertEquals(135, fixedWidth.length());

        CustomerRecord parsed = CustomerRecord.fromFixedWidthString(fixedWidth);
        assertEquals(original.getCustomerId(), parsed.getCustomerId());
        assertEquals(original.getLastName(), parsed.getLastName());
        assertEquals(original.getFirstName(), parsed.getFirstName());
        assertEquals(original.getContractId(), parsed.getContractId());
        assertEquals(original.getComment(), parsed.getComment());
    }
}
