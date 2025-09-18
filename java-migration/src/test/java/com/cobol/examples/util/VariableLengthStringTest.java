package com.cobol.examples.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VariableLengthStringTest {
    
    @Test
    void testConstructorWithString() {
        VariableLengthString vls = new VariableLengthString("test");
        assertEquals("test", vls.getText());
        assertEquals(4, vls.getLength());
    }
    
    @Test
    void testConstructorWithTrimming() {
        VariableLengthString vls = new VariableLengthString("  test  ");
        assertEquals("test", vls.getText());
        assertEquals(4, vls.getLength());
    }
    
    @Test
    void testSetTextWithNull() {
        VariableLengthString vls = new VariableLengthString();
        vls.setText(null);
        assertEquals("", vls.getText());
        assertEquals(0, vls.getLength());
    }
    
    @Test
    void testGetTextWithWildcards() {
        VariableLengthString vls = new VariableLengthString("test");
        assertEquals("%test%", vls.getTextWithWildcards());
    }
    
    @Test
    void testPrepareForLikeQuery() {
        assertEquals("%test%", VariableLengthString.prepareForLikeQuery("test"));
        assertEquals("%test%", VariableLengthString.prepareForLikeQuery("  test  "));
        assertEquals("%", VariableLengthString.prepareForLikeQuery(null));
        assertEquals("%", VariableLengthString.prepareForLikeQuery(""));
        assertEquals("%", VariableLengthString.prepareForLikeQuery("   "));
    }
    
    @Test
    void testGetStoredCharLength() {
        assertEquals(4, VariableLengthString.getStoredCharLength("test"));
        assertEquals(4, VariableLengthString.getStoredCharLength("  test  "));
        assertEquals(0, VariableLengthString.getStoredCharLength(null));
        assertEquals(0, VariableLengthString.getStoredCharLength(""));
        assertEquals(0, VariableLengthString.getStoredCharLength("   "));
    }
}
