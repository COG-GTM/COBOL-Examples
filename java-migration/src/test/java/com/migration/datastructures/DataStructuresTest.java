package com.migration.datastructures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Phase 2: Data Structures Tests
 *
 * Test cases mirror the COBOL examples from:
 * - redifines/redefines.cbl (Customer polymorphism)
 * - search/search.cbl (Binary and sequential search)
 * - comp_test/comp_test.cbl (COMP type conversions)
 */
class DataStructuresTest {

    @Nested
    @DisplayName("Customer Polymorphism (COBOL: REDEFINES)")
    class CustomerTests {

        @Test
        @DisplayName("Person customer with first/last name")
        void personCustomer() {
            // COBOL: ws-customer-type-person(1), first="test-first", last="test-last"
            Customer customer = new PersonCustomer(
                    "test-first",
                    "test-last",
                    new Address("123 fake st", "NV", "12345")
            );

            assertInstanceOf(PersonCustomer.class, customer);
            assertEquals("test-first test-last", customer.displayName());
            assertEquals("123 fake st, NV 12345", customer.address().formatted());
        }

        @Test
        @DisplayName("Corporate customer with corp name")
        void corporateCustomer() {
            // COBOL: ws-customer-type-corp(2), corp-name="no-name corp"
            Customer customer = new CorporateCustomer(
                    "no-name corp",
                    new Address("567 real st", "NY", "11795")
            );

            assertInstanceOf(CorporateCustomer.class, customer);
            assertEquals("no-name corp", customer.displayName());
            assertEquals("567 real st, NY 11795", customer.address().formatted());
        }

        @Test
        @DisplayName("Pattern matching on customer type (replaces COBOL IF ws-customer-type-person)")
        void customerPatternMatching() {
            // COBOL: IF ws-customer-type-person(idx) THEN display first/last ELSE display corp
            Customer person = new PersonCustomer("John", "Doe", new Address("1 St", "CA", "90000"));
            Customer corp = new CorporateCustomer("Acme Inc", new Address("2 St", "NY", "10000"));

            // Java sealed interface + instanceof replaces COBOL 88-level conditions
            String personResult;
            if (person instanceof PersonCustomer p) {
                personResult = "Person: " + p.firstName() + " " + p.lastName();
            } else if (person instanceof CorporateCustomer c) {
                personResult = "Corp: " + c.corpName();
            } else {
                personResult = "Unknown";
            }
            assertEquals("Person: John Doe", personResult);

            String corpResult;
            if (corp instanceof PersonCustomer p) {
                corpResult = "Person: " + p.firstName() + " " + p.lastName();
            } else if (corp instanceof CorporateCustomer c) {
                corpResult = "Corp: " + c.corpName();
            } else {
                corpResult = "Unknown";
            }
            assertEquals("Corp: Acme Inc", corpResult);
        }

        @Test
        @DisplayName("All three test records from COBOL redefines.cbl")
        void allTestRecords() {
            // Record 1: Person with first/last name
            Customer c1 = new PersonCustomer("test-first", "test-last",
                    new Address("123 fake st", "NV", "12345"));
            // Record 2: Corp with corp name
            Customer c2 = new CorporateCustomer("no-name corp",
                    new Address("567 real st", "NY", "11795"));
            // Record 3: Person with corp name set (COBOL allows this via REDEFINES)
            // In Java, we model this correctly as a PersonCustomer
            Customer c3 = new PersonCustomer("SET CORP V", "ALUE",
                    new Address("890 what st", "MA", "09345"));

            assertEquals("test-first test-last", c1.displayName());
            assertEquals("no-name corp", c2.displayName());
            assertInstanceOf(PersonCustomer.class, c3);
        }
    }

    @Nested
    @DisplayName("Search Tests (COBOL: SEARCH / SEARCH ALL)")
    class SearchTests {

        private CustomerSearchService searchService;

        @BeforeEach
        void setUp() {
            searchService = new CustomerSearchService();

            // Setup test data matching search.cbl setup-test-data
            searchService.addRecord(new CustomerSearchService.CustomerRecord(
                    1, 101, 500, "test item 1", "2021/01/01"));
            searchService.addRecord(new CustomerSearchService.CustomerRecord(
                    2, 102, 499, "test item 2", "2021/02/02"));
            searchService.addRecord(new CustomerSearchService.CustomerRecord(
                    3, 103, 498, "test item 3", "2021/03/03"));
        }

