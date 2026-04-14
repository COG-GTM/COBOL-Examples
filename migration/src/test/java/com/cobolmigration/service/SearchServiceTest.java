package com.cobolmigration.service;

import com.cobolmigration.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SearchService.
 * Verifies binary and linear search produce same results as COBOL
 * SEARCH/SEARCH ALL from search/search.cbl.
 */
class SearchServiceTest {

    private SearchService searchService;
    private List<Item> sortedItems;
    private List<Item> unsortedItems;

    @BeforeEach
    void setUp() {
        searchService = new SearchService();

        // Set up test data matching search.cbl lines 129-145
        sortedItems = new ArrayList<>();
        sortedItems.add(new Item(1, 101, 500, "test item 1", LocalDate.of(2021, 1, 1)));
        sortedItems.add(new Item(2, 102, 499, "test item 2", LocalDate.of(2021, 2, 2)));
        sortedItems.add(new Item(3, 103, 498, "test item 3", LocalDate.of(2021, 3, 3)));

        // Ensure sorted by natural ordering
        Collections.sort(sortedItems);

        // Set up unsorted test data matching search.cbl lines 147-154
        // ws-no-key-item-table: items NOT in id order
        unsortedItems = new ArrayList<>();
        unsortedItems.add(new Item(2, 0, 0, "Value of id 2.", null));
        unsortedItems.add(new Item(3, 0, 0, "Value of id 3.", null));
        unsortedItems.add(new Item(1, 0, 0, "Value of id 1.", null));
    }

    // ===== BINARY SEARCH TESTS (SEARCH ALL) =====

    @Test
    void testBinarySearchFound() {
        // COBOL: SEARCH ALL ws-item-table WHEN ws-item-id-1(idx) = 1
        Optional<Item> result = searchService.binarySearch(sortedItems, 1);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId1());
        assertEquals("test item 1", result.get().getName());
    }

    @Test
    void testBinarySearchFoundMiddle() {
        // Search for middle element
        Optional<Item> result = searchService.binarySearch(sortedItems, 2);
        assertTrue(result.isPresent());
        assertEquals(2, result.get().getId1());
        assertEquals("test item 2", result.get().getName());
    }

    @Test
    void testBinarySearchFoundLast() {
        // Search for last element
        Optional<Item> result = searchService.binarySearch(sortedItems, 3);
        assertTrue(result.isPresent());
        assertEquals(3, result.get().getId1());
        assertEquals("test item 3", result.get().getName());
    }

    @Test
    void testBinarySearchNotFound() {
        // COBOL: SEARCH ALL ... AT END DISPLAY "Item not found."
        Optional<Item> result = searchService.binarySearch(sortedItems, 999);
        assertFalse(result.isPresent());
    }

    @Test
    void testBinarySearchEmptyList() {
        Optional<Item> result = searchService.binarySearch(new ArrayList<>(), 1);
        assertFalse(result.isPresent());
    }

    @Test
    void testBinarySearchNullList() {
        Optional<Item> result = searchService.binarySearch(null, 1);
        assertFalse(result.isPresent());
    }

    // ===== BINARY SEARCH ALL KEYS =====

    @Test
    void testBinarySearchAllKeysFound() {
        // COBOL: SEARCH ALL ... WHEN ws-item-id-1 = X AND ws-item-id-2 = Y AND ws-item-id-3 = Z
        Optional<Item> result = searchService.binarySearchAllKeys(sortedItems, 1, 101, 500);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId1());
        assertEquals(101, result.get().getId2());
        assertEquals(500, result.get().getId3());
    }

    @Test
    void testBinarySearchAllKeysNotFoundPartialMatch() {
        // id1 matches but id2 doesn't
        Optional<Item> result = searchService.binarySearchAllKeys(sortedItems, 1, 999, 500);
        assertFalse(result.isPresent());
    }

    @Test
    void testBinarySearchAllKeysNotFound() {
        Optional<Item> result = searchService.binarySearchAllKeys(sortedItems, 999, 999, 999);
        assertFalse(result.isPresent());
    }

    @Test
    void testBinarySearchAllKeysEmptyList() {
        Optional<Item> result = searchService.binarySearchAllKeys(new ArrayList<>(), 1, 101, 500);
        assertFalse(result.isPresent());
    }

    @Test
    void testBinarySearchAllKeysNullList() {
        Optional<Item> result = searchService.binarySearchAllKeys(null, 1, 101, 500);
        assertFalse(result.isPresent());
    }

    // ===== LINEAR SEARCH TESTS (SEARCH) =====

    @Test
    void testLinearSearchFound() {
        // COBOL: SEARCH ws-no-key-item-table WHEN ws-no-key-id(idx-2) = 2
        Optional<Item> result = searchService.linearSearch(unsortedItems,
                item -> item.getId1() == 2);
        assertTrue(result.isPresent());
        assertEquals(2, result.get().getId1());
        assertEquals("Value of id 2.", result.get().getName());
    }

    @Test
    void testLinearSearchFoundUnsorted() {
        // Search for id=1 which is at the END of the unsorted list
        Optional<Item> result = searchService.linearSearch(unsortedItems,
                item -> item.getId1() == 1);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId1());
        assertEquals("Value of id 1.", result.get().getName());
    }

    @Test
    void testLinearSearchNotFound() {
        // COBOL: SEARCH ... AT END DISPLAY "Item not found."
        Optional<Item> result = searchService.linearSearch(unsortedItems,
                item -> item.getId1() == 999);
        assertFalse(result.isPresent());
    }

    @Test
    void testLinearSearchEmptyList() {
        Optional<Item> result = searchService.linearSearch(new ArrayList<>(),
                item -> item.getId1() == 1);
        assertFalse(result.isPresent());
    }

    @Test
    void testLinearSearchNullList() {
        Optional<Item> result = searchService.linearSearch(null,
                item -> item.getId1() == 1);
        assertFalse(result.isPresent());
    }

    @Test
    void testLinearSearchByName() {
        // Search by name using a predicate
        Optional<Item> result = searchService.linearSearch(unsortedItems,
                item -> "Value of id 3.".equals(item.getName()));
        assertTrue(result.isPresent());
        assertEquals(3, result.get().getId1());
    }

    @Test
    void testLinearSearchReturnsFirst() {
        // Add duplicate id items - linear search should return the first match
        List<Item> items = new ArrayList<>();
        items.add(new Item(1, 0, 0, "First", null));
        items.add(new Item(1, 0, 0, "Second", null));

        Optional<Item> result = searchService.linearSearch(items,
                item -> item.getId1() == 1);
        assertTrue(result.isPresent());
        assertEquals("First", result.get().getName());
    }
}
