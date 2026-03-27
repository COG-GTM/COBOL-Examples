package com.example.migration.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StringUtils.
 * Verifies behavior matches the original COBOL programs:
 *   - trim/trim.cbl
 *   - unstring/unstring.cbl
 *   - is_numeric/is_numeric.cbl
 *   - numval_test/numval_test.cbl
 */
class StringUtilsTest {

    // === TRIM tests (replaces trim/trim.cbl) ===

    @Test
    void trim_removesBothLeadingAndTrailingSpaces() {
        assertEquals("hello world", StringUtils.trim("    hello world       "));
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
    void trim_handlesNull() {
        assertNull(StringUtils.trim(null));
    }

    @Test
    void trim_handlesEmptyString() {
        assertEquals("", StringUtils.trim(""));
    }

    @Test
    void trim_handlesStringLiteral() {
        assertEquals("String literal", StringUtils.trim("   String literal    "));
    }

    // === UNSTRING tests (replaces unstring/unstring.cbl) ===

    @Test
    void unstring_simpleSpaceDelimiter() {
        StringUtils.UnstringResult result = StringUtils.unstring("Hello World", " ");

        assertEquals(2, result.getFieldsFilled());
        assertEquals("Hello", result.getParts().get(0));
        assertEquals("World", result.getParts().get(1));
    }

    @Test
    void unstring_multipleDelimiters() {
        StringUtils.UnstringResult result = StringUtils.unstring(
                "A<B<CD>E%FG!HIJ|KL!MN>OP#QR!ST", "<", ">", "!", "|");

        assertTrue(result.getFieldsFilled() > 1);
        assertEquals("A", result.getParts().get(0));
    }

    @Test
    void unstring_withCommaAndDotDelimiters() {
        StringUtils.UnstringResult result = StringUtils.unstring("123,456.12", ",", ".");

        assertEquals(3, result.getFieldsFilled());
        assertEquals("123", result.getParts().get(0));
        assertEquals("456", result.getParts().get(1));
        assertEquals("12", result.getParts().get(2));
    }

    @Test
    void unstring_handlesNullSource() {
        StringUtils.UnstringResult result = StringUtils.unstring(null, " ");
        assertTrue(result.getParts().isEmpty());
    }

    @Test
    void unstring_capturesDelimitersFound() {
        StringUtils.UnstringResult result = StringUtils.unstring("A|B|C", "|");

        assertEquals(2, result.getDelimitersFound().size());
        assertEquals("|", result.getDelimitersFound().get(0));
        assertEquals("|", result.getDelimitersFound().get(1));
    }

    @Test
    void unstring_capturesCharCounts() {
        StringUtils.UnstringResult result = StringUtils.unstring("AB|CDE|F", "|");

        assertEquals(3, result.getCharCounts().size());
        assertEquals(2, result.getCharCounts().get(0));
        assertEquals(3, result.getCharCounts().get(1));
        assertEquals(1, result.getCharCounts().get(2));
    }

    // === IS NUMERIC tests (replaces is_numeric/is_numeric.cbl) ===

    @Test
    void isNumeric_withDigits_returnsTrue() {
        assertTrue(StringUtils.isNumeric("12345"));
    }

    @Test
    void isNumeric_withDecimal_returnsTrue() {
        assertTrue(StringUtils.isNumeric("123.45"));
    }

    @Test
    void isNumeric_withNegativeNumber_returnsTrue() {
        assertTrue(StringUtils.isNumeric("-42"));
    }

    @Test
    void isNumeric_withLeadingTrailingSpaces_returnsTrueAfterTrim() {
        assertTrue(StringUtils.isNumeric("  123  "));
    }

    @Test
    void isNumeric_withLetters_returnsFalse() {
        assertFalse(StringUtils.isNumeric("abc"));
    }

    @Test
    void isNumeric_withMixedContent_returnsFalse() {
        assertFalse(StringUtils.isNumeric("12a34"));
    }

    @Test
    void isNumeric_withNull_returnsFalse() {
        assertFalse(StringUtils.isNumeric(null));
    }

    @Test
    void isNumeric_withBlank_returnsFalse() {
        assertFalse(StringUtils.isNumeric("   "));
    }

    // === NUMVAL tests (replaces numval_test/numval_test.cbl) ===

    @Test
    void numval_convertsStringToDouble() {
        assertEquals(123.0, StringUtils.numval("123"), 0.001);
    }

    @Test
    void numval_convertsDecimalString() {
        assertEquals(45.67, StringUtils.numval("45.67"), 0.001);
    }

    @Test
    void numval_handlesLeadingTrailingSpaces() {
        assertEquals(99.0, StringUtils.numval("  99  "), 0.001);
    }

    @Test
    void numval_convertsNegativeString() {
        assertEquals(-42.0, StringUtils.numval("-42"), 0.001);
    }

    @Test
    void numval_throwsForNonNumeric() {
        assertThrows(IllegalArgumentException.class, () -> StringUtils.numval("abc"));
    }

    @Test
    void numval_throwsForNull() {
        assertThrows(IllegalArgumentException.class, () -> StringUtils.numval(null));
    }

    @Test
    void numval_throwsForBlank() {
        assertThrows(IllegalArgumentException.class, () -> StringUtils.numval("   "));
    }
}
