package com.cobol.examples.search;

import com.cobol.examples.model.NoKeyItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SequentialSearchTableTest {
    private SequentialSearchTable table;
    
    @BeforeEach
    void setUp() {
        table = new SequentialSearchTable();
        table.addItem(new NoKeyItem(2, "Value of id 2."));
        table.addItem(new NoKeyItem(3, "Value of id 3."));
        table.addItem(new NoKeyItem(1, "Value of id 1."));
    }
    
    @Test
    void testSearchByIdFound() {
        NoKeyItem result = table.searchById(2);
        assertNotNull(result);
        assertEquals(2, result.getId());
        assertEquals("Value of id 2.", result.getValue());
    }
    
    @Test
    void testSearchByIdNotFound() {
        NoKeyItem result = table.searchById(999);
        assertNull(result);
    }
    
    @Test
    void testSearchByValueFound() {
        NoKeyItem result = table.searchByValue("Value of id 3.");
        assertNotNull(result);
        assertEquals(3, result.getId());
        assertEquals("Value of id 3.", result.getValue());
    }
    
    @Test
    void testSearchByValueNotFound() {
        NoKeyItem result = table.searchByValue("Non-existent value");
        assertNull(result);
    }
    
    @Test
    void testSearchByValueContains() {
        List<NoKeyItem> results = table.searchByValueContains("id 1");
        assertEquals(1, results.size());
        assertEquals(1, results.get(0).getId());
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
