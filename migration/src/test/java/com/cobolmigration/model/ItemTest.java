package com.cobolmigration.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Item model.
 * Verifies Comparable implementation matches COBOL sort key order:
 *   ASCENDING KEY IS ws-item-id-1, ws-item-id-2
 *   DESCENDING KEY IS ws-item-id-3
 */
class ItemTest {

    @Test
    void testDefaultConstructor() {
        Item item = new Item();
        assertEquals(0, item.getId1());
        assertEquals(0, item.getId2());
        assertEquals(0, item.getId3());
        assertNull(item.getName());
        assertNull(item.getDate());
    }

    @Test
    void testParameterizedConstructor() {
        LocalDate date = LocalDate.of(2021, 1, 1);
        Item item = new Item(1, 101, 500, "test item 1", date);

        assertEquals(1, item.getId1());
        assertEquals(101, item.getId2());
        assertEquals(500, item.getId3());
        assertEquals("test item 1", item.getName());
        assertEquals(date, item.getDate());
    }

    @Test
    void testFormattedDate() {
        // COBOL date format: YYYY/MM/DD from ws-item-date composite
        Item item = new Item(1, 101, 500, "test", LocalDate.of(2021, 1, 1));
        assertEquals("2021/01/01", item.getFormattedDate());

        item.setDate(LocalDate.of(2021, 12, 25));
        assertEquals("2021/12/25", item.getFormattedDate());
    }

    @Test
    void testFormattedDateNull() {
        Item item = new Item();
        assertEquals("", item.getFormattedDate());
    }

    @Test
    void testSetDateFromString() {
        Item item = new Item();
        item.setDateFromString("2021/01/01");
        assertEquals(LocalDate.of(2021, 1, 1), item.getDate());

        item.setDateFromString("2021/02/02");
        assertEquals(LocalDate.of(2021, 2, 2), item.getDate());
    }

    @Test
    void testSetDateFromStringNull() {
        Item item = new Item();
        item.setDateFromString(null);
        assertNull(item.getDate());

        item.setDateFromString("");
        assertNull(item.getDate());
    }

    @Test
    void testComparableAscendingId1() {
        // ASCENDING KEY IS ws-item-id-1
        Item item1 = new Item(1, 0, 0, "a", null);
        Item item2 = new Item(2, 0, 0, "b", null);
        Item item3 = new Item(3, 0, 0, "c", null);

        assertTrue(item1.compareTo(item2) < 0);
        assertTrue(item2.compareTo(item3) < 0);
        assertTrue(item3.compareTo(item1) > 0);
    }

    @Test
    void testComparableAscendingId2() {
        // When id1 is equal, compare by id2 ASCENDING
        Item item1 = new Item(1, 100, 0, "a", null);
        Item item2 = new Item(1, 200, 0, "b", null);

        assertTrue(item1.compareTo(item2) < 0);
        assertTrue(item2.compareTo(item1) > 0);
    }

    @Test
    void testComparableDescendingId3() {
        // When id1 and id2 are equal, compare by id3 DESCENDING
        Item item1 = new Item(1, 100, 500, "a", null);
        Item item2 = new Item(1, 100, 400, "b", null);

        // Descending: higher id3 should come first
        assertTrue(item1.compareTo(item2) < 0);
        assertTrue(item2.compareTo(item1) > 0);
    }

    @Test
    void testSortingMatchesCobolTestData() {
        // Test data from search.cbl lines 129-145
        Item item1 = new Item(1, 101, 500, "test item 1", LocalDate.of(2021, 1, 1));
        Item item2 = new Item(2, 102, 499, "test item 2", LocalDate.of(2021, 2, 2));
        Item item3 = new Item(3, 103, 498, "test item 3", LocalDate.of(2021, 3, 3));

        // Already in correct COBOL sort order
        List<Item> items = new ArrayList<>();
        items.add(item3);
        items.add(item1);
        items.add(item2);

        Collections.sort(items);

        assertEquals(1, items.get(0).getId1());
        assertEquals(2, items.get(1).getId1());
        assertEquals(3, items.get(2).getId1());
    }

    @Test
    void testSortingWithSameId1DifferentId2() {
        Item itemA = new Item(1, 200, 500, "a", null);
        Item itemB = new Item(1, 100, 500, "b", null);
        Item itemC = new Item(1, 300, 500, "c", null);

        List<Item> items = new ArrayList<>();
        items.add(itemA);
        items.add(itemB);
        items.add(itemC);

        Collections.sort(items);

        assertEquals(100, items.get(0).getId2());
        assertEquals(200, items.get(1).getId2());
        assertEquals(300, items.get(2).getId2());
    }

    @Test
    void testSortingWithSameId1Id2DifferentId3Descending() {
        Item itemA = new Item(1, 100, 300, "a", null);
        Item itemB = new Item(1, 100, 500, "b", null);
        Item itemC = new Item(1, 100, 100, "c", null);

        List<Item> items = new ArrayList<>();
        items.add(itemA);
        items.add(itemB);
        items.add(itemC);

        Collections.sort(items);

        // Descending by id3: 500 first, then 300, then 100
        assertEquals(500, items.get(0).getId3());
        assertEquals(300, items.get(1).getId3());
        assertEquals(100, items.get(2).getId3());
    }

    @Test
    void testEquality() {
        Item item1 = new Item(1, 101, 500, "test item 1", LocalDate.of(2021, 1, 1));
        Item item2 = new Item(1, 101, 500, "different name", LocalDate.of(2022, 6, 15));

        // Equality based on id1, id2, id3
        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    void testInequality() {
        Item item1 = new Item(1, 101, 500, "test", null);
        Item item2 = new Item(2, 101, 500, "test", null);

        assertNotEquals(item1, item2);
    }

    @Test
    void testToString() {
        Item item = new Item(1, 101, 500, "test item 1", LocalDate.of(2021, 1, 1));
        String str = item.toString();
        assertTrue(str.contains("id1=1"));
        assertTrue(str.contains("test item 1"));
        assertTrue(str.contains("2021/01/01"));
    }
}
