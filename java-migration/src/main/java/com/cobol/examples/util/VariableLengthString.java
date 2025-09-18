package com.cobol.examples.util;

public class VariableLengthString {
    private int length;
    private String text;
    
    public VariableLengthString() {
        this.text = "";
        this.length = 0;
    }
    
    public VariableLengthString(String text) {
        setText(text);
    }
    
    public void setText(String text) {
        if (text == null) {
            this.text = "";
            this.length = 0;
        } else {
            String trimmed = text.trim();
            this.text = trimmed;
            this.length = trimmed.length();
        }
    }
    
    public String getText() {
        return text;
    }
    
    public int getLength() {
        return length;
    }
    
    public String getTextWithWildcards() {
        return "%" + text + "%";
    }
    
    public String getTextWithWildcards(String searchString) {
        if (searchString == null) {
            return "%";
        }
        String trimmed = searchString.trim();
        return "%" + trimmed + "%";
    }
    
    public static String prepareForLikeQuery(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "%";
        }
        String trimmed = input.trim();
        return "%" + trimmed + "%";
    }
    
    public static int getStoredCharLength(String input) {
        if (input == null) {
            return 0;
        }
        return input.trim().length();
    }
    
    @Override
    public String toString() {
        return String.format("VariableLengthString{length=%d, text='%s'}", length, text);
    }
}
