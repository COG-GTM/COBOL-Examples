package com.cobolmigration.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Comprehensive unit tests for StringUtils.
 * Verifies behavior matching COBOL intrinsic functions from:
 *   trim/trim.cbl, unstring/unstring.cbl, is_numeric/is_numeric.cbl
 */
class StringUtilsTest {

    // --- cobolTrim tests (replaces FUNCTION TRIM) ---

    @Test
    void cobolTrim_shouldTrimBothEnds() {
        assertEquals("hello world", StringUtils.cobolTrim("    hello world       "));
    }

    @Test
    void cobolTrim_shouldHandleNull() {
        assertNull(StringUtils.cobolTrim(null));
    }

    @Test
    void cobolTrimLeading_shouldTrimLeadingOnly() {
        assertEquals("hello world       ", StringUtils.cobolTrimLeading("    hello world       "));
    }

    @Test
    void cobolTrimTrailing_shouldTrimTrailingOnly() {
        assertEquals("    hello world", StringUtils.cobolTrimTrailing("    hello world       "));
    }

    @Test
    void cobolTrim_withStringLiteral() {
        assertEquals("String literal", StringUtils.cobolTrim("   String literal    "));
    }

    // --- cobolUnstring tests (replaces UNSTRING ... DELIMITED BY) ---

    @Test
    void cobolUnstring_shouldSplitBySpace() {
        String[] parts = StringUtils.cobolUnstring("Hello World", " ");
        assertEquals(2, parts.length);
        assertEquals("Hello", parts[0]);
        assertEquals("World", parts[1]);
    }

    @Test
    void cobolUnstring_shouldSplitByPipe() {
        String[] parts = StringUtils.cobolUnstring("A|B|C", "|");
        assertEquals(3, parts.length);
        assertEquals("A", parts[0]);
        assertEquals("B", parts[1]);
        assertEquals("C", parts[2]);
    }

    @Test
    void cobolUnstring_shouldHandleNull() {
        String[] parts = StringUtils.cobolUnstring(null, " ");
        assertEquals(0, parts.length);
    }

    @Test
    void cobolUnstringMultiple_shouldSplitByMultipleDelimiters() {
        StringUtils.UnstringResult result =
                StringUtils.cobolUnstringMultiple("A<B>C!D|E", "<", ">", "!", "|");

        assertEquals(5, result.getParts().length);
        assertEquals("A", result.getParts()[0]);
        assertEquals("B", result.getParts()[1]);
        assertEquals("C", result.getParts()[2]);
        assertEquals("D", result.getParts()[3]);
        assertEquals("E", result.getParts()[4]);
    }

    @Test
    void cobolUnstringMultiple_shouldTrackDelimiters() {
        StringUtils.UnstringResult result =
                StringUtils.cobolUnstringMultiple("A<B>C", "<", ">");

        assertEquals("<", result.getDelimiters()[0]);
        assertEquals(">", result.getDelimiters()[1]);
    }

    @Test
    void cobolUnstringMultiple_shouldTrackCharCounts() {
        StringUtils.UnstringResult result =
                StringUtils.cobolUnstringMultiple("AB<CD>EFG", "<", ">");

        assertEquals(2, result.getCharCounts()[0]); // "AB"
        assertEquals(2, result.getCharCounts()[1]); // "CD"
        assertEquals(3, result.getCharCounts()[2]); // "EFG"
    }

    @Test
    void cobolUnstringMultiple_shouldCountFieldsFilled() {
        StringUtils.UnstringResult result =
                StringUtils.cobolUnstringMultiple("A<B<CD>E%FG!HIJ|KL!MN>OP",
                        "<", ">", "!", "|", "%");

        assertTrue(result.getFieldsFilled() > 1);
    }

    // --- isNumeric tests (replaces IS NUMERIC class condition) ---

    @Test
    void isNumeric_shouldReturnTrueForDigits() {
        assertTrue(StringUtils.isNumeric("12345"));
    }

    @Test
    void isNumeric_shouldReturnFalseForSpaces() {
        // In COBOL, trailing spaces cause IS NUMERIC to fail for PIC X fields
        assertFalse(StringUtils.isNumeric("123   "));
    }

    @Test
    void isNumeric_shouldReturnFalseForLetters() {
        assertFalse(StringUtils.isNumeric("abc123"));
    }

    @Test
    void isNumeric_shouldReturnFalseForEmpty() {
        assertFalse(StringUtils.isNumeric(""));
    }

    @Test
    void isNumeric_shouldReturnFalseForNull() {
        assertFalse(StringUtils.isNumeric(null));
    }

    @Test
    void isNumeric_shouldReturnTrueForSingleDigit() {
        assertTrue(StringUtils.isNumeric("0"));
    }
}
