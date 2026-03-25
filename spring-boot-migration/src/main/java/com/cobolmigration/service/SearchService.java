package com.cobolmigration.service;

import com.cobolmigration.model.SearchItem;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service replacing the COBOL SEARCH/SEARCH ALL operations in search/search.cbl.
 * Implements binary search (SEARCH ALL) and sequential search (SEARCH) patterns.
 */
@Service
public class SearchService {

    /**
     * Performs a binary search for an item by itemId1.
     * Replaces SEARCH ALL ws-item-table in search.cbl (lines 61-66).
     * The list must be sorted by itemId1 in ascending order (matching the
     * COBOL ascending key specification on lines 18-19).
     *
     * @param items sorted list of SearchItem
     * @param id    the itemId1 to search for
     * @return Optional containing the found item, or empty if not found
     */
    public Optional<SearchItem> searchById(List<SearchItem> items, int id) {
        int index = Collections.binarySearch(
                items,
                SearchItem.builder().itemId1(id).itemId2(0).build(),
                (a, b) -> Integer.compare(a.getItemId1(), b.getItemId1())
        );
        if (index >= 0) {
            return Optional.of(items.get(index));
        }
        return Optional.empty();
    }

    /**
     * Performs a binary search matching all three IDs.
     * Replaces SEARCH ALL with compound WHEN condition in search.cbl (lines 82-89).
     *
     * @param items sorted list of SearchItem
     * @param id1   itemId1 to match
     * @param id2   itemId2 to match
     * @param id3   itemId3 to match
     * @return Optional containing the found item, or empty if not found
     */
    public Optional<SearchItem> searchByAllIds(List<SearchItem> items, int id1, int id2, int id3) {
        return items.stream()
                .filter(item -> item.getItemId1() == id1
                        && item.getItemId2() == id2
                        && item.getItemId3() == id3)
                .findFirst();
    }

    /**
     * Sets up test data mirroring the setup-test-data paragraph in search.cbl (lines 127-156).
     *
     * @return list of SearchItem with test data
     */
    public List<SearchItem> setupTestData() {
        List<SearchItem> items = new ArrayList<>();

        items.add(SearchItem.builder()
                .itemId1(1).itemId2(101).itemId3(500)
                .itemName("test item 1")
                .itemDate(LocalDate.of(2021, 1, 1))
                .build());

        items.add(SearchItem.builder()
                .itemId1(2).itemId2(102).itemId3(499)
                .itemName("test item 2")
                .itemDate(LocalDate.of(2021, 2, 2))
                .build());

        items.add(SearchItem.builder()
                .itemId1(3).itemId2(103).itemId3(498)
                .itemName("test item 3")
                .itemDate(LocalDate.of(2021, 3, 3))
                .build());

        return items;
    }
}
