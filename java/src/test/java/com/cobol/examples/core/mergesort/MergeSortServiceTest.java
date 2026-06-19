package com.cobol.examples.core.mergesort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class MergeSortServiceTest {

    @Test
    void mergesByAscendingId() {
        List<CustomerRecord> merged = MergeSortService.mergeById(SampleCustomers.east(), SampleCustomers.west());

        assertEquals(11, merged.size());
        for (int i = 1; i < merged.size(); i++) {
            assertTrue(merged.get(i - 1).id() <= merged.get(i).id(), "ids must be ascending");
        }
        assertEquals(1, merged.get(0).id());
        assertEquals(999, merged.get(merged.size() - 1).id());
    }

    @Test
    void sortsByDescendingContractId() {
        List<CustomerRecord> merged = MergeSortService.mergeById(SampleCustomers.east(), SampleCustomers.west());
        List<CustomerRecord> sorted = MergeSortService.sortByContractIdDescending(merged);

        for (int i = 1; i < sorted.size(); i++) {
            assertTrue(sorted.get(i - 1).contractId() >= sorted.get(i).contractId(),
                    "contract ids must be descending");
        }
        assertEquals(12323, sorted.get(0).contractId());
        assertEquals(247, sorted.get(sorted.size() - 1).contractId());
    }
}
