package com.example.cobolmigration.service;

import com.example.cobolmigration.model.CustomerFileRecord;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Replaces merge_sort/merge_sort_test.cbl.
 * Provides merge and sort operations on customer file records.
 */
@Service
public class CustomerFileService {

    /**
     * Replaces merge-and-display-files paragraph (merge_sort_test.cbl lines 103-134).
     * Merges two lists (east + west customers) and sorts ascending by customerId,
     * matching: merge fd-sorting-file on ascending key f-customer-id.
     */
    public List<CustomerFileRecord> mergeAndSort(List<CustomerFileRecord> eastCustomers,
                                                  List<CustomerFileRecord> westCustomers) {
        List<CustomerFileRecord> merged = new ArrayList<>();
        merged.addAll(eastCustomers);
        merged.addAll(westCustomers);
        merged.sort(Comparator.comparingInt(CustomerFileRecord::getCustomerId));
        return merged;
    }

    /**
     * Replaces sort-and-display-file paragraph (merge_sort_test.cbl lines 138-169).
     * Sorts descending by contractId, matching:
     * sort fd-sorting-file on descending key f-customer-contract-id.
     */
    public List<CustomerFileRecord> sortByContractIdDescending(List<CustomerFileRecord> customers) {
        List<CustomerFileRecord> sorted = new ArrayList<>(customers);
        sorted.sort(Comparator.comparingInt(CustomerFileRecord::getContractId).reversed());
        return sorted;
    }

    /**
     * Generates the test data matching the create-test-data paragraph
     * (merge_sort_test.cbl lines 173-338).
     *
     * East customers (test-file-1): IDs 1, 5, 10, 50, 25, 75
     * West customers (test-file-2): IDs 999, 3, 30, 85, 24
     */
    public Map<String, List<CustomerFileRecord>> generateTestData() {
        List<CustomerFileRecord> east = new ArrayList<>();
        east.add(new CustomerFileRecord(1, "last-1", "first-1", 5423, "comment-1"));
        east.add(new CustomerFileRecord(5, "last-5", "first-5", 12323, "comment-5"));
        east.add(new CustomerFileRecord(10, "last-10", "first-10", 653, "comment-10"));
        east.add(new CustomerFileRecord(50, "last-50", "first-50", 5050, "comment-50"));
        east.add(new CustomerFileRecord(25, "last-25", "first-25", 7725, "comment-25"));
        east.add(new CustomerFileRecord(75, "last-75", "first-75", 1175, "comment-75"));

        List<CustomerFileRecord> west = new ArrayList<>();
        west.add(new CustomerFileRecord(999, "last-999", "first-999", 1610, "comment-99"));
        west.add(new CustomerFileRecord(3, "last-03", "first-03", 3331, "comment-03"));
        west.add(new CustomerFileRecord(30, "last-30", "first-30", 8765, "comment-30"));
        west.add(new CustomerFileRecord(85, "last-85", "first-85", 4567, "comment-85"));
        west.add(new CustomerFileRecord(24, "last-24", "first-24", 247, "comment-24"));

        return Map.of("east", east, "west", west);
    }

    /**
     * Runs the full demo: creates test data, merges, then sorts by contract ID descending.
     * This replicates the main-procedure flow of merge_sort_test.cbl (lines 91-100).
     */
    public Map<String, List<CustomerFileRecord>> runDemo() {
        Map<String, List<CustomerFileRecord>> testData = generateTestData();
        List<CustomerFileRecord> merged = mergeAndSort(testData.get("east"), testData.get("west"));
        List<CustomerFileRecord> sortedByContract = sortByContractIdDescending(merged);

        return Map.of(
            "mergedByCustomerId", merged,
            "sortedByContractIdDesc", sortedByContract
        );
    }
}
