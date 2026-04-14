package com.cobolmigration.util;

import com.cobolmigration.util.StringUtils.TrimMode;
import com.cobolmigration.util.StringUtils.UnstringResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StringUtils.
 * Verifies trim and unstring produce same results as COBOL programs
 * from trim/trim.cbl and unstring/unstring.cbl.
 */
class StringUtilsTest {

    // ===== TRIM TESTS =====
    // Based on trim/trim.cbl test cases

    @Test
    void testTrimBoth() {
        // COBOL: FUNCTION TRIM(ws-test-string-1)
        // ws-test-string-1 = "    hello world       "
        assertEquals("hello world", StringUtils.trim("    hello world       "));
    }

    @Test
    void testTrimLeading() {
        // COBOL: FUNCTION TRIM(ws-test-string-1 LEADING)
        assertEquals("hello world       ", StringUtils.trim("    hello world       ", TrimMode.LEADING));
    }

    @Test
    void testTrimTrailing() {
        // COBOL: FUNCTION TRIM(ws-test-string-1 TRAILING)
        assertEquals("    hello world", StringUtils.trim("    hello world       ", TrimMode.TRAILING));
    }

    @Test
    void testTrimNull() {
        assertEquals("", StringUtils.trim(null));
        assertEquals("", StringUtils.trim(null, TrimMode.LEADING));
        assertEquals("", StringUtils.trim(null, TrimMode.TRAILING));
    }

    @Test
    void testTrimEmpty() {
        assertEquals("", StringUtils.trim(""));
        assertEquals("", StringUtils.trim("   "));
    }

    @Test
    void testTrimLiteral() {
        // COBOL: FUNCTION TRIM("   String literal    ")
        assertEquals("String literal", StringUtils.trim("   String literal    "));
        assertEquals("String literal    ", StringUtils.trim("   String literal    ", TrimMode.LEADING));
        assertEquals("   String literal", StringUtils.trim("   String literal    ", TrimMode.TRAILING));
    }

    @Test
    void testTrimNoWhitespace() {
        assertEquals("hello", StringUtils.trim("hello"));
        assertEquals("hello", StringUtils.trim("hello", TrimMode.LEADING));
        assertEquals("hello", StringUtils.trim("hello", TrimMode.TRAILING));
    }

    // ===== UNSTRING TESTS =====
    // Based on unstring/unstring.cbl test cases

    @Test
    void testUnstringSimple() {
        // COBOL Example 1: UNSTRING "Hello World" DELIMITED BY SPACE INTO part1, part2
        List<String> parts = StringUtils.unstring("Hello World", " ");
        assertEquals(2, parts.size());
        assertEquals("Hello", parts.get(0));
        assertEquals("World", parts.get(1));
    }

    @Test
    void testUnstringWithPipeDelimiter() {
        List<String> parts = StringUtils.unstring("A|B|C", "|");
        assertEquals(3, parts.size());
        assertEquals("A", parts.get(0));
        assertEquals("B", parts.get(1));
        assertEquals("C", parts.get(2));
    }

    @Test
    void testUnstringNullInput() {
        List<String> parts = StringUtils.unstring(null, " ");
        assertTrue(parts.isEmpty());
    }

    @Test
    void testUnstringEmptyInput() {
        List<String> parts = StringUtils.unstring("", " ");
        assertTrue(parts.isEmpty());
    }

    @Test
    void testUnstringNullDelimiter() {
        List<String> parts = StringUtils.unstring("Hello", null);
        assertEquals(1, parts.size());
        assertEquals("Hello", parts.get(0));
    }

    @Test
    void testUnstringFullWithMultipleDelimiters() {
        // COBOL Example 4: Multiple delimiters "<", ">", "!", "|"
        // Source: "A<B<CD>E%FG!HIJ|KL!MN>OP#QR!ST"
        String source = "A<B<CD>E%FG!HIJ|KL!MN>OP#QR!ST";
        String[] delimiters = {"<", ">", "!", "|"};

        UnstringResult result = StringUtils.unstringFull(source, delimiters, 1, 0);

        assertTrue(result.getFieldsFilled() > 0);
        assertEquals("A", result.getParts().get(0));
        assertEquals("<", result.getDelimiters().get(0));
        assertEquals(1, result.getCounts().get(0));
    }

    @Test
    void testUnstringFullWithPointer() {
        // Verify pointer tracking (1-based, COBOL-style)
        String source = "Hello World";
        String[] delimiters = {" "};

        UnstringResult result = StringUtils.unstringFull(source, delimiters, 1, 1);

        assertEquals(1, result.getFieldsFilled());
        assertEquals("Hello", result.getParts().get(0));
        // Pointer should be at position after "Hello " (1-based)
        assertEquals(7, result.getPointer());

        // Continue from where we left off
        UnstringResult result2 = StringUtils.unstringFull(source, delimiters, result.getPointer(), 1);
        assertEquals("World", result2.getParts().get(0));
    }

    @Test
    void testUnstringFullTallying() {
        // COBOL: TALLYING IN ws-fields-filled
        String source = "A<B<CD>EFG!HIJ|KLMN>O";
        String[] delimiters = {"<", ">", "!", "|"};

        UnstringResult result = StringUtils.unstringFull(source, delimiters, 1, 6);

        assertEquals(6, result.getFieldsFilled());
        assertEquals(6, result.getParts().size());
    }

    @Test
    void testUnstringFullExample5() {
        // COBOL Example 5: "A<B<CD>EFG!HIJ|KLMN>O"
        String source = "A<B<CD>EFG!HIJ|KLMN>O";
        String[] delimiters = {"<", ">", "!", "|"};

        UnstringResult result = StringUtils.unstringFull(source, delimiters, 1, 6);

        assertEquals("A", result.getParts().get(0));
        assertEquals("<", result.getDelimiters().get(0));

        assertEquals("B", result.getParts().get(1));
        assertEquals("<", result.getDelimiters().get(1));

        assertEquals("CD", result.getParts().get(2));
        assertEquals(">", result.getDelimiters().get(2));
    }

    @Test
    void testUnstringFullNoDelimitersFound() {
        String source = "NoDelimitersHere";
        String[] delimiters = {"|", ";"};

        UnstringResult result = StringUtils.unstringFull(source, delimiters, 1, 0);

        assertEquals(1, result.getFieldsFilled());
        assertEquals("NoDelimitersHere", result.getParts().get(0));
        assertEquals("", result.getDelimiters().get(0));
    }

    @Test
    void testUnstringFullMaxFields() {
        String source = "A|B|C|D|E";
        String[] delimiters = {"|"};

        // Limit to 3 fields
        UnstringResult result = StringUtils.unstringFull(source, delimiters, 1, 3);

        assertEquals(3, result.getFieldsFilled());
        assertEquals(3, result.getParts().size());
        assertEquals("A", result.getParts().get(0));
        assertEquals("B", result.getParts().get(1));
        assertEquals("C", result.getParts().get(2));
    }
}
