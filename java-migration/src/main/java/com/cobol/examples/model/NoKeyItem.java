package com.cobol.examples.model;

import java.util.Objects;

public class NoKeyItem {
    private int id;
    private String value;
    
    public NoKeyItem() {}
    
    public NoKeyItem(int id, String value) {
        this.id = id;
        this.value = value;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoKeyItem noKeyItem = (NoKeyItem) o;
        return id == noKeyItem.id && Objects.equals(value, noKeyItem.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, value);
    }
    
    @Override
    public String toString() {
        return String.format("NoKeyItem{id=%d, value='%s'}", id, value);
    }
}
