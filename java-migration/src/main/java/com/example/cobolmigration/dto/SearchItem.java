package com.example.cobolmigration.dto;

/**
 * DTO representing a search table item from search/search.cbl (lines 17-33).
 *
 * The COBOL OCCURS table has ascending keys (ws-item-id-1, ws-item-id-2) and
 * a descending key (ws-item-id-3). In Java this is a plain POJO; sorting and
 * binary search use Comparable / Collections.binarySearch().
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

    /**
     * Natural ordering by ascending id1, then ascending id2 — mirrors the
     * COBOL ASCENDING KEY IS ws-item-id-1, ws-item-id-2 definition.
     */
    @Override
    public int compareTo(SearchItem other) {
        int cmp = Integer.compare(this.id1, other.id1);
        if (cmp != 0) {
            return cmp;
        }
        return Integer.compare(this.id2, other.id2);
    }
}
