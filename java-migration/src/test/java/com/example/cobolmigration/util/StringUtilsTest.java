package com.example.cobolmigration.util;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link StringUtils} verifying COBOL intrinsic function equivalents.
 */
class StringUtilsTest {

    // TRIM tests (trim/ module)

    @Test
    void cobolTrim_removesLeadingAndTrailingSpaces() {
        assertEquals("Hello", StringUtils.cobolTrim("  Hello  "));
    }

    @Test
    void cobolTrim_returnsEmptyForNull() {
        assertEquals("", StringUtils.cobolTrim(null));
    }

    @Test
    void cobolTrim_returnsEmptyForBlank() {
        assertEquals("", StringUtils.cobolTrim("   "));
    }

    // UNSTRING tests (unstring/ module)

    @Test
    void cobolUnstring_splitsBySpace() {
        List<String> parts = StringUtils.cobolUnstring("Hello World", " ");
        assertEquals(2, parts.size());
        assertEquals("Hello", parts.get(0));
        assertEquals("World", parts.get(1));
    }

    @Test
    void cobolUnstring_splitsByPipe() {
        List<String> parts = StringUtils.cobolUnstring("A|B|C", "\\|");
        assertEquals(3, parts.size());
        assertEquals("A", parts.get(0));
        assertEquals("C", parts.get(2));
    }

    @Test
    void cobolUnstring_returnsEmptyListForNull() {
        List<String> parts = StringUtils.cobolUnstring(null, " ");
        assertTrue(parts.isEmpty());
    }

    // IS NUMERIC tests (is_numeric/ module)

    @Test
    void isNumeric_returnsTrueForInteger() {
        assertTrue(StringUtils.isNumeric("12345"));
    }

    @Test
    void isNumeric_returnsTrueForDecimal() {
        assertTrue(StringUtils.isNumeric("123.45"));
    }

    @Test
    void isNumeric_returnsTrueWithLeadingTrailingSpaces() {
        // Mirrors the COBOL process-trim behaviour
        assertTrue(StringUtils.isNumeric("  123  "));
    }

    @Test
    void isNumeric_returnsFalseForAlpha() {
        assertFalse(StringUtils.isNumeric("ABC"));
    }

    @Test
    void isNumeric_returnsFalseForNull() {
        assertFalse(StringUtils.isNumeric(null));
    }

    @Test
    void isNumeric_returnsFalseForEmpty() {
        assertFalse(StringUtils.isNumeric(""));
    }

    // NUMVAL tests (numval_test/ module)

    @Test
    void numval_parsesInteger() {
        assertEquals(42.0, StringUtils.numval("42"));
    }

    @Test
    void numval_parsesDecimal() {
        assertEquals(12345.63, StringUtils.numval("12345.63"), 0.001);
    }

    @Test
    void numval_trimsWhitespace() {
        assertEquals(99.0, StringUtils.numval("  99  "));
    }

    @Test
    void numval_throwsForInvalidInput() {
        assertThrows(NumberFormatException.class,
                () -> StringUtils.numval("ABC"));
    }

    @Test
    void numval_throwsForNull() {
        assertThrows(NumberFormatException.class,
                () -> StringUtils.numval(null));
    }
}
