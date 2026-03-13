package com.cobolmigration.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NumericUtils.
 * Verifies behavior matches COBOL IS NUMERIC and NUMVAL operations.
 */
class NumericUtilsTest {

    // --- IS NUMERIC tests (from is_numeric/is_numeric.cbl) ---

    @Test
    void isNumeric_shouldReturnTrueForAllDigits() {
        assertTrue(NumericUtils.isNumeric("12345"));
        assertTrue(NumericUtils.isNumeric("0000000001"));
    }

    @Test
    void isNumeric_shouldReturnFalseForSpaces() {
        // COBOL: PIC X(10) with "123" has trailing spaces -> IS NUMERIC returns false
        assertFalse(NumericUtils.isNumeric("123       "));
    }

    @Test
    void isNumeric_shouldReturnFalseForLetters() {
        assertFalse(NumericUtils.isNumeric("abc"));
        assertFalse(NumericUtils.isNumeric("12a45"));
    }

    @Test
    void isNumeric_shouldReturnFalseForEmpty() {
        assertFalse(NumericUtils.isNumeric(""));
        assertFalse(NumericUtils.isNumeric(null));
    }

    @Test
    void isNumericTrimmed_shouldReturnTrueAfterTrimming() {
        // COBOL: FUNCTION TRIM(ws-user-input) IS NUMERIC
        assertTrue(NumericUtils.isNumericTrimmed("  123  "));
        assertTrue(NumericUtils.isNumericTrimmed("456"));
    }

    @Test
    void isNumericTrimmed_shouldReturnFalseForNonNumeric() {
        assertFalse(NumericUtils.isNumericTrimmed("  12a  "));
    }

    @Test
    void isNumericZeroFilled_shouldReturnTrueForValidNumbers() {
        // COBOL: JUSTIFIED RIGHT, INSPECT REPLACING LEADING SPACES BY '0'
        assertTrue(NumericUtils.isNumericZeroFilled("123", 10));
        assertTrue(NumericUtils.isNumericZeroFilled("  456  ", 10));
    }

    @Test
    void isNumericZeroFilled_shouldReturnFalseForNonNumeric() {
        assertFalse(NumericUtils.isNumericZeroFilled("abc", 10));
    }

    // --- NUMVAL tests (from numval_test/numval_test.cbl) ---

    @Test
    void numval_shouldConvertSimpleNumber() {
        // COBOL: FUNCTION NUMVAL(ws-x-val) where ws-x-val = "100"
        assertEquals(new BigDecimal("100"), NumericUtils.numval("100"));
    }

    @Test
    void numval_shouldHandleLeadingTrailingSpaces() {
        assertEquals(new BigDecimal("42"), NumericUtils.numval("  42  "));
    }

    @Test
    void numval_shouldHandleDecimalPoint() {
        assertEquals(new BigDecimal("123.45"), NumericUtils.numval("123.45"));
    }

    @Test
    void numval_shouldHandleNegativeNumbers() {
        assertEquals(new BigDecimal("-67.89"), NumericUtils.numval("-67.89"));
    }

    @Test
    void numval_shouldThrowForBlankInput() {
        assertThrows(NumberFormatException.class, () -> NumericUtils.numval("   "));
        assertThrows(NumberFormatException.class, () -> NumericUtils.numval(null));
    }

    @Test
    void numvalC_shouldHandleCurrencyFormat() {
        // COBOL: NUMVAL-C for currency-formatted strings
        assertEquals(new BigDecimal("1234.56"), NumericUtils.numvalC("$1,234.56"));
    }

    @Test
    void numvalC_shouldHandleNoCurrencySymbol() {
        assertEquals(new BigDecimal("1000"), NumericUtils.numvalC("1,000"));
    }

    @Test
    void computeAdd_shouldUseExactDecimalArithmetic() {
        // COBOL: COMPUTE ws-total = FUNCTION NUMVAL(ws-x-val) + ws-9-val
        // Verifies BigDecimal precision matches COBOL's exact arithmetic
        BigDecimal result = NumericUtils.computeAdd("100", "200");
        assertEquals(new BigDecimal("300"), result);
    }

    @Test
    void computeAdd_shouldHandleDecimalPrecision() {
        // Verify no floating-point precision issues
        BigDecimal result = NumericUtils.computeAdd("0.1", "0.2");
        assertEquals(new BigDecimal("0.3"), result);
    }
}
