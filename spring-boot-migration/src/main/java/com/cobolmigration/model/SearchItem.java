package com.cobolmigration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Model class replacing the COBOL ws-item-table structure in search/search.cbl (lines 17-33).
 * Fields map to:
 *   ws-item-id-1  -> itemId1
 *   ws-item-id-2  -> itemId2
 *   ws-item-id-3  -> itemId3
 *   ws-item-name  -> itemName
 *   ws-item-date  -> itemDate
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchItem implements Comparable<SearchItem> {

    private int itemId1;
    private int itemId2;
    private int itemId3;
    private String itemName;
    private LocalDate itemDate;

    @Override
    public int compareTo(SearchItem other) {
        int cmp = Integer.compare(this.itemId1, other.itemId1);
        if (cmp != 0) return cmp;
        return Integer.compare(this.itemId2, other.itemId2);
    }
}
