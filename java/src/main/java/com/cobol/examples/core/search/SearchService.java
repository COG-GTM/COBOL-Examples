package com.cobol.examples.core.search;

import java.util.List;
import java.util.Optional;

/**
 * Pure-logic port of {@code search/search.cbl}.
 *
 * <p>COBOL offered two table-search verbs: {@code SEARCH ALL} (binary search,
 * requiring an indexed and sorted key) and {@code SEARCH} (sequential, no key
 * needed). Both are reproduced here as static helpers operating on lists.
 */
public final class SearchService {

    /** A keyed table row, mirroring {@code ws-item-table}. */
    public record Item(int id1, int id2, int id3, String name, String date) {
    }

    private SearchService() {
    }

    /**
     * Equivalent of {@code SEARCH ALL ... WHEN ws-item-id-1 = key}. The input
     * must be sorted ascending on {@code id1}, just as the COBOL table required.
     */
    public static Optional<Item> binarySearchById1(List<Item> sortedById1, int key) {
        int low = 0;
        int high = sortedById1.size() - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            int cmp = Integer.compare(sortedById1.get(mid).id1(), key);
            if (cmp == 0) {
                return Optional.of(sortedById1.get(mid));
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return Optional.empty();
    }

    /** Equivalent of a sequential {@code SEARCH} over an unsorted table. */
    public static Optional<Item> linearSearchById1(List<Item> table, int key) {
        for (Item item : table) {
            if (item.id1() == key) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }
}
