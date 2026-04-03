package com.cobolmigration.model;

import java.time.LocalDate;

/**
 * Maps the COBOL WORKING-STORAGE record from search/search.cbl lines 17-33.
 * Represents items in a searchable table with multiple keys.
 */
public class ItemRecord {

    private int itemId1;    // pic 9(4)
    private int itemId2;    // pic 9(4)
    private int itemId3;    // pic 9(4)
    private String itemName; // pic x(16)
    private LocalDate itemDate;

    public ItemRecord() {
    }

    public ItemRecord(int itemId1, int itemId2, int itemId3, String itemName, LocalDate itemDate) {
        this.itemId1 = itemId1;
        this.itemId2 = itemId2;
        this.itemId3 = itemId3;
        this.itemName = itemName;
        this.itemDate = itemDate;
    }

    public int getItemId1() {
        return itemId1;
    }

    public void setItemId1(int itemId1) {
        this.itemId1 = itemId1;
    }

    public int getItemId2() {
        return itemId2;
    }

    public void setItemId2(int itemId2) {
        this.itemId2 = itemId2;
    }

    public int getItemId3() {
        return itemId3;
    }

    public void setItemId3(int itemId3) {
        this.itemId3 = itemId3;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public LocalDate getItemDate() {
        return itemDate;
    }

    public void setItemDate(LocalDate itemDate) {
        this.itemDate = itemDate;
    }

    @Override
    public String toString() {
        return "ItemRecord{" +
                "itemId1=" + itemId1 +
                ", itemId2=" + itemId2 +
                ", itemId3=" + itemId3 +
                ", itemName='" + itemName + '\'' +
                ", itemDate=" + itemDate +
                '}';
    }
}
