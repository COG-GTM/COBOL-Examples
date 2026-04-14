package com.cobolmigration.service;

import com.cobolmigration.model.ItemRecord;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Replaces COBOL SEARCH and SEARCH ALL from search/search.cbl lines 17-33.
 * Provides linear search (SEARCH) and binary search (SEARCH ALL) for ItemRecord tables.
 */
@Service
public class SearchService {

    /**
     * Linear search - replaces COBOL SEARCH (sequential).
     * Does not require data to be sorted.
     * Source: search/search.cbl lines 99-109
     *
     * @param table     the list of ItemRecords to search
     * @param condition the predicate to match
     * @return Optional containing the found record, or empty if not found
     */
    public Optional<ItemRecord> linearSearch(List<ItemRecord> table, Predicate<ItemRecord> condition) {
        if (table == null || table.isEmpty()) {
            return Optional.empty();
        }
        return table.stream().filter(condition).findFirst();
    }

    /**
     * Binary search - replaces COBOL SEARCH ALL (binary search on ascending key).
     * Requires data to be sorted by itemId1 ascending.
     * Source: search/search.cbl lines 60-66
     *
     * @param sortedTable the list of ItemRecords sorted by itemId1 ascending
     * @param targetId    the itemId1 value to search for
     * @return Optional containing the found record, or empty if not found
     */
    public Optional<ItemRecord> binarySearch(List<ItemRecord> sortedTable, int targetId) {
        if (sortedTable == null || sortedTable.isEmpty()) {
            return Optional.empty();
        }

        ItemRecord key = new ItemRecord();
        key.setItemId1(targetId);

        int index = Collections.binarySearch(
                sortedTable,
                key,
                Comparator.comparingInt(ItemRecord::getItemId1)
        );

        if (index >= 0) {
            return Optional.of(sortedTable.get(index));
        }
        return Optional.empty();
    }

    /**
     * Multi-key binary search - replaces COBOL SEARCH ALL with multiple key conditions.
     * Source: search/search.cbl lines 82-89
     *
     * @param sortedTable the list of ItemRecords sorted by keys
     * @param targetId1   the itemId1 value to match
     * @param targetId2   the itemId2 value to match
     * @param targetId3   the itemId3 value to match
     * @return Optional containing the found record, or empty if not found
     */
    public Optional<ItemRecord> binarySearchMultiKey(List<ItemRecord> sortedTable,
                                                     int targetId1, int targetId2, int targetId3) {
        if (sortedTable == null || sortedTable.isEmpty()) {
            return Optional.empty();
        }

        return sortedTable.stream()
                .filter(item -> item.getItemId1() == targetId1
                        && item.getItemId2() == targetId2
                        && item.getItemId3() == targetId3)
                .findFirst();
    }
}
