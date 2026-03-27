package com.example.cobolmigration.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for search operations matching search/search.cbl behavior.
 */
class SearchDemoServiceTest {

    private SearchDemoService service;

    @BeforeEach
    void setUp() {
        service = new SearchDemoService();
    }

    @Test
    void binarySearchById1_found() {
        // search.cbl: SEARCH ALL ws-item-table WHEN ws-item-id-1(idx) = 2
        Map<String, Object> result = service.binarySearchById1(2);
        assertTrue((boolean) result.get("found"));
        SearchDemoService.KeyedItem item = (SearchDemoService.KeyedItem) result.get("item");
        assertEquals(2, item.id1());
        assertEquals("test item 2", item.name());
    }

    @Test
    void binarySearchById1_notFound() {
        Map<String, Object> result = service.binarySearchById1(999);
        assertFalse((boolean) result.get("found"));
        assertEquals("Item not found.", result.get("message"));
    }

    @Test
    void sequentialSearchById_found() {
        // search.cbl: SEARCH ws-no-key-item-table WHEN ws-no-key-id(idx-2) = 1
        Map<String, Object> result = service.sequentialSearchById(1);
        assertTrue((boolean) result.get("found"));
        SearchDemoService.UnkeyedItem item = (SearchDemoService.UnkeyedItem) result.get("item");
        assertEquals(1, item.id());
        assertEquals("Value of id 1.", item.value());
    }

    @Test
    void sequentialSearchById_notFound() {
        Map<String, Object> result = service.sequentialSearchById(999);
        assertFalse((boolean) result.get("found"));
    }
}
