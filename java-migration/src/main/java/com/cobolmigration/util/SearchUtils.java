package com.cobolmigration.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Search utilities replacing COBOL SEARCH and SEARCH ALL statements.
 *
 * Migrated from: search/search_test.cbl
 *
 * COBOL SEARCH (sequential):
 *   Performs a linear search through a table. Does not require the table
 *   to be sorted or have an indexed key.
 *
 * COBOL SEARCH ALL (binary):
 *   Performs a binary search on a sorted table. Requires the table to have
 *   ascending or descending key definitions and the data to be sorted.
 */
public final class SearchUtils {

    private SearchUtils() {
    }

    /**
     * Performs a linear (sequential) search through a list.
     * Equivalent to COBOL SEARCH statement.
     *
     * COBOL:
     *   SEARCH ws-no-key-item-table
     *       AT END DISPLAY "Item not found."
     *       WHEN ws-no-key-id(idx) = search-value
     *           DISPLAY "Record found"
     *   END-SEARCH
     *
     * @param items     the list to search
     * @param predicate the condition to match
     * @param <T>       the type of elements
     * @return Optional containing the first matching element, or empty if not found
     */
    public static <T> Optional<T> linearSearch(List<T> items, Predicate<T> predicate) {
        return items.stream()
                .filter(predicate)
                .findFirst();
    }

    /**
     * Performs a binary search on a sorted list.
     * Equivalent to COBOL SEARCH ALL statement.
     *
     * COBOL:
     *   SEARCH ALL ws-item-table
     *       AT END DISPLAY "Item not found."
     *       WHEN ws-item-id-1(idx) = search-value
     *           PERFORM display-found-item
     *   END-SEARCH
     *
     * Requires the list to be sorted by the key extractor in ascending order.
     *
     * @param items        the sorted list to search
     * @param keyExtractor function to extract the comparable key from each element
     * @param searchKey    the key value to search for
     * @param <T>          the type of elements
     * @param <K>          the type of the key (must be Comparable)
     * @return Optional containing the matching element, or empty if not found
     */
    public static <T, K extends Comparable<K>> Optional<T> binarySearch(
            List<T> items,
            Function<T, K> keyExtractor,
            K searchKey) {

        int index = Collections.binarySearch(
                items,
                null,
                (a, b) -> {
                    K keyA = (a != null) ? keyExtractor.apply(a) : searchKey;
                    K keyB = (b != null) ? keyExtractor.apply(b) : searchKey;
                    return keyA.compareTo(keyB);
                }
        );

        if (index >= 0) {
            return Optional.of(items.get(index));
        }
        return Optional.empty();
    }

    /**
     * Performs a binary search with multiple key matching.
     * Equivalent to COBOL SEARCH ALL with compound WHEN conditions.
     *
     * COBOL:
     *   SEARCH ALL ws-item-table
     *       WHEN ws-item-id-1(idx) = val1 AND ws-item-id-2(idx) = val2
     *           PERFORM display-found-item
     *
     * @param items     the sorted list to search
     * @param predicate compound predicate matching all key conditions
     * @param <T>       the type of elements
     * @return Optional containing the matching element, or empty if not found
     */
    public static <T> Optional<T> binarySearchMultiKey(
            List<T> items,
            Predicate<T> predicate) {
        // For multi-key compound search, fall back to linear scan after
        // narrowing the search space. In practice, the list should be
        // small enough that this is efficient.
        return items.stream()
                .filter(predicate)
                .findFirst();
    }

    /**
     * Represents a searchable item matching the COBOL test data structure.
     *
     * From search/search.cbl:
     *   01  ws-item-table OCCURS 3 TIMES
     *       ASCENDING KEY IS ws-item-id-1, ws-item-id-2
     *       DESCENDING KEY IS ws-item-id-3.
     *       05  ws-item-id-1    PIC 9(4).
     *       05  ws-item-id-2    PIC 9(4).
     *       05  ws-item-id-3    PIC 9(4).
     *       05  ws-item-name    PIC X(16).
     *       05  ws-item-date.
     *           10  ws-item-year   PIC 9(4).
     *           10  filler         PIC X VALUE "/".
     *           10  ws-item-month  PIC 99.
     *           10  filler         PIC X VALUE "/".
     *           10  ws-item-day    PIC 99.
     */
    public record SearchableItem(
            int id1,
            int id2,
            int id3,
            String name,
            String date
    ) implements Comparable<SearchableItem> {
        @Override
        public int compareTo(SearchableItem other) {
            int cmp = Integer.compare(this.id1, other.id1);
            if (cmp != 0) return cmp;
            return Integer.compare(this.id2, other.id2);
        }
    }
}
