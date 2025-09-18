package com.cobol.examples.search;

import com.cobol.examples.model.TableItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BinarySearchTableTest {
    private BinarySearchTable table;
    
    @BeforeEach
    void setUp() {
        table = new BinarySearchTable();
        table.addItem(new TableItem(1, 101, 500, "test item 1", LocalDate.of(2021, 1, 1)));
        table.addItem(new TableItem(2, 102, 499, "test item 2", LocalDate.of(2021, 2, 2)));
        table.addItem(new TableItem(3, 103, 498, "test item 3", LocalDate.of(2021, 3, 3)));
    }
    
    @Test
    void testSearchByItemId1Found() {
        TableItem result = table.searchByItemId1(2);
        assertNotNull(result);
        assertEquals(2, result.getItemId1());
        assertEquals(102, result.getItemId2());
        assertEquals("test item 2", result.getItemName());
    }
    
    @Test
    void testSearchByItemId1NotFound() {
        TableItem result = table.searchByItemId1(999);
        assertNull(result);
    }
    
    @Test
    void testSearchByAllKeysFound() {
        TableItem result = table.searchByAllKeys(1, 101, 500);
        assertNotNull(result);
        assertEquals(1, result.getItemId1());
        assertEquals(101, result.getItemId2());
        assertEquals(500, result.getItemId3());
        assertEquals("test item 1", result.getItemName());
    }
    
    @Test
    void testSearchByAllKeysNotFound() {
        TableItem result = table.searchByAllKeys(1, 101, 999);
        assertNull(result);
    }
    
    @Test
    void testSearchByItemId3Descending() {
        TableItem result = table.searchByItemId3Descending(499);
        assertNotNull(result);
        assertEquals(2, result.getItemId1());
        assertEquals(499, result.getItemId3());
    }
    
    @Test
    void testTableSize() {
        assertEquals(3, table.size());
        assertFalse(table.isEmpty());
    }
    
    @Test
    void testClearTable() {
        table.clear();
        assertEquals(0, table.size());
        assertTrue(table.isEmpty());
    }
}
