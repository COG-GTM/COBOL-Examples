package com.cobolmigration.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for StringUtils - validates migration of COBOL string operations.
 */
class StringUtilsTest {

    // --- trim tests (replaces trim/trim.cbl) ---

    @Test
    void trim_removesLeadingAndTrailingSpaces() {
        assertEquals("hello world", StringUtils.trim("    hello world       "));
    }

    @Test
    void trim_handlesNull() {
        assertEquals("", StringUtils.trim(null));
    }

    @Test
    void trim_handlesEmptyString() {
        assertEquals("", StringUtils.trim(""));
    }

    @Test
    void trim_handlesAllSpaces() {
        assertEquals("", StringUtils.trim("          "));
    }

    @Test
    void trimLeading_removesOnlyLeadingSpaces() {
        assertEquals("hello world       ", StringUtils.trimLeading("    hello world       "));
    }

    @Test
    void trimTrailing_removesOnlyTrailingSpaces() {
        assertEquals("    hello world", StringUtils.trimTrailing("    hello world       "));
    }

    @Test
    void trim_stringLiteral() {
        assertEquals("String literal", StringUtils.trim("   String literal    "));
    }

    // --- unstring tests (replaces unstring/unstring.cbl) ---

    @Test
    void unstring_simpleSpaceDelimiter() {
        List<String> result = StringUtils.unstring("Hello World", " ");
        assertEquals(2, result.size());
        assertEquals("Hello", result.get(0));
        assertEquals("World", result.get(1));
    }

    @Test
    void unstring_pipeDelimiter() {
        List<String> result = StringUtils.unstring("A|B|C", "|");
        assertEquals(3, result.size());
        assertEquals("A", result.get(0));
        assertEquals("B", result.get(1));
        assertEquals("C", result.get(2));
    }

    @Test
    void unstring_nullInput() {
        List<String> result = StringUtils.unstring(null, " ");
        assertTrue(result.isEmpty());
    }

    @Test
    void unstring_emptyDelimiter() {
        List<String> result = StringUtils.unstring("Hello", "");
        assertEquals(1, result.size());
        assertEquals("Hello", result.get(0));
    }

    @Test
    void unstringMultipleDelimiters_splitsByAny() {
        List<String> result = StringUtils.unstringMultipleDelimiters(
                "A<B>C!D|E", "<", ">", "!", "|");
        assertEquals(5, result.size());
        assertEquals("A", result.get(0));
        assertEquals("B", result.get(1));
        assertEquals("C", result.get(2));
        assertEquals("D", result.get(3));
        assertEquals("E", result.get(4));
    }

    // --- isNumeric tests (replaces is_numeric/is_numeric.cbl) ---

    @Test
    void isNumeric_digitsOnly() {
        assertTrue(StringUtils.isNumeric("12345"));
    }

    @Test
    void isNumeric_withLeadingTrailingSpaces() {
        assertTrue(StringUtils.isNumeric("  12345  "));
    }

    @Test
    void isNumeric_withLetters() {
        assertFalse(StringUtils.isNumeric("abc123"));
    }

    @Test
    void isNumeric_null() {
        assertFalse(StringUtils.isNumeric(null));
    }

    @Test
    void isNumeric_emptyString() {
        assertFalse(StringUtils.isNumeric(""));
    }

    @Test
    void isNumeric_decimalNumber() {
        assertTrue(StringUtils.isNumeric("123.45"));
    }

    @Test
    void isNumeric_negativeNumber() {
        assertTrue(StringUtils.isNumeric("-123"));
    }

    // --- numval tests (replaces numval_test/numval_test.cbl) ---

    @Test
    void numval_integerString() {
        assertEquals(100.0, StringUtils.numval("100"));
    }

    @Test
    void numval_decimalString() {
        assertEquals(123.45, StringUtils.numval("123.45"));
    }

    @Test
    void numval_withSpaces() {
        assertEquals(42.0, StringUtils.numval("  42  "));
    }

    @Test
    void numval_null() {
        assertEquals(0.0, StringUtils.numval(null));
    }

    @Test
    void numval_empty() {
        assertEquals(0.0, StringUtils.numval(""));
    }

    @Test
    void numval_invalidThrows() {
        assertThrows(NumberFormatException.class, () -> StringUtils.numval("abc"));
    }

    @Test
    void numval_addition_matchesCobolBehavior() {
        // Replaces: compute ws-total = function numval(ws-x-val) + ws-9-val
        double val1 = StringUtils.numval("10");
        double val2 = 20;
        assertEquals(30.0, val1 + val2);
    }
}
