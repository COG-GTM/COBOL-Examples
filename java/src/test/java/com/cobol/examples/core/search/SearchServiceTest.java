package com.cobol.examples.core.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cobol.examples.core.search.SearchService.Item;
import java.util.List;
import org.junit.jupiter.api.Test;

class SearchServiceTest {

    private static final List<Item> TABLE = List.of(
            new Item(10, 100, 30, "alpha", "2021/01/10"),
            new Item(20, 200, 20, "bravo", "2021/02/20"),
            new Item(30, 300, 10, "charlie", "2021/03/30"));

    @Test
    void binarySearchFindsExistingKey() {
        assertEquals("bravo", SearchService.binarySearchById1(TABLE, 20).orElseThrow().name());
    }

    @Test
    void binarySearchMissesAbsentKey() {
        assertTrue(SearchService.binarySearchById1(TABLE, 25).isEmpty());
    }

    @Test
    void linearSearchFindsExistingKey() {
        assertEquals("charlie", SearchService.linearSearchById1(TABLE, 30).orElseThrow().name());
    }

    @Test
    void linearSearchMissesAbsentKey() {
        assertTrue(SearchService.linearSearchById1(TABLE, 99).isEmpty());
    }
}
