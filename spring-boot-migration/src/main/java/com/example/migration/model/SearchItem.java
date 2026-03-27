package com.example.migration.model;

/**
 * Replaces the COBOL ws-item-table structure from search/search.cbl (lines 17-32).
 *
 * COBOL structure:
 *   01 ws-item-table OCCURS 3 TIMES
 *       ASCENDING KEY IS ws-item-id-1, ws-item-id-2
 *       DESCENDING KEY IS ws-item-id-3
 *       INDEXED BY idx.
 *       05 ws-item-id-1      pic 9(4).
 *       05 ws-item-id-2      pic 9(4).
 *       05 ws-item-id-3      pic 9(4).
 *       05 ws-item-name      pic x(16).
 *       05 ws-item-date.
 *           10 ws-item-year  pic 9(4).
 *           10 filler        pic x value "/".
 *           10 ws-item-month pic 99.
 *           10 filler        pic x value "/".
 *           10 ws-item-day   pic 99.
 */
public class SearchItem implements Comparable<SearchItem> {

    private int id1;
    private int id2;
    private int id3;
    private String name;
    private String date;

    public SearchItem() {
    }

    public SearchItem(int id1, int id2, int id3, String name, String date) {
        this.id1 = id1;
        this.id2 = id2;
        this.id3 = id3;
        this.name = name;
        this.date = date;
    }

    @Override
    public int compareTo(SearchItem other) {
        int cmp = Integer.compare(this.id1, other.id1);
        if (cmp != 0) return cmp;
        return Integer.compare(this.id2, other.id2);
    }

    public int getId1() {
        return id1;
    }

    public void setId1(int id1) {
        this.id1 = id1;
    }

    public int getId2() {
        return id2;
    }

    public void setId2(int id2) {
        this.id2 = id2;
    }

    public int getId3() {
        return id3;
    }

    public void setId3(int id3) {
        this.id3 = id3;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
