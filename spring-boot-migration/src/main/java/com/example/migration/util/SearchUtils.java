package com.example.migration.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Utility class replacing COBOL SEARCH and SEARCH ALL operations
 * from search/search.cbl.
 *
 * COBOL SEARCH ALL (binary search) requires:
 *   - Table must have ascending/descending key(s)
 *   - Table must be sorted on the key(s)
 *   - Uses indexed access
 *
 * COBOL SEARCH (sequential search):
 *   - No sorting required
 *   - Slower linear scan
 *
 * This class provides both via Java's Collections.binarySearch() and
 * linear search with predicates.
 */
public final class SearchUtils {

    private SearchUtils() {
    }

    /**
     * Replaces COBOL: SEARCH ALL ws-item-table (search.cbl lines 61-66).
     *
     * Binary search on a sorted list by a key using a Comparator.
     * The list MUST be sorted by the same comparator for correct results.
     *
     * @param sortedList the sorted list to search
     * @param key the key to search for
     * @param comparator comparator defining the sort order
     * @return the index of the found element, or -1 if not found
     */
    public static <T> int binarySearch(List<T> sortedList, T key, Comparator<T> comparator) {
        int index = Collections.binarySearch(sortedList, key, comparator);
        return index >= 0 ? index : -1;
    }

    /**
     * Replaces COBOL: SEARCH ws-no-key-item-table (search.cbl lines 100-109).
     *
     * Sequential search through a list using a predicate.
     * Does not require the list to be sorted.
     *
     * @param list the list to search
     * @param predicate condition to match
     * @return the index of the first matching element, or -1 if not found
     */
    public static <T> int sequentialSearch(List<T> list, java.util.function.Predicate<T> predicate) {
        for (int i = 0; i < list.size(); i++) {
            if (predicate.test(list.get(i))) {
                return i;
            }
        }
        return -1;
    }
}
