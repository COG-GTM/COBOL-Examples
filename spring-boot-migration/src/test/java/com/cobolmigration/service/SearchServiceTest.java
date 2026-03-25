package com.cobolmigration.service;

import com.cobolmigration.model.SearchItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for SearchService.
 * Uses the exact test data from search.cbl (lines 127-156) to verify
 * binary search behavior.
 */
class SearchServiceTest {

    private SearchService searchService;
    private List<SearchItem> testData;

    @BeforeEach
    void setUp() {
        searchService = new SearchService();
        testData = searchService.setupTestData();
    }

    @Test
    void setupTestData_createsCorrectItems() {
        // Verify test data matches search.cbl lines 127-156
        assertEquals(3, testData.size());

        // Item 1: id1=0001, id2=0101, id3=0500, name="test item 1", date=2021/01/01
        SearchItem item1 = testData.get(0);
        assertEquals(1, item1.getItemId1());
        assertEquals(101, item1.getItemId2());
        assertEquals(500, item1.getItemId3());
        assertEquals("test item 1", item1.getItemName());
        assertEquals(LocalDate.of(2021, 1, 1), item1.getItemDate());

        // Item 2: id1=0002, id2=0102, id3=0499, name="test item 2", date=2021/02/02
        SearchItem item2 = testData.get(1);
        assertEquals(2, item2.getItemId1());
        assertEquals(102, item2.getItemId2());
        assertEquals(499, item2.getItemId3());
        assertEquals("test item 2", item2.getItemName());
        assertEquals(LocalDate.of(2021, 2, 2), item2.getItemDate());

        // Item 3: id1=0003, id2=0103, id3=0498, name="test item 3", date=2021/03/03
        SearchItem item3 = testData.get(2);
        assertEquals(3, item3.getItemId1());
        assertEquals(103, item3.getItemId2());
        assertEquals(498, item3.getItemId3());
        assertEquals("test item 3", item3.getItemName());
        assertEquals(LocalDate.of(2021, 3, 3), item3.getItemDate());
    }

    @Test
    void searchById_findsExistingItem() {
        Optional<SearchItem> result = searchService.searchById(testData, 2);

        assertTrue(result.isPresent());
        assertEquals(2, result.get().getItemId1());
        assertEquals("test item 2", result.get().getItemName());
    }

    @Test
    void searchById_findsFirstItem() {
        Optional<SearchItem> result = searchService.searchById(testData, 1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getItemId1());
    }

    @Test
    void searchById_findsLastItem() {
        Optional<SearchItem> result = searchService.searchById(testData, 3);

        assertTrue(result.isPresent());
        assertEquals(3, result.get().getItemId1());
    }

    @Test
    void searchById_returnsEmptyForNonExistent() {
        // Matches "at end" display "Item not found." in search.cbl line 63
        Optional<SearchItem> result = searchService.searchById(testData, 999);

        assertFalse(result.isPresent());
    }

    @Test
    void searchByAllIds_findsMatchingItem() {
        // Matches compound WHEN in search.cbl lines 85-88
        Optional<SearchItem> result = searchService.searchByAllIds(testData, 2, 102, 499);

        assertTrue(result.isPresent());
        assertEquals("test item 2", result.get().getItemName());
    }

    @Test
    void searchByAllIds_returnsEmptyWhenPartialMatch() {
        // id1 matches but id2 and id3 don't
        Optional<SearchItem> result = searchService.searchByAllIds(testData, 1, 999, 999);

        assertFalse(result.isPresent());
    }

    @Test
    void searchByAllIds_returnsEmptyForNoMatch() {
        Optional<SearchItem> result = searchService.searchByAllIds(testData, 999, 999, 999);

        assertFalse(result.isPresent());
    }
}
