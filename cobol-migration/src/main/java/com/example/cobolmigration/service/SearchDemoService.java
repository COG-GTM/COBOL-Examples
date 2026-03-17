package com.example.cobolmigration.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Replaces search/search.cbl — demonstrates binary search (SEARCH ALL) and
 * sequential search (SEARCH) on in-memory tables.
 *
 * COBOL context:
 *   - SEARCH ALL (line 61): binary search on a sorted, keyed table (ascending ws-item-id-1)
 *   - SEARCH (line 100): sequential/linear search on an unkeyed table (ws-no-key-item-table)
 *
 * Java equivalents:
 *   - Collections.binarySearch() for the keyed table
 *   - Stream.filter() for the sequential search
 */
@Service
public class SearchDemoService {

    /**
     * Represents a keyed item from the COBOL ws-item-table (search.cbl lines 17-32).
     */
    public record KeyedItem(int id1, int id2, int id3, String name, String date) {}

    /**
     * Represents an unkeyed item from the COBOL ws-no-key-item-table (search.cbl lines 37-39).
     */
    public record UnkeyedItem(int id, String value) {}

    /**
     * Sets up the test data matching search.cbl setup-test-data paragraph (lines 127-156).
     */
    public Map<String, Object> getTestData() {
        List<KeyedItem> keyedItems = new ArrayList<>();
        keyedItems.add(new KeyedItem(1, 101, 500, "test item 1", "2021/01/01"));
        keyedItems.add(new KeyedItem(2, 102, 499, "test item 2", "2021/02/02"));
        keyedItems.add(new KeyedItem(3, 103, 498, "test item 3", "2021/03/03"));

        List<UnkeyedItem> unkeyedItems = new ArrayList<>();
        unkeyedItems.add(new UnkeyedItem(2, "Value of id 2."));
        unkeyedItems.add(new UnkeyedItem(3, "Value of id 3."));
        unkeyedItems.add(new UnkeyedItem(1, "Value of id 1."));

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("keyedItems", keyedItems);
        data.put("unkeyedItems", unkeyedItems);
        return data;
    }

    /**
     * Binary search by id1 — replaces SEARCH ALL ws-item-table (search.cbl lines 61-66).
     * The keyed table must be sorted ascending by id1 for binary search.
     *
     * Uses Collections.binarySearch() as the Java equivalent of COBOL's SEARCH ALL.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> binarySearchById1(int searchId) {
        Map<String, Object> testData = getTestData();
        List<KeyedItem> items = (List<KeyedItem>) testData.get("keyedItems");

        // Items are already sorted by id1 (ascending) — required for binary search
        int index = Collections.binarySearch(
            items,
            new KeyedItem(searchId, 0, 0, "", ""),
            Comparator.comparingInt(KeyedItem::id1)
        );

        Map<String, Object> result = new LinkedHashMap<>();
        if (index >= 0) {
            KeyedItem found = items.get(index);
            result.put("found", true);
            result.put("item", found);
            result.put("searchMethod", "binary (SEARCH ALL equivalent)");
        } else {
            result.put("found", false);
            result.put("message", "Item not found.");
            result.put("searchMethod", "binary (SEARCH ALL equivalent)");
        }
        return result;
    }

    /**
     * Sequential search by id — replaces SEARCH ws-no-key-item-table (search.cbl lines 100-109).
     * Sequential search does not require sorted data.
     *
     * Uses Stream.filter() as the Java equivalent of COBOL's SEARCH (sequential).
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> sequentialSearchById(int searchId) {
        Map<String, Object> testData = getTestData();
        List<UnkeyedItem> items = (List<UnkeyedItem>) testData.get("unkeyedItems");

        Optional<UnkeyedItem> found = items.stream()
            .filter(item -> item.id() == searchId)
            .findFirst();

        Map<String, Object> result = new LinkedHashMap<>();
        if (found.isPresent()) {
            result.put("found", true);
            result.put("item", found.get());
            result.put("searchMethod", "sequential (SEARCH equivalent)");
        } else {
            result.put("found", false);
            result.put("message", "Item not found.");
            result.put("searchMethod", "sequential (SEARCH equivalent)");
        }
        return result;
    }
}
