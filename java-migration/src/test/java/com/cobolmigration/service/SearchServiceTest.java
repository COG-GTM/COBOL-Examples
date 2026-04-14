package com.cobolmigration.service;

import com.cobolmigration.model.ItemRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for SearchService - validates migration of search/search.cbl.
 */
class SearchServiceTest {

    private SearchService searchService;
    private List<ItemRecord> sortedTable;
    private List<ItemRecord> unsortedTable;

    @BeforeEach
    void setUp() {
        searchService = new SearchService();

        // Sorted table matching COBOL test data (search.cbl lines 129-145)
        sortedTable = new ArrayList<>();
        sortedTable.add(new ItemRecord(1, 101, 500, "test item 1", LocalDate.of(2021, 1, 1)));
        sortedTable.add(new ItemRecord(2, 102, 499, "test item 2", LocalDate.of(2021, 2, 2)));
        sortedTable.add(new ItemRecord(3, 103, 498, "test item 3", LocalDate.of(2021, 3, 3)));

        // Unsorted table matching COBOL test data (search.cbl lines 147-154)
        unsortedTable = new ArrayList<>();
        unsortedTable.add(new ItemRecord(2, 0, 0, "Value of id 2.", null));
        unsortedTable.add(new ItemRecord(3, 0, 0, "Value of id 3.", null));
        unsortedTable.add(new ItemRecord(1, 0, 0, "Value of id 1.", null));
    }

    @Test
    void binarySearch_findsExistingItem() {
        Optional<ItemRecord> result = searchService.binarySearch(sortedTable, 2);
        assertTrue(result.isPresent());
        assertEquals("test item 2", result.get().getItemName());
    }

    @Test
    void binarySearch_returnsEmptyForMissingItem() {
        Optional<ItemRecord> result = searchService.binarySearch(sortedTable, 999);
        assertFalse(result.isPresent());
    }

    @Test
    void binarySearch_emptyTable() {
        Optional<ItemRecord> result = searchService.binarySearch(List.of(), 1);
        assertFalse(result.isPresent());
    }

    @Test
    void binarySearch_nullTable() {
        Optional<ItemRecord> result = searchService.binarySearch(null, 1);
        assertFalse(result.isPresent());
    }

    @Test
    void binarySearchMultiKey_findsWithAllKeysMatching() {
        Optional<ItemRecord> result = searchService.binarySearchMultiKey(sortedTable, 2, 102, 499);
        assertTrue(result.isPresent());
        assertEquals("test item 2", result.get().getItemName());
    }

    @Test
    void binarySearchMultiKey_failsWhenOneKeyDoesNotMatch() {
        Optional<ItemRecord> result = searchService.binarySearchMultiKey(sortedTable, 2, 999, 499);
        assertFalse(result.isPresent());
    }

    @Test
    void linearSearch_findsInUnsortedTable() {
        Optional<ItemRecord> result = searchService.linearSearch(
                unsortedTable, item -> item.getItemId1() == 1);
        assertTrue(result.isPresent());
        assertEquals("Value of id 1.", result.get().getItemName());
    }

    @Test
    void linearSearch_returnsEmptyForMissingItem() {
        Optional<ItemRecord> result = searchService.linearSearch(
                unsortedTable, item -> item.getItemId1() == 999);
        assertFalse(result.isPresent());
    }

    @Test
    void linearSearch_emptyTable() {
        Optional<ItemRecord> result = searchService.linearSearch(
                List.of(), item -> item.getItemId1() == 1);
        assertFalse(result.isPresent());
    }

    @Test
    void linearSearch_nullTable() {
        Optional<ItemRecord> result = searchService.linearSearch(
                null, item -> item.getItemId1() == 1);
        assertFalse(result.isPresent());
    }
}
