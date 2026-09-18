package com.cobolexamples.support;

public class Field {
    private final int width;
    private String value;

    public Field(int width) {
        this.width = width;
        this.value = " ".repeat(width);
    }

    public Field(int width, String v) {
        this.width = width;
        set(v);
    }

    public void set(String v) {
        this.value = Pic.x(v, width);
    }

    public String get() {
        return value;
    }

    public int width() {
        return width;
    }

    @Override
    public String toString() {
        return value;
    }
}
