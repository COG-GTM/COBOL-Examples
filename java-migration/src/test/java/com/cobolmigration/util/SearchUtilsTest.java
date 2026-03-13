package com.cobolmigration.util;

import com.cobolmigration.util.SearchUtils.SearchableItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SearchUtils.
 * Verifies linear and binary search behavior matches COBOL SEARCH and SEARCH ALL.
 */
class SearchUtilsTest {

    private List<SearchableItem> sortedItems;
    private List<SimpleItem> unsortedItems;

    record SimpleItem(int id, String value) {
    }

    @BeforeEach
    void setUp() {
        // Keyed table matching search/search.cbl test data
        // Ascending key: id1, id2; Descending key: id3
        sortedItems = List.of(
                new SearchableItem(1, 101, 500, "test item 1", "2021/01/01"),
                new SearchableItem(2, 102, 499, "test item 2", "2021/02/02"),
                new SearchableItem(3, 103, 498, "test item 3", "2021/03/03")
        );

        // Non-keyed table (not sorted)
        unsortedItems = List.of(
                new SimpleItem(2, "Value of id 2."),
                new SimpleItem(3, "Value of id 3."),
                new SimpleItem(1, "Value of id 1.")
        );
    }

    // --- SEARCH ALL (binary search) tests ---

    @Test
    void binarySearch_shouldFindExistingItem() {
        // COBOL: SEARCH ALL ws-item-table WHEN ws-item-id-1(idx) = 2
        Optional<SearchableItem> result = SearchUtils.binarySearch(
                sortedItems, SearchableItem::id1, 2);

        assertTrue(result.isPresent());
        assertEquals("test item 2", result.get().name());
    }

    @Test
    void binarySearch_shouldReturnEmptyForMissingItem() {
        // COBOL: AT END DISPLAY "Item not found."
        Optional<SearchableItem> result = SearchUtils.binarySearch(
                sortedItems, SearchableItem::id1, 999);

        assertFalse(result.isPresent());
    }

    @Test
    void binarySearchMultiKey_shouldMatchAllKeys() {
        // COBOL: WHEN ws-item-id-1(idx) = 2 AND ws-item-id-2(idx) = 102 AND ws-item-id-3(idx) = 499
        Optional<SearchableItem> result = SearchUtils.binarySearchMultiKey(
                sortedItems,
                item -> item.id1() == 2 && item.id2() == 102 && item.id3() == 499);

        assertTrue(result.isPresent());
        assertEquals("test item 2", result.get().name());
    }

    @Test
    void binarySearchMultiKey_shouldReturnEmptyForPartialMatch() {
        // All three keys must match
        Optional<SearchableItem> result = SearchUtils.binarySearchMultiKey(
                sortedItems,
                item -> item.id1() == 2 && item.id2() == 102 && item.id3() == 500);

        assertFalse(result.isPresent());
    }

    // --- SEARCH (sequential/linear search) tests ---

    @Test
    void linearSearch_shouldFindInUnsortedList() {
        // COBOL: SEARCH ws-no-key-item-table WHEN ws-no-key-id(idx) = 1
        Optional<SimpleItem> result = SearchUtils.linearSearch(
                unsortedItems, item -> item.id() == 1);

        assertTrue(result.isPresent());
        assertEquals("Value of id 1.", result.get().value());
    }

    @Test
    void linearSearch_shouldReturnFirstMatch() {
        // Linear search returns the first matching element
        List<SimpleItem> items = List.of(
                new SimpleItem(1, "first"),
                new SimpleItem(1, "second"));

        Optional<SimpleItem> result = SearchUtils.linearSearch(
                items, item -> item.id() == 1);

        assertTrue(result.isPresent());
        assertEquals("first", result.get().value());
    }

    @Test
    void linearSearch_shouldReturnEmptyForNoMatch() {
        // COBOL: AT END DISPLAY "Item not found."
        Optional<SimpleItem> result = SearchUtils.linearSearch(
                unsortedItems, item -> item.id() == 999);

        assertFalse(result.isPresent());
    }

    @Test
    void linearSearch_shouldWorkOnEmptyList() {
        Optional<SimpleItem> result = SearchUtils.linearSearch(
                List.of(), item -> item.id() == 1);

        assertFalse(result.isPresent());
    }
}
