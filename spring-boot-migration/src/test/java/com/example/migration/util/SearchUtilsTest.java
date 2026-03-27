package com.example.migration.util;

import com.example.migration.model.SearchItem;
import org.junit.jupiter.api.Test;
import java.util.Comparator;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SearchUtils.
 * Verifies binary search behavior matches COBOL SEARCH ALL (search/search.cbl lines 61-66)
 * and sequential search matches COBOL SEARCH (search/search.cbl lines 100-109).
 */
class SearchUtilsTest {

    @Test
    void binarySearch_findsExistingItem() {
        List<SearchItem> items = List.of(
                new SearchItem(1, 101, 500, "test item 1", "2021/01/01"),
                new SearchItem(2, 102, 499, "test item 2", "2021/02/02"),
                new SearchItem(3, 103, 498, "test item 3", "2021/03/03")
        );

        SearchItem key = new SearchItem(2, 0, 0, null, null);
        int index = SearchUtils.binarySearch(items, key,
                Comparator.comparingInt(SearchItem::getId1));

        assertEquals(1, index);
        assertEquals("test item 2", items.get(index).getName());
    }

    @Test
    void binarySearch_returnsNegativeOneForMissingItem() {
        List<SearchItem> items = List.of(
                new SearchItem(1, 101, 500, "test item 1", "2021/01/01"),
                new SearchItem(2, 102, 499, "test item 2", "2021/02/02"),
                new SearchItem(3, 103, 498, "test item 3", "2021/03/03")
        );

        SearchItem key = new SearchItem(99, 0, 0, null, null);
        int index = SearchUtils.binarySearch(items, key,
                Comparator.comparingInt(SearchItem::getId1));

        assertEquals(-1, index);
    }

    @Test
    void binarySearch_findsFirstItem() {
        List<SearchItem> items = List.of(
                new SearchItem(1, 101, 500, "test item 1", "2021/01/01"),
                new SearchItem(2, 102, 499, "test item 2", "2021/02/02"),
                new SearchItem(3, 103, 498, "test item 3", "2021/03/03")
        );

        SearchItem key = new SearchItem(1, 0, 0, null, null);
        int index = SearchUtils.binarySearch(items, key,
                Comparator.comparingInt(SearchItem::getId1));

        assertEquals(0, index);
    }

    @Test
    void binarySearch_findsLastItem() {
        List<SearchItem> items = List.of(
                new SearchItem(1, 101, 500, "test item 1", "2021/01/01"),
                new SearchItem(2, 102, 499, "test item 2", "2021/02/02"),
                new SearchItem(3, 103, 498, "test item 3", "2021/03/03")
        );

        SearchItem key = new SearchItem(3, 0, 0, null, null);
        int index = SearchUtils.binarySearch(items, key,
                Comparator.comparingInt(SearchItem::getId1));

        assertEquals(2, index);
    }

    @Test
    void sequentialSearch_findsMatchingItem() {
        List<String> items = List.of("Value of id 2.", "Value of id 3.", "Value of id 1.");

        int index = SearchUtils.sequentialSearch(items, s -> s.contains("id 3"));

        assertEquals(1, index);
    }

    @Test
    void sequentialSearch_returnsNegativeOneForNoMatch() {
        List<String> items = List.of("Value of id 2.", "Value of id 3.", "Value of id 1.");

        int index = SearchUtils.sequentialSearch(items, s -> s.contains("id 99"));

        assertEquals(-1, index);
    }

    @Test
    void sequentialSearch_findsFirstMatch() {
        List<Integer> items = List.of(2, 3, 1);

        int index = SearchUtils.sequentialSearch(items, i -> i == 1);

        assertEquals(2, index);
    }
}
