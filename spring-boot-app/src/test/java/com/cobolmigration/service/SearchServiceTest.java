package com.cobolmigration.service;

import com.cobolmigration.service.SearchService.SearchableItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SearchService} validating binary search behavior
 * that matches the COBOL SEARCH ALL functionality from
 * {@code search/search.cbl}.
 */
class SearchServiceTest {

    private SearchService searchService;
    private List<SearchableItem> sortedItems;

    @BeforeEach
    void setUp() {
        searchService = new SearchService();

        // Mirrors the test data from search.cbl (lines 129-145):
        //   ws-item-id-1(1) = 0001, ws-item-name(1) = "test item 1"
        //   ws-item-id-1(2) = 0002, ws-item-name(2) = "test item 2"
        //   ws-item-id-1(3) = 0003, ws-item-name(3) = "test item 3"
        sortedItems = new ArrayList<>();
        sortedItems.add(new SearchableItem(1, "test item 1", "2021/01/01"));
        sortedItems.add(new SearchableItem(2, "test item 2", "2021/02/02"));
        sortedItems.add(new SearchableItem(3, "test item 3", "2021/03/03"));
    }

    @Test
    void binarySearchById_findsExistingItem() {
        Optional<SearchableItem> result = searchService.binarySearchById(sortedItems, 2);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(2);
        assertThat(result.get().getName()).isEqualTo("test item 2");
    }

    @Test
    void binarySearchById_findsFirstItem() {
        Optional<SearchableItem> result = searchService.binarySearchById(sortedItems, 1);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("test item 1");
    }

    @Test
    void binarySearchById_findsLastItem() {
        Optional<SearchableItem> result = searchService.binarySearchById(sortedItems, 3);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("test item 3");
    }

    @Test
    void binarySearchById_returnsEmptyForNonExistentItem() {
        // Equivalent to SEARCH ALL ... AT END DISPLAY "Item not found."
        Optional<SearchableItem> result = searchService.binarySearchById(sortedItems, 999);

        assertThat(result).isEmpty();
    }

    @Test
    void binarySearchById_emptyList_returnsEmpty() {
        Optional<SearchableItem> result = searchService.binarySearchById(List.of(), 1);

        assertThat(result).isEmpty();
    }

    @Test
    void sequentialSearchById_findsExistingItem() {
        // Mirrors the sequential SEARCH (without ALL) from search.cbl (lines 100-109)
        // Sequential search does not require sorted data
        List<SearchableItem> unsortedItems = new ArrayList<>();
        unsortedItems.add(new SearchableItem(2, "Value of id 2.", ""));
        unsortedItems.add(new SearchableItem(3, "Value of id 3.", ""));
        unsortedItems.add(new SearchableItem(1, "Value of id 1.", ""));

        Optional<SearchableItem> result = searchService.sequentialSearchById(unsortedItems, 3);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(3);
    }

    @Test
    void sequentialSearchById_returnsEmptyForNonExistentItem() {
        Optional<SearchableItem> result = searchService.sequentialSearchById(sortedItems, 999);

        assertThat(result).isEmpty();
    }

    @Test
    void sequentialSearchById_emptyList_returnsEmpty() {
        Optional<SearchableItem> result = searchService.sequentialSearchById(List.of(), 1);

        assertThat(result).isEmpty();
    }
}
