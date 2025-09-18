package com.cobol.examples.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class TableItem {
    private int itemId1;
    private int itemId2;
    private int itemId3;
    private String itemName;
    private LocalDate itemDate;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    
    public TableItem() {}
    
    public TableItem(int itemId1, int itemId2, int itemId3, String itemName, LocalDate itemDate) {
        this.itemId1 = itemId1;
        this.itemId2 = itemId2;
        this.itemId3 = itemId3;
        this.itemName = itemName;
        this.itemDate = itemDate;
    }
    
    public TableItem(int itemId1, int itemId2, int itemId3, String itemName, String dateString) {
        this.itemId1 = itemId1;
        this.itemId2 = itemId2;
        this.itemId3 = itemId3;
        this.itemName = itemName;
        this.itemDate = LocalDate.parse(dateString, DATE_FORMATTER);
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
    
    public String getFormattedDate() {
        return itemDate != null ? itemDate.format(DATE_FORMATTER) : "";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableItem tableItem = (TableItem) o;
        return itemId1 == tableItem.itemId1 &&
               itemId2 == tableItem.itemId2 &&
               itemId3 == tableItem.itemId3 &&
               Objects.equals(itemName, tableItem.itemName) &&
               Objects.equals(itemDate, tableItem.itemDate);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(itemId1, itemId2, itemId3, itemName, itemDate);
    }
    
    @Override
    public String toString() {
        return String.format("TableItem{id1=%d, id2=%d, id3=%d, name='%s', date='%s'}", 
                           itemId1, itemId2, itemId3, itemName, getFormattedDate());
    }
}
