package com.cobol.examples.search;

import com.cobol.examples.model.NoKeyItem;
import java.util.ArrayList;
import java.util.List;

public class SequentialSearchTable {
    private List<NoKeyItem> items;
    
    public SequentialSearchTable() {
        this.items = new ArrayList<>();
    }
    
    public SequentialSearchTable(List<NoKeyItem> items) {
        this.items = new ArrayList<>(items);
    }
    
    public void addItem(NoKeyItem item) {
        items.add(item);
    }
    
    public NoKeyItem searchById(int id) {
        for (NoKeyItem item : items) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }
    
    public NoKeyItem searchByValue(String value) {
        for (NoKeyItem item : items) {
            if (value != null && value.equals(item.getValue())) {
                return item;
            }
        }
        return null;
    }
    
    public List<NoKeyItem> searchByValueContains(String substring) {
        List<NoKeyItem> results = new ArrayList<>();
        if (substring == null) {
            return results;
        }
        
        for (NoKeyItem item : items) {
            if (item.getValue() != null && item.getValue().contains(substring)) {
                results.add(item);
            }
        }
        return results;
    }
    
    public List<NoKeyItem> getAllItems() {
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
    }
}
