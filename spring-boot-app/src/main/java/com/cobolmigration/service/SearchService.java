package com.cobolmigration.service;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Service implementing binary search equivalent of COBOL SEARCH ALL.
 *
 * <p>Migrated from {@code search/search.cbl} (lines 61-66) which uses
 * SEARCH ALL on an indexed table with ascending/descending keys:</p>
 * <pre>
 *   SEARCH ALL ws-item-table
 *       AT END DISPLAY "Item not found."
 *       WHEN ws-item-id-1(idx) = ws-accept-id-1
 *           PERFORM display-found-item
 *   END-SEARCH
 * </pre>
 *
 * <p>In COBOL, SEARCH ALL requires the table to be sorted by its key and
 * performs a binary search. In Java, we use {@link Collections#binarySearch}
 * to achieve the same O(log n) lookup.</p>
 */
@Service
public class SearchService {

    /**
     * Performs a binary search for an item by its ID in a pre-sorted list.
     *
     * <p>The list must be sorted in ascending order by the item's ID
     * (matching the COBOL requirement for SEARCH ALL with ascending key).</p>
     *
     * @param items    a list of {@link SearchableItem} sorted by ID ascending
     * @param searchId the ID to search for
     * @return an Optional containing the found item, or empty if not found
     */
    public Optional<SearchableItem> binarySearchById(
            List<SearchableItem> items, int searchId) {

        int index = Collections.binarySearch(
                items,
                new SearchableItem(searchId, "", ""),
                Comparator.comparingInt(SearchableItem::getId)
        );

        if (index >= 0) {
            return Optional.of(items.get(index));
        }
        return Optional.empty();
    }

    /**
     * Performs a sequential search (linear scan) for an item by its ID.
     *
     * <p>Equivalent to the COBOL sequential SEARCH (without ALL), which
     * does not require sorting:</p>
     * <pre>
     *   SEARCH ws-no-key-item-table
     *       AT END DISPLAY "Item not found."
     *       WHEN ws-no-key-id(idx-2) = ws-accept-id-1
     *           ...
     *   END-SEARCH
     * </pre>
     *
     * @param items    a list of items (need not be sorted)
     * @param searchId the ID to search for
     * @return an Optional containing the found item, or empty if not found
     */
    public Optional<SearchableItem> sequentialSearchById(
            List<SearchableItem> items, int searchId) {

        return items.stream()
                .filter(item -> item.getId() == searchId)
                .findFirst();
    }

    /**
     * Represents a searchable item, modeled after the COBOL indexed table:
     * <pre>
     *   01  ws-item-table occurs 3 times
     *           ascending key is ws-item-id-1 ...
     *       05  ws-item-id-1   pic 9(4).
     *       05  ws-item-name   pic x(16).
     *       05  ws-item-date   ...
     * </pre>
     */
    public static class SearchableItem {
        private final int id;
        private final String name;
        private final String date;

        public SearchableItem(int id, String name, String date) {
            this.id = id;
            this.name = name;
            this.date = date;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDate() {
            return date;
        }

        @Override
        public String toString() {
            return "SearchableItem{id=" + id +
                    ", name='" + name + '\'' +
                    ", date='" + date + '\'' + '}';
        }
    }
}
