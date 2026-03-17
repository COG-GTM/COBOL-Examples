package com.example.cobolmigration.service;

import com.example.cobolmigration.model.CustomerFileRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests merge and sort with the same test data from merge_sort/merge_sort_test.cbl.
 */
class CustomerFileServiceTest {

    private CustomerFileService service;

    @BeforeEach
    void setUp() {
        service = new CustomerFileService();
    }

    @Test
    void generateTestData_shouldCreateEastAndWestCustomerLists() {
        Map<String, List<CustomerFileRecord>> data = service.generateTestData();
        assertEquals(6, data.get("east").size());
        assertEquals(5, data.get("west").size());
    }

    @Test
    void generateTestData_eastCustomersMatchCobolData() {
        // From merge_sort_test.cbl create-test-data (lines 185-259)
        Map<String, List<CustomerFileRecord>> data = service.generateTestData();
        List<CustomerFileRecord> east = data.get("east");

        assertEquals(1, east.get(0).getCustomerId());
        assertEquals("last-1", east.get(0).getLastName());
        assertEquals("first-1", east.get(0).getFirstName());
        assertEquals(5423, east.get(0).getContractId());

        assertEquals(75, east.get(5).getCustomerId());
        assertEquals(1175, east.get(5).getContractId());
    }

    @Test
    void generateTestData_westCustomersMatchCobolData() {
        // From merge_sort_test.cbl create-test-data (lines 272-333)
        Map<String, List<CustomerFileRecord>> data = service.generateTestData();
        List<CustomerFileRecord> west = data.get("west");

        assertEquals(999, west.get(0).getCustomerId());
        assertEquals(1610, west.get(0).getContractId());

        assertEquals(24, west.get(4).getCustomerId());
        assertEquals(247, west.get(4).getContractId());
    }

    @Test
    void mergeAndSort_shouldMergeAndSortAscendingByCustomerId() {
        // Replaces merge-and-display-files: merge on ascending key f-customer-id
        Map<String, List<CustomerFileRecord>> data = service.generateTestData();
        List<CustomerFileRecord> merged = service.mergeAndSort(data.get("east"), data.get("west"));

        assertEquals(11, merged.size());

        // Verify ascending order by customerId
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
    void sortByContractIdDescending_shouldSortDescending() {
        // Replaces sort-and-display-file: sort on descending key f-customer-contract-id
        Map<String, List<CustomerFileRecord>> data = service.generateTestData();
        List<CustomerFileRecord> merged = service.mergeAndSort(data.get("east"), data.get("west"));
        List<CustomerFileRecord> sorted = service.sortByContractIdDescending(merged);

        assertEquals(11, sorted.size());

        // Verify descending order by contractId
        assertEquals(12323, sorted.get(0).getContractId());
        assertEquals(8765, sorted.get(1).getContractId());
        assertEquals(7725, sorted.get(2).getContractId());
        assertEquals(5423, sorted.get(3).getContractId());
        assertEquals(5050, sorted.get(4).getContractId());
        assertEquals(4567, sorted.get(5).getContractId());
        assertEquals(3331, sorted.get(6).getContractId());
        assertEquals(1610, sorted.get(7).getContractId());
        assertEquals(1175, sorted.get(8).getContractId());
        assertEquals(653, sorted.get(9).getContractId());
        assertEquals(247, sorted.get(10).getContractId());
    }

    @Test
    void runDemo_shouldReturnBothMergedAndSortedResults() {
        Map<String, List<CustomerFileRecord>> result = service.runDemo();
        assertNotNull(result.get("mergedByCustomerId"));
        assertNotNull(result.get("sortedByContractIdDesc"));
        assertEquals(11, result.get("mergedByCustomerId").size());
        assertEquals(11, result.get("sortedByContractIdDesc").size());
    }
}
