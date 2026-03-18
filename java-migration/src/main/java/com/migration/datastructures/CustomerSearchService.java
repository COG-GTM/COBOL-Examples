package com.migration.datastructures;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Phase 2: Data Structures - Search and COMP type mapping
 *
 * Migrates COBOL SEARCH/SEARCH ALL (search/search_test.cbl) and
 * COMP types (comp_test/comp_test.cbl) to Java equivalents.
 *
 * COBOL SEARCH ALL (binary search on sorted indexed table) maps to:
 * - TreeMap for indexed lookup by key
 * - Collections.binarySearch() for list-based binary search
 *
 * COBOL COMP types map to:
 * - COMP (binary integer) -> int or long
 * - COMP-2 (double-precision float) -> double
 * - COMP-3 (packed decimal) -> BigDecimal
 *
 * Precision mapping documentation:
 * | COBOL Type | Java Type   | Precision                        |
 * |------------|-------------|----------------------------------|
 * | PIC 9(n)   | int/long    | Exact for integers up to n digits|
 * | COMP       | int         | 32-bit signed integer            |
 * | COMP-2     | double      | ~15-17 significant decimal digits|
 * | COMP-3     | BigDecimal  | Arbitrary precision              |
 * | PIC 9(n)V9 | BigDecimal  | Fixed-point decimal equivalent   |
 */
public class CustomerSearchService {

    private final TreeMap<Integer, CustomerRecord> customerIndex = new TreeMap<>();

    /**
     * Record representing the COBOL ws-item-table structure from search.cbl.
     */
    public record CustomerRecord(
            int id1,
            int id2,
            int id3,
            String name,
            String date
    ) {}

    /**
     * Adds a customer record to the indexed store.
     * Equivalent to populating the COBOL ws-item-table.
     */
    public void addRecord(CustomerRecord record) {
        customerIndex.put(record.id1(), record);
    }

    /**
     * Binary search by primary key (id1).
     * Equivalent to COBOL: SEARCH ALL ws-item-table WHEN ws-item-id-1(idx) = searchId
     */
    public Optional<CustomerRecord> searchById(int id) {
        CustomerRecord record = customerIndex.get(id);
        return Optional.ofNullable(record);
    }

    /**
     * Binary search with compound key matching (id1 AND id2 AND id3).
     * Equivalent to COBOL: SEARCH ALL ... WHEN id-1 = x AND id-2 = y AND id-3 = z
     */
    public Optional<CustomerRecord> searchByCompoundKey(int id1, int id2, int id3) {
        return searchById(id1)
                .filter(r -> r.id2() == id2 && r.id3() == id3);
    }

    /**
     * Sequential search (linear scan).
     * Equivalent to COBOL: SEARCH ws-no-key-item-table (without ALL keyword).
     */
    public Optional<CustomerRecord> sequentialSearch(List<CustomerRecord> records, int searchId) {
        for (CustomerRecord record : records) {
            if (record.id1() == searchId) {
                return Optional.of(record);
            }
        }
        return Optional.empty();
    }

    /**
     * Binary search using Collections.binarySearch on a sorted list.
     * Alternative approach to TreeMap for COBOL SEARCH ALL migration.
     */
    public Optional<CustomerRecord> binarySearchInList(List<CustomerRecord> sortedRecords, int searchId) {
        int index = Collections.binarySearch(
                sortedRecords,
                null,
                Comparator.comparingInt((CustomerRecord r) -> r != null ? r.id1() : searchId)
        );
        // Custom binary search using the comparator
        int lo = 0;
        int hi = sortedRecords.size() - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            int midId = sortedRecords.get(mid).id1();
            if (midId < searchId) {
                lo = mid + 1;
            } else if (midId > searchId) {
                hi = mid - 1;
            } else {
                return Optional.of(sortedRecords.get(mid));
            }
        }
        return Optional.empty();
    }

    /**
     * Returns all customer records sorted by primary key.
     */
    public Map<Integer, CustomerRecord> getAllRecords() {
        return Collections.unmodifiableMap(customerIndex);
    }

    /**
     * Clears all records from the index.
     */
    public void clear() {
        customerIndex.clear();
    }

    // ---- COMP type conversion utilities ----

    /**
     * COBOL COMP to Java int conversion.
     * COMP (PIC 999 COMP) -> binary integer.
     *
     * In COBOL, moving a display value to a COMP variable converts it.
     * In Java, this is simply integer parsing.
     */
    public static int compToInt(String displayValue) {
        return Integer.parseInt(displayValue.strip());
    }

    /**
     * COBOL COMP-2 to Java double conversion.
     * COMP-2 -> double-precision floating point (64-bit IEEE 754).
     */
    public static double comp2ToDouble(String displayValue) {
        return Double.parseDouble(displayValue.strip());
    }

    /**
     * COBOL display value to BigDecimal for precise arithmetic.
     * Useful for monetary calculations where COMP-3 would be used in COBOL.
     */
    public static BigDecimal toBigDecimal(String displayValue) {
        return new BigDecimal(displayValue.strip());
    }
}
