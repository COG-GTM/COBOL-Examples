package com.migration.stringutils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Phase 1: String Utilities Tests
 *
 * Test cases mirror the COBOL examples from:
 * - trim/trim.cbl
 * - unstring/unstring.cbl
 * - is_numeric/is_numeric.cbl
 * - numval_test/numval_test.cbl
 */
class StringUtilsTest {

    @Nested
    @DisplayName("Trim Tests (COBOL: FUNCTION TRIM)")
    class TrimTests {

        @Test
        @DisplayName("TRIM both - removes leading and trailing spaces")
        void trimBoth() {
            // COBOL: ws-test-string-1 PIC X(30) VALUE "    hello world       "
            String input = "    hello world       ";
            assertEquals("hello world", StringUtils.trim(input));
        }

        @Test
        @DisplayName("TRIM LEADING - removes only leading spaces")
        void trimLeading() {
            String input = "    hello world       ";
            assertEquals("hello world       ", StringUtils.trimLeading(input));
        }

        @Test
        @DisplayName("TRIM TRAILING - removes only trailing spaces")
        void trimTrailing() {
            String input = "    hello world       ";
            assertEquals("    hello world", StringUtils.trimTrailing(input));
        }

        @Test
        @DisplayName("TRIM string literal")
        void trimLiteral() {
            assertEquals("String literal", StringUtils.trim("   String literal    "));
            assertEquals("String literal    ", StringUtils.trimLeading("   String literal    "));
            assertEquals("   String literal", StringUtils.trimTrailing("   String literal    "));
        }

        @Test
        @DisplayName("TRIM null returns null")
        void trimNull() {
            assertNull(StringUtils.trim(null));
            assertNull(StringUtils.trimLeading(null));
            assertNull(StringUtils.trimTrailing(null));
        }

        @Test
        @DisplayName("TRIM empty string returns empty")
        void trimEmpty() {
            assertEquals("", StringUtils.trim(""));
            assertEquals("", StringUtils.trim("   "));
        }
    }

    @Nested
    @DisplayName("Unstring Tests (COBOL: UNSTRING ... DELIMITED BY)")
    class UnstringTests {

        @Test
        @DisplayName("EX 1: Simple unstring by space")
        void simpleUnstring() {
            // COBOL: UNSTRING "Hello World" DELIMITED BY SPACE INTO ws-part-1 ws-part-2
            String[] parts = StringUtils.unstring("Hello World", " ");
            assertEquals("Hello", parts[0]);
            assertEquals("World", parts[1]);
        }

        @Test
        @DisplayName("EX 2: Unstring with ALL spaces (consecutive delimiters)")
        void unstringAllSpaces() {
            // COBOL: UNSTRING ... DELIMITED BY ALL SPACES
            String[] parts = StringUtils.unstringAll("Hello World", " ");
            assertEquals("Hello", parts[0]);
            assertEquals("World", parts[1]);
        }

        @Test
        @DisplayName("EX 4: Unstring with multiple delimiters and statistics")
        void unstringMultipleDelimiters() {
            // COBOL: UNSTRING "A<B<CD>E%FG!HIJ|KL!MN>OP#QR!ST"
            //        DELIMITED BY ALL "<" OR ">" OR "!" OR "|"
            String source = "A<B<CD>E%FG!HIJ|KL!MN>OP#QR!ST";
            StringUtils.UnstringResult result =
                    StringUtils.unstringMultipleDelimiters(source, "<", ">", "!", "|");

            // Verify parts
            assertEquals("A", result.parts()[0]);
            assertEquals("B", result.parts()[1]);
            assertEquals("CD", result.parts()[2]);

            // Verify delimiters found
            assertEquals("<", result.delimitersFound()[0]);
            assertEquals("<", result.delimitersFound()[1]);
            assertEquals(">", result.delimitersFound()[2]);

            // Verify char counts
            assertEquals(1, result.charCounts()[0]); // "A" = 1 char
            assertEquals(1, result.charCounts()[1]); // "B" = 1 char
            assertEquals(2, result.charCounts()[2]); // "CD" = 2 chars

            // Verify total fields filled
            assertTrue(result.fieldsFilled() > 0);
        }

        @Test
        @DisplayName("EX 5: Unstring with multiple delimiters into multiple destinations")
        void unstringMultipleDelimitersMultipleDest() {
            // COBOL: "A<B<CD>EFG!HIJ|KLMN>O"
            String source = "A<B<CD>EFG!HIJ|KLMN>O";
            StringUtils.UnstringResult result =
                    StringUtils.unstringMultipleDelimiters(source, "<", ">", "!", "|");

            // "A<B<CD>EFG!HIJ|KLMN>O" splits into 7 parts:
            // A, B, CD, EFG, HIJ, KLMN, O
            assertEquals(7, result.fieldsFilled());
            assertEquals("A", result.parts()[0]);
            assertEquals("B", result.parts()[1]);
            assertEquals("CD", result.parts()[2]);
            assertEquals("EFG", result.parts()[3]);
            assertEquals("HIJ", result.parts()[4]);
            assertEquals("KLMN", result.parts()[5]);
            assertEquals("O", result.parts()[6]);
        }

