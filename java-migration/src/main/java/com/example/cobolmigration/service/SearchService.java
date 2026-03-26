package com.example.cobolmigration.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import org.springframework.stereotype.Service;

/**
 * Spring Service mapping search/search.cbl.
 * Provides linear search (SEARCH verb) and binary search (SEARCH ALL verb).
 */
@Service
public class SearchService {

    /**
     * Sequential/linear search through a list.
     * Maps the COBOL {@code SEARCH} verb which does sequential lookup.
     * Returns {@link Optional#empty()} when not found (mapping the AT END clause).
     *
     * @param items   the list to search
     * @param matcher predicate to test each item
     * @param <T>     element type
     * @return the first matching item, or empty
     */
    public <T> Optional<T> linearSearch(List<T> items, Predicate<T> matcher) {
        if (items == null) {
            return Optional.empty();
        }
        return items.stream()
                .filter(matcher)
                .findFirst();
    }

    /**
     * Binary search on a pre-sorted list.
     * Maps the COBOL {@code SEARCH ALL} verb which requires the table to be sorted
     * by the ascending/descending key.
     * Returns {@link Optional#empty()} when not found (mapping the AT END clause).
     *
     * @param sortedItems pre-sorted list
     * @param key         the item to search for (used as comparison target)
     * @param comparator  comparator consistent with the sort order
     * @param <T>         element type
     * @return the found item, or empty
     */
    public <T> Optional<T> binarySearch(List<T> sortedItems, T key, Comparator<T> comparator) {
        if (sortedItems == null || sortedItems.isEmpty()) {
            return Optional.empty();
        }
        int index = Collections.binarySearch(sortedItems, key, comparator);
        if (index >= 0) {
            return Optional.of(sortedItems.get(index));
        }
        return Optional.empty();
    }
}
