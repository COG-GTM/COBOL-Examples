package com.cobol.examples.search;

import com.cobol.examples.model.TableItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BinarySearchTable {
    private List<TableItem> items;
    private boolean isSortedByAscendingKeys = false;
    private boolean isSortedByDescendingKey3 = false;
    
    public BinarySearchTable() {
        this.items = new ArrayList<>();
    }
    
    public BinarySearchTable(List<TableItem> items) {
        this.items = new ArrayList<>(items);
    }
    
    public void addItem(TableItem item) {
        items.add(item);
        isSortedByAscendingKeys = false;
        isSortedByDescendingKey3 = false;
    }
    
    public void sortByAscendingKeys() {
        Collections.sort(items, Comparator
            .comparing(TableItem::getItemId1)
            .thenComparing(TableItem::getItemId2));
        isSortedByAscendingKeys = true;
        isSortedByDescendingKey3 = false;
    }
    
    public void sortByDescendingKey3() {
        Collections.sort(items, Comparator
            .comparing(TableItem::getItemId3, Comparator.reverseOrder()));
        isSortedByDescendingKey3 = true;
        isSortedByAscendingKeys = false;
    }
    
    public TableItem searchByItemId1(int itemId1) {
        if (!isSortedByAscendingKeys) {
            sortByAscendingKeys();
        }
        
        TableItem searchKey = new TableItem();
        searchKey.setItemId1(itemId1);
        
        int index = Collections.binarySearch(items, searchKey, 
            Comparator.comparing(TableItem::getItemId1));
        
        return index >= 0 ? items.get(index) : null;
    }
    
    public TableItem searchByAllKeys(int itemId1, int itemId2, int itemId3) {
        if (!isSortedByAscendingKeys) {
            sortByAscendingKeys();
        }
        
        for (TableItem item : items) {
            if (item.getItemId1() == itemId1 && 
                item.getItemId2() == itemId2 && 
                item.getItemId3() == itemId3) {
                return item;
            }
        }
        return null;
    }
    
    public TableItem searchByItemId3Descending(int itemId3) {
        if (!isSortedByDescendingKey3) {
            sortByDescendingKey3();
        }
        
        TableItem searchKey = new TableItem();
        searchKey.setItemId3(itemId3);
        
        int index = Collections.binarySearch(items, searchKey, 
            Comparator.comparing(TableItem::getItemId3, Comparator.reverseOrder()));
        
        return index >= 0 ? items.get(index) : null;
    }
    
    public List<TableItem> getAllItems() {
        return new ArrayList<>(items);
    }
    
    public int size() {
        return items.size();
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    public void clear() {
        items.clear();
        isSortedByAscendingKeys = false;
        isSortedByDescendingKey3 = false;
    }
}