        @Test
        @DisplayName("SEARCH ALL by single key (binary search)")
        void searchAllSingleKey() {
            // COBOL: SEARCH ALL ws-item-table WHEN ws-item-id-1(idx) = 2
            Optional<CustomerSearchService.CustomerRecord> result = searchService.searchById(2);

            assertTrue(result.isPresent());
            assertEquals("test item 2", result.get().name());
            assertEquals("2021/02/02", result.get().date());
        }

        @Test
        @DisplayName("SEARCH ALL by compound key")
        void searchAllCompoundKey() {
            // COBOL: WHEN ws-item-id-1 = 1 AND ws-item-id-2 = 101 AND ws-item-id-3 = 500
            Optional<CustomerSearchService.CustomerRecord> result =
                    searchService.searchByCompoundKey(1, 101, 500);

            assertTrue(result.isPresent());
            assertEquals("test item 1", result.get().name());
        }

        @Test
        @DisplayName("SEARCH ALL compound key - no match when one key differs")
        void searchAllCompoundKeyNoMatch() {
            // All three keys must match
            Optional<CustomerSearchService.CustomerRecord> result =
                    searchService.searchByCompoundKey(1, 999, 500);

            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("SEARCH ALL - item not found")
        void searchAllNotFound() {
            // COBOL: AT END DISPLAY "Item not found."
            Optional<CustomerSearchService.CustomerRecord> result = searchService.searchById(999);
            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("Sequential search (COBOL: SEARCH without ALL)")
        void sequentialSearch() {
            // COBOL: SEARCH ws-no-key-item-table (no sorting required)
            List<CustomerSearchService.CustomerRecord> unsortedRecords = List.of(
                    new CustomerSearchService.CustomerRecord(2, 0, 0, "Value of id 2.", ""),
                    new CustomerSearchService.CustomerRecord(3, 0, 0, "Value of id 3.", ""),
                    new CustomerSearchService.CustomerRecord(1, 0, 0, "Value of id 1.", "")
            );

            Optional<CustomerSearchService.CustomerRecord> result =
                    searchService.sequentialSearch(unsortedRecords, 1);

            assertTrue(result.isPresent());
            assertEquals("Value of id 1.", result.get().name());
        }

        @Test
        @DisplayName("Binary search in sorted list")
        void binarySearchInList() {
            List<CustomerSearchService.CustomerRecord> sorted = List.of(
                    new CustomerSearchService.CustomerRecord(1, 101, 500, "test item 1", "2021/01/01"),
                    new CustomerSearchService.CustomerRecord(2, 102, 499, "test item 2", "2021/02/02"),
                    new CustomerSearchService.CustomerRecord(3, 103, 498, "test item 3", "2021/03/03")
            );

            Optional<CustomerSearchService.CustomerRecord> result =
                    searchService.binarySearchInList(sorted, 3);

            assertTrue(result.isPresent());
            assertEquals("test item 3", result.get().name());
        }
    }

    @Nested
    @DisplayName("COMP Type Tests (COBOL: COMP/COMP-2/COMP-3)")
    class CompTests {

        @Test
        @DisplayName("COMP to int conversion")
        void compToInt() {
            // COBOL: MOVE 12 TO ws-comp-val. MULTIPLY ws-comp-val BY 2.
            int compVal = CustomerSearchService.compToInt("12");
            compVal *= 2;
            assertEquals(24, compVal);
        }

        @Test
        @DisplayName("COMP-2 to double conversion")
        void comp2ToDouble() {
            // COBOL: ws-data-comp-value COMP-2 = 12345.63
            double val = CustomerSearchService.comp2ToDouble("12345.63");
            assertEquals(12345.63, val, 0.001);
        }

        @Test
        @DisplayName("BigDecimal for precise arithmetic (COMP-3 equivalent)")
        void bigDecimalPrecision() {
            BigDecimal val = CustomerSearchService.toBigDecimal("12345.63");
            assertEquals(new BigDecimal("12345.63"), val);

            // Demonstrate precision that double would lose
            BigDecimal a = CustomerSearchService.toBigDecimal("0.1");
            BigDecimal b = CustomerSearchService.toBigDecimal("0.2");
            assertEquals(new BigDecimal("0.3"), a.add(b));
        }

        @Test
        @DisplayName("Display to COMP round-trip (COBOL: MOVE input TO ws-comp-val)")
        void displayToCompRoundTrip() {
            // COBOL: ACCEPT ws-input -> MOVE ws-input TO ws-comp-val -> DISPLAY ws-comp-val
            String input = "42";
            int compVal = CustomerSearchService.compToInt(input);
            assertEquals(42, compVal);
            assertEquals("42", String.valueOf(compVal));
        }
    }
}
