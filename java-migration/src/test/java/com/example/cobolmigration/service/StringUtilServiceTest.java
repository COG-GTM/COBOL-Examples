package com.example.cobolmigration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for StringUtilService.
 * Test inputs match the COBOL test programs: trim/trim.cbl, unstring/unstring.cbl,
 * is_numeric/is_numeric.cbl.
 */
class StringUtilServiceTest {

    private StringUtilService service;

    @BeforeEach
    void setUp() {
        service = new StringUtilService();
    }

    // --- trimBoth tests (maps FUNCTION TRIM) ---

    @Test
    void trimBoth_removesLeadingAndTrailingSpaces() {
        // From trim.cbl: ws-test-string-1 = "    hello world       "
        assertEquals("hello world", service.trimBoth("    hello world       "));
    }

    @Test
    void trimBoth_noSpaces() {
        assertEquals("hello", service.trimBoth("hello"));
    }

    @Test
    void trimBoth_nullReturnsNull() {
        assertNull(service.trimBoth(null));
    }

    @Test
    void trimBoth_emptyString() {
        assertEquals("", service.trimBoth("   "));
    }

    @Test
    void trimBoth_stringLiteral() {
        // From trim.cbl: "   String literal    "
        assertEquals("String literal", service.trimBoth("   String literal    "));
    }

    // --- trimLeading tests (maps FUNCTION TRIM ... LEADING) ---

    @Test
    void trimLeading_removesLeadingSpacesOnly() {
        assertEquals("hello world       ", service.trimLeading("    hello world       "));
    }

    @Test
    void trimLeading_stringLiteral() {
        assertEquals("String literal    ", service.trimLeading("   String literal    "));
    }

    @Test
    void trimLeading_nullReturnsNull() {
        assertNull(service.trimLeading(null));
    }

    // --- trimTrailing tests (maps FUNCTION TRIM ... TRAILING) ---

    @Test
    void trimTrailing_removesTrailingSpacesOnly() {
        assertEquals("    hello world", service.trimTrailing("    hello world       "));
    }

    @Test
    void trimTrailing_stringLiteral() {
        assertEquals("   String literal", service.trimTrailing("   String literal    "));
    }

    @Test
    void trimTrailing_nullReturnsNull() {
        assertNull(service.trimTrailing(null));
    }

    // --- unstring tests (maps UNSTRING ... DELIMITED BY) ---

    @Test
    void unstring_simpleSpaceDelimiter() {
        // From unstring.cbl example 1: "Hello World" split by space
        List<String> parts = service.unstring("Hello World", " ");
        assertEquals(2, parts.size());
        assertEquals("Hello", parts.get(0));
        assertEquals("World", parts.get(1));
    }

    @Test
    void unstring_pipeDelimiter() {
        List<String> parts = service.unstring("A|B|C", "|");
        assertEquals(3, parts.size());
        assertEquals("A", parts.get(0));
        assertEquals("B", parts.get(1));
        assertEquals("C", parts.get(2));
    }

    @Test
    void unstring_noDelimiterFound() {
        List<String> parts = service.unstring("Hello", "|");
        assertEquals(1, parts.size());
        assertEquals("Hello", parts.get(0));
    }

    @Test
    void unstring_nullSource() {
        List<String> parts = service.unstring(null, " ");
        assertTrue(parts.isEmpty());
    }

    @Test
    void unstring_nullDelimiter() {
        List<String> parts = service.unstring("Hello", null);
        assertTrue(parts.isEmpty());
    }

    // --- isNumeric tests (maps IS NUMERIC) ---

    @Test
    void isNumeric_allDigits() {
        assertTrue(service.isNumeric("12345"));
    }

    @Test
    void isNumeric_withTrailingSpaces_notNumeric() {
        // In COBOL, a PIC X field with trailing spaces is NOT numeric
        assertFalse(service.isNumeric("123   "));
    }

    @Test
    void isNumeric_withLetters() {
        assertFalse(service.isNumeric("abc123"));
    }

    @Test
    void isNumeric_emptyString() {
        assertFalse(service.isNumeric(""));
    }

    @Test
    void isNumeric_null() {
        assertFalse(service.isNumeric(null));
    }

    @Test
    void isNumeric_singleDigit() {
        assertTrue(service.isNumeric("0"));
    }

    // --- isNumericTrimmed tests (maps TRIM + IS NUMERIC) ---

    @Test
    void isNumericTrimmed_digitsWithSpaces() {
        // From is_numeric.cbl process-trim paragraph
        assertTrue(service.isNumericTrimmed("  12345  "));
    }

    @Test
    void isNumericTrimmed_digitsOnly() {
        assertTrue(service.isNumericTrimmed("42"));
    }

    @Test
    void isNumericTrimmed_withLetters() {
        assertFalse(service.isNumericTrimmed("  abc  "));
    }

    @Test
    void isNumericTrimmed_null() {
        assertFalse(service.isNumericTrimmed(null));
    }
}
