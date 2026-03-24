package com.cobolmigration.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Search utility methods replacing COBOL SEARCH and SEARCH ALL statements.
 * From search/search.cbl:
 *
 * Sequential search (SEARCH):
 *   SEARCH ws-no-key-item-table
 *       AT END DISPLAY "Item not found."
 *       WHEN ws-no-key-id(idx-2) = ws-accept-id-1
 *           DISPLAY "Record found:"
 *   Does not require sorted data or keys. Slower linear scan.
 *
 * Binary search (SEARCH ALL):
 *   SEARCH ALL ws-item-table
 *       AT END DISPLAY "Item not found."
 *       WHEN ws-item-id-1(idx) = ws-accept-id-1
 *           PERFORM display-found-item
 *   Requires table to have ASCENDING/DESCENDING KEY and data to be sorted.
 */
public final class SearchUtils {

    private SearchUtils() {
    }

    /**
     * Linear (sequential) search through a list.
     * Replaces: SEARCH ws-no-key-item-table ... WHEN condition
     * Does not require sorted data.
     */
    public static <T> Optional<T> linearSearch(List<T> table, Predicate<T> condition) {
        for (T item : table) {
            if (condition.test(item)) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    /**
     * Binary search through a sorted list.
     * Replaces: SEARCH ALL ws-item-table ... WHEN key = value
     * Requires the list to be sorted by the comparator's key.
     */
    public static <T> Optional<T> binarySearch(List<T> sortedTable,
                                                Comparator<T> comparator,
                                                T key) {
        int index = Collections.binarySearch(sortedTable, key, comparator);
        if (index >= 0) {
            return Optional.of(sortedTable.get(index));
        }
        return Optional.empty();
    }

    /**
     * Find the index of an element using linear search.
     * Returns -1 if not found (similar to COBOL AT END condition).
     */
    public static <T> int linearSearchIndex(List<T> table, Predicate<T> condition) {
        for (int i = 0; i < table.size(); i++) {
            if (condition.test(table.get(i))) {
                return i;
            }
        }
        return -1;
    }
}
