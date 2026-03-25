package com.cobolmigration.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for StringUtils.
 * Tests trim, unstring, isNumeric, numval with edge cases
 * including COBOL space-padding behavior.
 */
class StringUtilsTest {

    // === TRIM tests (replacing trim/trim.cbl behavior) ===

    @Test
    void trim_removesLeadingAndTrailingSpaces() {
        // Matches trim.cbl line 23: function trim(ws-test-string-1)
        assertEquals("hello world", StringUtils.trim("    hello world       "));
    }

    @Test
    void trim_handlesNoSpaces() {
        assertEquals("hello", StringUtils.trim("hello"));
    }

    @Test
    void trim_handlesAllSpaces() {
        assertEquals("", StringUtils.trim("          "));
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
    void trim_preservesInternalSpaces() {
        // COBOL TRIM only removes leading/trailing, not internal spaces
        assertEquals("hello   world", StringUtils.trim("  hello   world  "));
    }

    // === UNSTRING tests (replacing unstring/unstring.cbl behavior) ===

    @Test
    void unstring_splitsOnSpace() {
        // Matches unstring.cbl example 1 (lines 58-61)
        String[] parts = StringUtils.unstring("Hello World", " ");
        assertEquals(2, parts.length);
        assertEquals("Hello", parts[0]);
        assertEquals("World", parts[1]);
    }

    @Test
    void unstring_splitsOnPipe() {
        // Matches unstring.cbl ws-delimiter = '|' (line 21)
        String[] parts = StringUtils.unstring("A|B|C", "|");
        assertEquals(3, parts.length);
        assertEquals("A", parts[0]);
        assertEquals("B", parts[1]);
        assertEquals("C", parts[2]);
    }

    @Test
    void unstring_handlesNoDelimiter() {
        String[] parts = StringUtils.unstring("Hello", "|");
        assertEquals(1, parts.length);
        assertEquals("Hello", parts[0]);
    }

    @Test
    void unstring_handlesNullSource() {
        String[] parts = StringUtils.unstring(null, " ");
        assertEquals(0, parts.length);
    }

    @Test
    void unstring_handlesNullDelimiter() {
        String[] parts = StringUtils.unstring("Hello", null);
        assertEquals(0, parts.length);
    }

    @Test
    void unstring_preservesEmptyParts() {
        // COBOL UNSTRING preserves empty fields when consecutive delimiters found
        String[] parts = StringUtils.unstring("A||C", "|");
        assertEquals(3, parts.length);
        assertEquals("A", parts[0]);
        assertEquals("", parts[1]);
        assertEquals("C", parts[2]);
    }

    @Test
    void unstring_splitsOnCommaAndDot() {
        // Matches unstring.cbl example 6 (lines 243-248): splitting formatted number
        String[] parts = StringUtils.unstring("123,456.12", ",");
        assertEquals(2, parts.length);
        assertEquals("123", parts[0]);
        assertEquals("456.12", parts[1]);
    }

    // === IS NUMERIC tests (replacing is_numeric/is_numeric.cbl behavior) ===

    @Test
    void isNumeric_trueForDigits() {
        // After trim, contiguous digits pass (is_numeric.cbl lines 71-75)
        assertTrue(StringUtils.isNumeric("12345"));
    }

    @Test
    void isNumeric_trueForDigitsWithSpaces() {
        // COBOL IS NUMERIC fails with spaces (line 32-36), but our impl trims first
        assertTrue(StringUtils.isNumeric("  12345  "));
    }

    @Test
    void isNumeric_trueForDecimal() {
        assertTrue(StringUtils.isNumeric("123.45"));
    }

    @Test
    void isNumeric_trueForNegative() {
        assertTrue(StringUtils.isNumeric("-123"));
    }

    @Test
    void isNumeric_falseForAlpha() {
        assertFalse(StringUtils.isNumeric("abc"));
    }

    @Test
    void isNumeric_falseForMixed() {
        assertFalse(StringUtils.isNumeric("12abc"));
    }

    @Test
    void isNumeric_falseForNull() {
        assertFalse(StringUtils.isNumeric(null));
    }

    @Test
    void isNumeric_falseForBlank() {
        assertFalse(StringUtils.isNumeric("   "));
    }

    @Test
    void isNumeric_falseForEmpty() {
        assertFalse(StringUtils.isNumeric(""));
    }

    // === NUMVAL tests (replacing numval_test/numval_test.cbl behavior) ===

    @Test
    void numericValue_convertsSimpleNumber() {
        // Matches numval_test.cbl line 28: function numval(ws-x-val)
        assertEquals(new BigDecimal("12345"), StringUtils.numericValue("12345"));
    }

    @Test
    void numericValue_convertsDecimal() {
        assertEquals(new BigDecimal("123.45"), StringUtils.numericValue("123.45"));
    }

    @Test
    void numericValue_handlesLeadingTrailingSpaces() {
        assertEquals(new BigDecimal("42"), StringUtils.numericValue("  42  "));
    }

    @Test
    void numericValue_stripsCurrencySymbol() {
        // COBOL NUMVAL can handle formatted numbers
        assertEquals(new BigDecimal("1234.56"), StringUtils.numericValue("$1,234.56"));
    }

    @Test
    void numericValue_throwsForNonNumeric() {
        assertThrows(NumberFormatException.class, () -> StringUtils.numericValue("abc"));
    }

    @Test
    void numericValue_throwsForNull() {
        assertThrows(NumberFormatException.class, () -> StringUtils.numericValue(null));
    }

    @Test
    void numericValue_throwsForBlank() {
        assertThrows(NumberFormatException.class, () -> StringUtils.numericValue("   "));
    }
}