        @Test
        @DisplayName("EX 6: Unstring formatted number")
        void unstringFormattedNumber() {
            // COBOL: ws-source-num PIC $999,999.99 value 123456.12
            // After formatting: $123,456.12
            // Unstring starting at position 2 (skip $), delimited by ',' or '.'
            String formatted = "$123,456.12";
            String withoutDollar = formatted.substring(1); // "123,456.12"
            String[] parts = StringUtils.unstring(withoutDollar, ",");
            assertEquals("123", parts[0]);

            // Full split by , and .
            StringUtils.UnstringResult result =
                    StringUtils.unstringMultipleDelimiters(withoutDollar, ",", ".");
            assertEquals("123", result.parts()[0]);
            assertEquals("456", result.parts()[1]);
            assertEquals("12", result.parts()[2]);
        }

        @Test
        @DisplayName("Unstring null returns empty array")
        void unstringNull() {
            assertEquals(0, StringUtils.unstring(null, ",").length);
        }
    }

    @Nested
    @DisplayName("IsNumeric Tests (COBOL: IS NUMERIC)")
    class IsNumericTests {

        @Test
        @DisplayName("Plain numeric check - digits only")
        void plainNumeric() {
            assertTrue(StringUtils.isNumeric("12345"));
            assertTrue(StringUtils.isNumeric("0"));
            assertTrue(StringUtils.isNumeric("-123"));
            assertTrue(StringUtils.isNumeric("123.45"));
        }

        @Test
        @DisplayName("Plain numeric with spaces fails (matches COBOL behavior)")
        void numericWithSpaces() {
            // In COBOL, PIC X(10) with trailing spaces is NOT numeric
            assertFalse(StringUtils.isNumeric("123   "));
            assertFalse(StringUtils.isNumeric("  123"));
        }

        @Test
        @DisplayName("Trimmed numeric check passes")
        void trimmedNumeric() {
            // COBOL: IF FUNCTION TRIM(ws-user-input) IS NUMERIC
            assertTrue(StringUtils.isNumericTrimmed("  123  "));
            assertTrue(StringUtils.isNumericTrimmed("  456.78  "));
        }

        @Test
        @DisplayName("Non-numeric values")
        void nonNumeric() {
            assertFalse(StringUtils.isNumeric("abc"));
            assertFalse(StringUtils.isNumeric("12a34"));
            assertFalse(StringUtils.isNumeric(""));
            assertFalse(StringUtils.isNumeric(null));
        }

        @Test
        @DisplayName("Zero-filled numeric check")
        void zeroFilledNumeric() {
            // COBOL: right justify, replace leading spaces with '0', then IS NUMERIC
            assertTrue(StringUtils.isNumericZeroFilled("123", 10));
            assertTrue(StringUtils.isNumericZeroFilled("  456  ", 10));
        }
    }

    @Nested
    @DisplayName("Numval Tests (COBOL: FUNCTION NUMVAL)")
    class NumvalTests {

        @Test
        @DisplayName("Basic numval conversion")
        void basicNumval() {
            // COBOL: COMPUTE ws-total = FUNCTION NUMVAL(ws-x-val) + ws-9-val
            assertEquals(123.0, StringUtils.numval("123"));
            assertEquals(456.78, StringUtils.numval("456.78"));
        }

        @Test
        @DisplayName("Numval with leading/trailing spaces")
        void numvalWithSpaces() {
            // COBOL NUMVAL strips spaces automatically
            assertEquals(42.0, StringUtils.numval("  42  "));
            assertEquals(100.5, StringUtils.numval("  100.5  "));
        }

        @Test
        @DisplayName("Numval with trailing sign (COBOL convention)")
        void numvalTrailingSign() {
            assertEquals(-123.0, StringUtils.numval("123-"));
            assertEquals(456.0, StringUtils.numval("456+"));
        }

        @Test
        @DisplayName("Numval with negative number")
        void numvalNegative() {
            assertEquals(-99.9, StringUtils.numval("-99.9"));
        }

        @Test
        @DisplayName("Numval null throws exception")
        void numvalNull() {
            assertThrows(NumberFormatException.class, () -> StringUtils.numval(null));
        }

        @Test
        @DisplayName("Numval empty string throws exception")
        void numvalEmpty() {
            assertThrows(NumberFormatException.class, () -> StringUtils.numval(""));
            assertThrows(NumberFormatException.class, () -> StringUtils.numval("   "));
        }

        @Test
        @DisplayName("Numval addition mirrors COBOL compute")
        void numvalCompute() {
            // COBOL: COMPUTE ws-total = FUNCTION NUMVAL("10") + 20
            double total = StringUtils.numval("10") + 20;
            assertEquals(30.0, total);
        }
    }
}
