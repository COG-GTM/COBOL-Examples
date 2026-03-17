package com.example.cobolmigration.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests split/trim operations matching COBOL examples from trim.cbl and unstring.cbl.
 */
class StringUtilServiceTest {

    private StringUtilService service;

    @BeforeEach
    void setUp() {
        service = new StringUtilService();
    }

    // --- trimVariants tests (matching trim/trim.cbl) ---

    @Test
    void trimVariants_matchesCobolTrimFunction() {
        // From trim.cbl line 16: ws-test-string-1 = "    hello world       "
        String input = "    hello world       ";
        Map<String, String> result = service.trimVariants(input);

        // FUNCTION TRIM(ws-test-string-1)          -> "hello world"
        assertEquals("hello world", result.get("full"));
        // FUNCTION TRIM(ws-test-string-1 LEADING)  -> "hello world       "
        assertEquals("hello world       ", result.get("leading"));
        // FUNCTION TRIM(ws-test-string-1 TRAILING) -> "    hello world"
        assertEquals("    hello world", result.get("trailing"));
    }

    @Test
    void trimVariants_nullInput() {
        Map<String, String> result = service.trimVariants(null);
        assertNull(result.get("full"));
        assertNull(result.get("leading"));
        assertNull(result.get("trailing"));
    }

    @Test
    void trimVariants_stringLiteral() {
        // From trim.cbl lines 50-57
        String input = "   String literal    ";
        Map<String, String> result = service.trimVariants(input);
        assertEquals("String literal", result.get("full"));
        assertEquals("String literal    ", result.get("leading"));
        assertEquals("   String literal", result.get("trailing"));
    }

    // --- splitByDelimiter tests (matching unstring.cbl Example 1) ---

    @Test
    void splitByDelimiter_simpleSpace() {
        // unstring.cbl Example 1 (lines 58-61):
        // UNSTRING "Hello World" DELIMITED BY SPACE INTO ws-part-1 ws-part-2
        List<String> result = service.splitByDelimiter("Hello World", " ");
        assertEquals(2, result.size());
        assertEquals("Hello", result.get(0));
        assertEquals("World", result.get(1));
    }

    @Test
    void splitByDelimiter_pipeDelimiter() {
        List<String> result = service.splitByDelimiter("A|B|C", "|");
        assertEquals(3, result.size());
        assertEquals("A", result.get(0));
        assertEquals("B", result.get(1));
        assertEquals("C", result.get(2));
    }

    // --- splitByMultipleDelimiters tests (matching unstring.cbl Examples 4-5) ---

    @Test
    @SuppressWarnings("unchecked")
    void splitByMultipleDelimiters_matchesCobolExample4() {
        // unstring.cbl Example 4 (lines 148, 155-163):
        // Source: "A<B<CD>E%FG!HIJ|KL!MN>OP#QR!ST"
        // Delimiters: "<", ">", "!", "|"
        String source = "A<B<CD>E%FG!HIJ|KL!MN>OP#QR!ST";
        List<String> delimiters = List.of("<", ">", "!", "|");

        Map<String, Object> result = service.splitByMultipleDelimiters(source, delimiters);
        List<Map<String, Object>> parts = (List<Map<String, Object>>) result.get("parts");

        // First split: "A" delimited by "<"
        assertEquals("A", parts.get(0).get("value"));
        assertEquals("<", parts.get(0).get("delimiter"));
        assertEquals(1, parts.get(0).get("charCount"));

        // Second split: "B" delimited by "<"
        assertEquals("B", parts.get(1).get("value"));
        assertEquals("<", parts.get(1).get("delimiter"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void splitByMultipleDelimiters_matchesCobolExample5() {
        // unstring.cbl Example 5 (lines 183, 188-214):
        // Source: "A<B<CD>EFG!HIJ|KLMN>O"
        // Delimiters: "<", ">", "!", "|"
        String source = "A<B<CD>EFG!HIJ|KLMN>O";
        List<String> delimiters = List.of("<", ">", "!", "|");

        Map<String, Object> result = service.splitByMultipleDelimiters(source, delimiters);
        List<Map<String, Object>> parts = (List<Map<String, Object>>) result.get("parts");
        int fieldsFilled = (int) result.get("fieldsFilled");

        // Java splits all segments including the trailing "O" after the last delimiter.
        // In COBOL, only 6 INTO destinations were provided so "O" was dropped.
        assertEquals(7, fieldsFilled);
        assertEquals("A", parts.get(0).get("value"));
        assertEquals("B", parts.get(1).get("value"));
        assertEquals("CD", parts.get(2).get("value"));
        assertEquals("EFG", parts.get(3).get("value"));
        assertEquals("HIJ", parts.get(4).get("value"));
        assertEquals("KLMN", parts.get(5).get("value"));
        assertEquals("O", parts.get(6).get("value"));
    }

    // --- splitFormattedNumber tests (matching unstring.cbl Example 6) ---

    @Test
    void splitFormattedNumber_matchesCobolExample6() {
        // unstring.cbl Example 6 (lines 240-252):
        // Source: $123,456.12 -> after removing $ and splitting by ',' and '.'
        // Parts: "123", "456", "12"
        List<String> result = service.splitFormattedNumber("$123,456.12");
        assertEquals(3, result.size());
        assertEquals("123", result.get(0));
        assertEquals("456", result.get(1));
        assertEquals("12", result.get(2));
    }

    @Test
    void splitFormattedNumber_withoutDollarSign() {
        List<String> result = service.splitFormattedNumber("999,999.99");
        assertEquals(3, result.size());
        assertEquals("999", result.get(0));
        assertEquals("999", result.get(1));
        assertEquals("99", result.get(2));
    }

    @Test
    void splitFormattedNumber_nullInput() {
        List<String> result = service.splitFormattedNumber(null);
        assertEquals(0, result.size());
    }
}
