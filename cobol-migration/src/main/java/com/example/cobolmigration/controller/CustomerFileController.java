package com.example.cobolmigration.controller;

import com.example.cobolmigration.model.CustomerFileRecord;
import com.example.cobolmigration.service.CustomerFileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST endpoints replacing merge_sort/merge_sort_test.cbl.
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerFileController {

    private final CustomerFileService customerFileService;

    public CustomerFileController(CustomerFileService customerFileService) {
        this.customerFileService = customerFileService;
    }

    /**
     * Accepts two customer lists (east and west), returns merged+sorted result.
     * Replaces the merge-and-display-files paragraph.
     */
    @PostMapping("/merge-sort")
    public ResponseEntity<List<CustomerFileRecord>> mergeAndSort(
            @RequestBody MergeSortRequest request) {
        return ResponseEntity.ok(
            customerFileService.mergeAndSort(request.eastCustomers(), request.westCustomers())
        );
    }

    /**
     * Runs the full demo: create test data, merge, sort — returns results.
     * Replaces the main-procedure flow (create-test-data, merge-and-display-files,
     * sort-and-display-file).
     */
    @GetMapping("/demo")
    public ResponseEntity<Map<String, List<CustomerFileRecord>>> runDemo() {
        return ResponseEntity.ok(customerFileService.runDemo());
    }

    public record MergeSortRequest(
        List<CustomerFileRecord> eastCustomers,
        List<CustomerFileRecord> westCustomers
    ) {}
}
