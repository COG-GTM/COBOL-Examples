package com.cobolmigration.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NumericUtils.
 * Verifies isNumeric and numval handle COBOL-style numeric formats
 * from is_numeric/is_numeric.cbl and numval_test/numval_test.cbl.
 */
class NumericUtilsTest {

    // ===== IS NUMERIC TESTS =====
    // Based on is_numeric/is_numeric.cbl behavior

    @Test
    void testIsNumericWithDigits() {
        // Trimmed contiguous digits are numeric
        assertTrue(NumericUtils.isNumeric("12345"));
        assertTrue(NumericUtils.isNumeric("0"));
        assertTrue(NumericUtils.isNumeric("999999999"));
    }

    @Test
    void testIsNumericWithSpaces() {
        // Our isNumeric trims input first, matching COBOL FUNCTION TRIM(input) IS NUMERIC.
        // So "123   " becomes "123" which IS numeric.
        // Note: raw COBOL IS NUMERIC on a PIC X field with trailing spaces would be false,
        // but our utility includes trim for convenience.
        assertTrue(NumericUtils.isNumeric("123   "));
        assertTrue(NumericUtils.isNumeric("   123"));
        assertTrue(NumericUtils.isNumeric("  123  "));
        assertTrue(NumericUtils.isNumeric("123"));
    }

    @Test
    void testIsNumericWithTrimmedInput() {
        // COBOL: FUNCTION TRIM(ws-user-input) IS NUMERIC
        // The trim removes spaces, then the digits-only check applies
        String input = "  456  ";
        assertTrue(NumericUtils.isNumeric(input.trim()));
    }

    @Test
    void testIsNumericWithAlpha() {
        assertFalse(NumericUtils.isNumeric("ABC"));
        assertFalse(NumericUtils.isNumeric("12A34"));
        assertFalse(NumericUtils.isNumeric("hello"));
    }

    @Test
    void testIsNumericWithDecimalPoint() {
        // Strict COBOL IS NUMERIC: decimal point makes it NOT numeric for PIC X
        assertFalse(NumericUtils.isNumeric("123.45"));
    }

    @Test
    void testIsNumericWithSign() {
        // Strict COBOL IS NUMERIC: signs make it NOT numeric for PIC X
        assertFalse(NumericUtils.isNumeric("+123"));
        assertFalse(NumericUtils.isNumeric("-123"));
    }

    @Test
    void testIsNumericNull() {
        assertFalse(NumericUtils.isNumeric(null));
    }

    @Test
    void testIsNumericEmpty() {
        assertFalse(NumericUtils.isNumeric(""));
        assertFalse(NumericUtils.isNumeric("   "));
    }

    @Test
    void testIsNumericZeroFilled() {
        // COBOL: right-justified and zero-filled IS numeric
        // "0000000123" should be numeric
        assertTrue(NumericUtils.isNumeric("0000000123"));
    }

    // ===== IS NUMERIC EXTENDED TESTS =====

    @Test
    void testIsNumericExtendedWithDecimals() {
        assertTrue(NumericUtils.isNumericExtended("123.45"));
        assertTrue(NumericUtils.isNumericExtended(".5"));
        assertTrue(NumericUtils.isNumericExtended("0.0"));
    }

    @Test
    void testIsNumericExtendedWithSigns() {
        assertTrue(NumericUtils.isNumericExtended("+123"));
        assertTrue(NumericUtils.isNumericExtended("-456.78"));
    }

    @Test
    void testIsNumericExtendedWithScientific() {
        assertTrue(NumericUtils.isNumericExtended("1.5e10"));
        assertTrue(NumericUtils.isNumericExtended("-2.5E-3"));
    }

    // ===== NUMVAL TESTS =====
    // Based on numval_test/numval_test.cbl behavior

    @Test
    void testNumvalSimpleInteger() {
        // COBOL: FUNCTION NUMVAL(ws-x-val) where ws-x-val is "123"
        assertEquals(123.0, NumericUtils.numval("123"), 0.001);
    }

    @Test
    void testNumvalWithLeadingSpaces() {
        assertEquals(456.0, NumericUtils.numval("  456"), 0.001);
    }

    @Test
    void testNumvalWithTrailingSpaces() {
        assertEquals(789.0, NumericUtils.numval("789   "), 0.001);
    }

    @Test
    void testNumvalWithDecimal() {
        assertEquals(123.45, NumericUtils.numval("123.45"), 0.001);
    }

    @Test
    void testNumvalWithLeadingSign() {
        assertEquals(-123.0, NumericUtils.numval("-123"), 0.001);
        assertEquals(123.0, NumericUtils.numval("+123"), 0.001);
    }

    @Test
    void testNumvalWithTrailingSign() {
        // COBOL-style trailing sign
        assertEquals(-123.0, NumericUtils.numval("123-"), 0.001);
        assertEquals(123.0, NumericUtils.numval("123+"), 0.001);
    }

    @Test
    void testNumvalWithCommas() {
        // COBOL numeric editing with commas
        assertEquals(123456.0, NumericUtils.numval("123,456"), 0.001);
        assertEquals(1234567.89, NumericUtils.numval("1,234,567.89"), 0.001);
    }

    @Test
    void testNumvalWithCurrencySymbol() {
        assertEquals(123456.12, NumericUtils.numval("$123,456.12"), 0.001);
    }

    @Test
    void testNumvalWithSignAndSpaces() {
        // COBOL: "- 123" or "+ 456"
        assertEquals(-123.0, NumericUtils.numval("- 123"), 0.001);
        assertEquals(456.0, NumericUtils.numval("+ 456"), 0.001);
    }

    @Test
    void testNumvalNull() {
        assertThrows(NumberFormatException.class, () -> NumericUtils.numval(null));
    }

    @Test
    void testNumvalEmpty() {
        assertThrows(NumberFormatException.class, () -> NumericUtils.numval(""));
        assertThrows(NumberFormatException.class, () -> NumericUtils.numval("   "));
    }

    @Test
    void testNumvalInvalid() {
        assertThrows(NumberFormatException.class, () -> NumericUtils.numval("ABC"));
    }

    @Test
    void testNumvalInt() {
        assertEquals(123L, NumericUtils.numvalInt("123"));
        assertEquals(-456L, NumericUtils.numvalInt("-456"));
        assertEquals(124L, NumericUtils.numvalInt("123.6"));
    }

    @Test
    void testNumvalComputeEquivalent() {
        // COBOL: COMPUTE ws-total = FUNCTION NUMVAL(ws-x-val) + ws-9-val
        double val1 = NumericUtils.numval("100");
        long val2 = 200;
        double total = val1 + val2;
        assertEquals(300.0, total, 0.001);
    }
}
