package com.cobolmigration.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for SearchUtils verifying behavior matching search/search.cbl.
 */
class SearchUtilsTest {

    // Simple record for testing
    record TestItem(int id, String name) {}

    @Test
    void linearSearch_shouldFindMatchingItem() {
        List<TestItem> table = Arrays.asList(
                new TestItem(2, "Value of id 2."),
                new TestItem(3, "Value of id 3."),
                new TestItem(1, "Value of id 1.")
        );

        Optional<TestItem> result = SearchUtils.linearSearch(table, item -> item.id() == 3);

        assertTrue(result.isPresent());
        assertEquals("Value of id 3.", result.get().name());
    }

    @Test
    void linearSearch_shouldReturnEmptyForNoMatch() {
        List<TestItem> table = Arrays.asList(
                new TestItem(1, "a"), new TestItem(2, "b")
        );

        Optional<TestItem> result = SearchUtils.linearSearch(table, item -> item.id() == 999);
        assertFalse(result.isPresent());
    }

    @Test
    void binarySearch_shouldFindInSortedList() {
        List<TestItem> sortedTable = Arrays.asList(
                new TestItem(1, "test item 1"),
                new TestItem(2, "test item 2"),
                new TestItem(3, "test item 3")
        );
        Comparator<TestItem> byId = Comparator.comparingInt(TestItem::id);

        Optional<TestItem> result = SearchUtils.binarySearch(
                sortedTable, byId, new TestItem(2, ""));

        assertTrue(result.isPresent());
        assertEquals("test item 2", result.get().name());
    }

    @Test
    void binarySearch_shouldReturnEmptyForNoMatch() {
        List<TestItem> sortedTable = Arrays.asList(
                new TestItem(1, "a"), new TestItem(3, "c"), new TestItem(5, "e")
        );
        Comparator<TestItem> byId = Comparator.comparingInt(TestItem::id);

        Optional<TestItem> result = SearchUtils.binarySearch(
                sortedTable, byId, new TestItem(4, ""));
        assertFalse(result.isPresent());
    }

    @Test
    void linearSearchIndex_shouldReturnCorrectIndex() {
        List<TestItem> table = Arrays.asList(
                new TestItem(2, "a"), new TestItem(3, "b"), new TestItem(1, "c")
        );

        int index = SearchUtils.linearSearchIndex(table, item -> item.id() == 3);
        assertEquals(1, index);
    }

    @Test
    void linearSearchIndex_shouldReturnMinusOneForNoMatch() {
        List<TestItem> table = Arrays.asList(new TestItem(1, "a"));
        int index = SearchUtils.linearSearchIndex(table, item -> item.id() == 999);
        assertEquals(-1, index);
    }
}
