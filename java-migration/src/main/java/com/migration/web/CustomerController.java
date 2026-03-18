package com.migration.web;

import com.migration.datastructures.Address;
import com.migration.datastructures.CorporateCustomer;
import com.migration.datastructures.Customer;
import com.migration.datastructures.CustomerSearchService;
import com.migration.datastructures.PersonCustomer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Phase 7: UI Layer - Customer REST Controller
 *
 * Provides REST endpoints for customer data operations using the
 * Phase 2 data structures (sealed interface + records).
 *
 * COBOL display_test/display_test.cbl DISPLAY operations are replaced
 * by JSON responses returned from these endpoints.
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerSearchService searchService;

    public CustomerController() {
        this.searchService = new CustomerSearchService();
        initializeSampleData();
    }

    private void initializeSampleData() {
        searchService.addRecord(new CustomerSearchService.CustomerRecord(
                1, 101, 500, "test item 1", "2021/01/01"));
        searchService.addRecord(new CustomerSearchService.CustomerRecord(
                2, 102, 499, "test item 2", "2021/02/02"));
        searchService.addRecord(new CustomerSearchService.CustomerRecord(
                3, 103, 498, "test item 3", "2021/03/03"));
    }

    /**
     * GET /api/customers - List all customer records.
     */
    @GetMapping
    public ResponseEntity<Map<Integer, CustomerSearchService.CustomerRecord>> getAllCustomers() {
        return ResponseEntity.ok(searchService.getAllRecords());
    }

    /**
     * GET /api/customers/{id} - Search for a customer by ID.
     * Replaces COBOL SEARCH ALL binary search.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerSearchService.CustomerRecord> getCustomer(@PathVariable int id) {
        return searchService.searchById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/customers/person - Create a person-type customer.
     * Demonstrates the PersonCustomer record (COBOL ws-customer-type-person).
     */
    @PostMapping("/person")
    public ResponseEntity<Map<String, Object>> createPerson(@RequestBody Map<String, String> body) {
        Customer customer = new PersonCustomer(
                body.getOrDefault("firstName", ""),
                body.getOrDefault("lastName", ""),
                new Address(
                        body.getOrDefault("street", ""),
                        body.getOrDefault("state", ""),
                        body.getOrDefault("zipCode", "")
                )
        );
        return ResponseEntity.status(201).body(Map.of(
                "type", "PERSON",
                "displayName", customer.displayName(),
                "address", customer.address().formatted()
        ));
    }

    /**
     * POST /api/customers/corporate - Create a corporate-type customer.
     * Demonstrates the CorporateCustomer record (COBOL ws-customer-type-corp).
     */
    @PostMapping("/corporate")
    public ResponseEntity<Map<String, Object>> createCorporate(@RequestBody Map<String, String> body) {
        Customer customer = new CorporateCustomer(
                body.getOrDefault("corpName", ""),
                new Address(
                        body.getOrDefault("street", ""),
                        body.getOrDefault("state", ""),
                        body.getOrDefault("zipCode", "")
                )
        );
        return ResponseEntity.status(201).body(Map.of(
                "type", "CORPORATE",
                "displayName", customer.displayName(),
                "address", customer.address().formatted()
        ));
    }
}
