package com.example.cobolmigration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for SearchService.
 * Tests linear and binary search with found and not-found cases,
 * matching the search/search.cbl behavior.
 */
class SearchServiceTest {

    private SearchService service;

    @BeforeEach
    void setUp() {
        service = new SearchService();
    }

    // --- linearSearch tests (maps SEARCH verb) ---

    @Test
    void linearSearch_found() {
        List<String> items = List.of("apple", "banana", "cherry");
        Optional<String> result = service.linearSearch(items, s -> s.equals("banana"));
        assertTrue(result.isPresent());
        assertEquals("banana", result.get());
    }

    @Test
    void linearSearch_notFound() {
        // Maps AT END clause — item not in table
        List<String> items = List.of("apple", "banana", "cherry");
        Optional<String> result = service.linearSearch(items, s -> s.equals("grape"));
        assertFalse(result.isPresent());
    }

    @Test
    void linearSearch_firstMatch() {
        List<Integer> items = List.of(10, 20, 30, 20);
        Optional<Integer> result = service.linearSearch(items, i -> i == 20);
        assertTrue(result.isPresent());
        assertEquals(20, result.get());
    }

    @Test
    void linearSearch_emptyList() {
        Optional<String> result = service.linearSearch(List.of(), s -> s.equals("test"));
        assertFalse(result.isPresent());
    }

    @Test
    void linearSearch_nullList() {
        Optional<String> result = service.linearSearch(null, s -> s.equals("test"));
        assertFalse(result.isPresent());
    }

    @Test
    void linearSearch_withIntegerIds() {
        // Simulates the unsorted ws-no-key-item-table from search.cbl
        record Item(int id, String value) {}
        List<Item> items = List.of(
                new Item(2, "Value of id 2."),
                new Item(3, "Value of id 3."),
                new Item(1, "Value of id 1.")
        );
        Optional<Item> result = service.linearSearch(items, item -> item.id() == 1);
        assertTrue(result.isPresent());
        assertEquals("Value of id 1.", result.get().value());
    }

    // --- binarySearch tests (maps SEARCH ALL verb) ---

    @Test
    void binarySearch_found() {
        // Sorted list like ws-item-table in search.cbl (ascending key)
        List<Integer> sorted = List.of(1, 2, 3, 4, 5);
        Optional<Integer> result = service.binarySearch(sorted, 3, Comparator.naturalOrder());
        assertTrue(result.isPresent());
        assertEquals(3, result.get());
    }

    @Test
    void binarySearch_notFound() {
        // Maps AT END clause
        List<Integer> sorted = List.of(1, 2, 3, 4, 5);
        Optional<Integer> result = service.binarySearch(sorted, 6, Comparator.naturalOrder());
        assertFalse(result.isPresent());
    }

    @Test
    void binarySearch_emptyList() {
        Optional<Integer> result = service.binarySearch(List.of(), 1, Comparator.naturalOrder());
        assertFalse(result.isPresent());
    }

    @Test
    void binarySearch_nullList() {
        Optional<Integer> result = service.binarySearch(null, 1, Comparator.naturalOrder());
        assertFalse(result.isPresent());
    }

    @Test
    void binarySearch_singleElement_found() {
        List<Integer> sorted = List.of(42);
        Optional<Integer> result = service.binarySearch(sorted, 42, Comparator.naturalOrder());
        assertTrue(result.isPresent());
        assertEquals(42, result.get());
    }

    @Test
    void binarySearch_singleElement_notFound() {
        List<Integer> sorted = List.of(42);
        Optional<Integer> result = service.binarySearch(sorted, 99, Comparator.naturalOrder());
        assertFalse(result.isPresent());
    }

    @Test
    void binarySearch_withCustomComparator() {
        // Simulates searching by ws-item-id-1 ascending key
        record SearchItem(int id, String name) {}
        List<SearchItem> sorted = List.of(
                new SearchItem(1, "test item 1"),
                new SearchItem(2, "test item 2"),
                new SearchItem(3, "test item 3")
        );
        SearchItem key = new SearchItem(2, "");
        Comparator<SearchItem> comp = Comparator.comparingInt(SearchItem::id);
        Optional<SearchItem> result = service.binarySearch(sorted, key, comp);
        assertTrue(result.isPresent());
        assertEquals("test item 2", result.get().name());
    }
}
