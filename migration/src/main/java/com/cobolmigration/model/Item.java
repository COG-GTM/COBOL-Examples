package com.cobolmigration.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Item model migrated from search/search.cbl (lines 17-33).
 *
 * Implements Comparable with comparison logic matching COBOL's key specification:
 *   ASCENDING KEY IS ws-item-id-1, ws-item-id-2
 *   DESCENDING KEY IS ws-item-id-3
 *
 * COBOL field mappings:
 *   ws-item-id-1    (pic 9(4))     -> int id1
 *   ws-item-id-2    (pic 9(4))     -> int id2
 *   ws-item-id-3    (pic 9(4))     -> int id3
 *   ws-item-name    (pic x(16))    -> String name
 *   ws-item-date    (composite)    -> LocalDate date
 *     ws-item-year  (pic 9(4))     \
 *     filler        (pic x "/")     > composed into LocalDate
 *     ws-item-month (pic 99)       /
 *     filler        (pic x "/")
 *     ws-item-day   (pic 99)
 */
public class Item implements Comparable<Item> {

    private int id1;
    private int id2;
    private int id3;
    private String name;
    private LocalDate date;

    public Item() {
    }

    public Item(int id1, int id2, int id3, String name, LocalDate date) {
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Returns the date formatted as the COBOL composite date string: "YYYY/MM/DD".
     * Matches the COBOL ws-item-date layout with filler "/" characters.
     */
    public String getFormattedDate() {
        if (date == null) {
            return "";
        }
        return String.format("%04d/%02d/%02d", date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    /**
     * Parses a COBOL-formatted date string "YYYY/MM/DD" and sets the date field.
     */
    public void setDateFromString(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            this.date = null;
            return;
        }
        String[] parts = dateStr.split("/");
        if (parts.length == 3) {
            int year = Integer.parseInt(parts[0].trim());
            int month = Integer.parseInt(parts[1].trim());
            int day = Integer.parseInt(parts[2].trim());
            this.date = LocalDate.of(year, month, day);
        }
    }

    /**
     * Comparison logic matching COBOL's key specification:
     *   ASCENDING KEY IS ws-item-id-1, ws-item-id-2
     *   DESCENDING KEY IS ws-item-id-3
     *
     * First compares by id1 ascending, then id2 ascending, then id3 descending.
     */
    @Override
    public int compareTo(Item other) {
        int cmp = Integer.compare(this.id1, other.id1);
        if (cmp != 0) return cmp;

        cmp = Integer.compare(this.id2, other.id2);
        if (cmp != 0) return cmp;

        // Descending key for id3: reverse the comparison
        return Integer.compare(other.id3, this.id3);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id1 == item.id1 && id2 == item.id2 && id3 == item.id3;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id1, id2, id3);
    }

    @Override
    public String toString() {
        return String.format("Item{id1=%d, id2=%d, id3=%d, name='%s', date=%s}",
                id1, id2, id3, name, getFormattedDate());
    }
}
