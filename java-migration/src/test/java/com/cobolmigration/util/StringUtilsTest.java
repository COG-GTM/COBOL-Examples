package com.cobolmigration.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StringUtils.
 * Verifies behavior matches COBOL TRIM and UNSTRING operations.
 */
class StringUtilsTest {

    // --- TRIM tests (from trim/trim.cbl) ---

    @Test
    void trim_shouldRemoveBothLeadingAndTrailingSpaces() {
        // COBOL: FUNCTION TRIM("    hello world       ")
        assertEquals("hello world", StringUtils.trim("    hello world       "));
    }

    @Test
    void trimLeading_shouldRemoveOnlyLeadingSpaces() {
        // COBOL: FUNCTION TRIM(ws-test-string-1 LEADING)
        assertEquals("hello world       ", StringUtils.trimLeading("    hello world       "));
    }

    @Test
    void trimTrailing_shouldRemoveOnlyTrailingSpaces() {
        // COBOL: FUNCTION TRIM(ws-test-string-1 TRAILING)
        assertEquals("    hello world", StringUtils.trimTrailing("    hello world       "));
    }

    @Test
    void trim_shouldHandleNull() {
        assertNull(StringUtils.trim(null));
        assertNull(StringUtils.trimLeading(null));
        assertNull(StringUtils.trimTrailing(null));
    }

    @Test
    void trim_shouldHandleStringLiteral() {
        // COBOL: FUNCTION TRIM("   String literal    ")
        assertEquals("String literal", StringUtils.trim("   String literal    "));
    }

    // --- PIC X fixed-length field tests ---

    @Test
    void padToFixedLength_shouldPadWithTrailingSpaces() {
        // COBOL PIC X(30) pads short strings with spaces
        String result = StringUtils.padToFixedLength("hello", 30);
        assertEquals(30, result.length());
        assertTrue(result.startsWith("hello"));
        assertTrue(result.endsWith("                         "));
    }

    @Test
    void padToFixedLength_shouldTruncateLongStrings() {
        String result = StringUtils.padToFixedLength("this is a very long string", 10);
        assertEquals(10, result.length());
        assertEquals("this is a ", result);
    }

    @Test
    void padToFixedLength_shouldHandleNull() {
        String result = StringUtils.padToFixedLength(null, 10);
        assertEquals(10, result.length());
        assertEquals("          ", result);
    }

    // --- UNSTRING tests (from unstring/unstring.cbl) ---

    @Test
    void unstring_simpleDelimiter() {
        // COBOL EX 1: UNSTRING "Hello World" DELIMITED BY SPACE INTO part1 part2
        List<String> parts = StringUtils.unstring("Hello World", " ");
        assertEquals(2, parts.size());
        assertEquals("Hello", parts.get(0));
        assertEquals("World", parts.get(1));
    }

    @Test
    void unstring_pipeDelimiter() {
        List<String> parts = StringUtils.unstring("A|B|C", "|");
        assertEquals(3, parts.size());
        assertEquals("A", parts.get(0));
        assertEquals("B", parts.get(1));
        assertEquals("C", parts.get(2));
    }

    @Test
    void unstringMultipleDelimiters_shouldSplitCorrectly() {
        // COBOL EX 4: UNSTRING "A<B<CD>E%FG!HIJ|KL!MN>OP#QR!ST"
        //   DELIMITED BY "<" OR ">" OR "!" OR "|"
        List<StringUtils.UnstringResult> results =
                StringUtils.unstringMultipleDelimiters(
                        "A<B<CD>E%FG!HIJ|KL!MN>OP#QR!ST",
                        "<", ">", "!", "|");

        assertFalse(results.isEmpty());
        // First part should be "A" with delimiter "<"
        assertEquals("A", results.get(0).value());
        assertEquals("<", results.get(0).delimiter());
        assertEquals(1, results.get(0).charCount());
    }

    @Test
    void unstringMultipleDelimiters_shouldTrackDelimitersAndCounts() {
        List<StringUtils.UnstringResult> results =
                StringUtils.unstringMultipleDelimiters("AB<CD>EF", "<", ">");

        assertEquals(3, results.size());
        assertEquals("AB", results.get(0).value());
        assertEquals("<", results.get(0).delimiter());
        assertEquals(2, results.get(0).charCount());

        assertEquals("CD", results.get(1).value());
        assertEquals(">", results.get(1).delimiter());
        assertEquals(2, results.get(1).charCount());

        assertEquals("EF", results.get(2).value());
        assertEquals("", results.get(2).delimiter());
        assertEquals(2, results.get(2).charCount());
    }

    @Test
    void unstring_shouldHandleEmptySource() {
        List<String> parts = StringUtils.unstring("", " ");
        assertTrue(parts.isEmpty());
    }

    @Test
    void unstring_shouldHandleNull() {
        List<String> parts = StringUtils.unstring(null, " ");
        assertTrue(parts.isEmpty());
    }
}
