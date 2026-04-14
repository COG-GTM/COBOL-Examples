package com.cobolmigration.service;

import com.cobolmigration.model.Item;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Search service migrated from search/search.cbl (lines 61-66, 99-109).
 *
 * Provides binary search (SEARCH ALL) and linear search (SEARCH) equivalents.
 *
 * COBOL SEARCH ALL (binary search) from search.cbl:
 *   SEARCH ALL ws-item-table
 *     AT END DISPLAY "Item not found."
 *     WHEN ws-item-id-1(idx) = ws-accept-id-1
 *       PERFORM display-found-item
 *   END-SEARCH
 *
 * COBOL SEARCH (sequential search) from search.cbl:
 *   SEARCH ws-no-key-item-table
 *     AT END DISPLAY "Item not found."
 *     WHEN ws-no-key-id(idx-2) = ws-accept-id-1
 *       ...
 *   END-SEARCH
 */
@Service
public class SearchService {

    /**
     * Binary search for an item by id1 in a sorted list.
     * Equivalent to COBOL: SEARCH ALL ws-item-table
     *   WHEN ws-item-id-1(idx) = targetId1
     *
     * The list must be sorted by the ascending key (id1) as specified in the
     * COBOL table definition: ASCENDING KEY IS ws-item-id-1, ws-item-id-2.
     *
     * Uses Collections.binarySearch() with a custom Comparator that matches
     * COBOL's comparison on ws-item-id-1.
     *
     * @param sortedItems list of items sorted by id1 ascending
     * @param targetId1   the id1 value to search for
     * @return Optional containing the found item, or empty if not found
     */
    public Optional<Item> binarySearch(List<Item> sortedItems, int targetId1) {
        if (sortedItems == null || sortedItems.isEmpty()) {
            return Optional.empty();
        }

        // Create a key item for binary search comparison
        Item key = new Item();
        key.setId1(targetId1);

        int index = Collections.binarySearch(sortedItems, key,
                Comparator.comparingInt(Item::getId1));

        if (index >= 0) {
            return Optional.of(sortedItems.get(index));
        }
        return Optional.empty();
    }

    /**
     * Binary search for an item matching all three IDs.
     * Equivalent to COBOL: SEARCH ALL ws-item-table
     *   WHEN ws-item-id-1(idx) = ws-accept-id-1
     *   AND ws-item-id-2(idx) = ws-accept-id-2
     *   AND ws-item-id-3(idx) = ws-accept-id-3
     *
     * @param sortedItems list of items sorted according to COBOL key spec
     * @param targetId1   the id1 value to match
     * @param targetId2   the id2 value to match
     * @param targetId3   the id3 value to match
     * @return Optional containing the found item, or empty if not found
     */
    public Optional<Item> binarySearchAllKeys(List<Item> sortedItems,
                                              int targetId1, int targetId2, int targetId3) {
        if (sortedItems == null || sortedItems.isEmpty()) {
            return Optional.empty();
        }

        // Create a key item with all search criteria
        Item key = new Item();
        key.setId1(targetId1);
        key.setId2(targetId2);
        key.setId3(targetId3);

        // Use the Item's natural ordering (compareTo) which matches COBOL key spec
        int index = Collections.binarySearch(sortedItems, key);

        if (index >= 0) {
            return Optional.of(sortedItems.get(index));
        }
        return Optional.empty();
    }

    /**
     * Linear (sequential) search for items matching a predicate.
     * Equivalent to COBOL: SEARCH ws-no-key-item-table
     *   WHEN condition
     *
     * Sequential searching does not require sorted data or keys,
     * matching COBOL's SEARCH (without ALL) behavior.
     *
     * @param items     list of items to search (order not required)
     * @param condition the predicate to match against
     * @return Optional containing the first matching item, or empty if not found
     */
    public Optional<Item> linearSearch(List<Item> items, Predicate<Item> condition) {
        if (items == null || items.isEmpty()) {
            return Optional.empty();
        }

        for (Item item : items) {
            if (condition.test(item)) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }
}
