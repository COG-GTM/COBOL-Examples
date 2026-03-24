package com.cobolmigration.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for NumericUtils verifying behavior matching:
 *   comp_test/comp_test.cbl and numval_test/numval_test.cbl
 */
class NumericUtilsTest {

    @Test
    void numval_shouldConvertSimpleNumber() {
        assertEquals(new BigDecimal("123"), NumericUtils.numval("123"));
    }

    @Test
    void numval_shouldHandleLeadingTrailingSpaces() {
        assertEquals(new BigDecimal("456"), NumericUtils.numval("  456  "));
    }

    @Test
    void numval_shouldHandleDecimalNumbers() {
        assertEquals(new BigDecimal("123.45"), NumericUtils.numval("123.45"));
    }

    @Test
    void numval_shouldHandleNegativeNumbers() {
        assertEquals(new BigDecimal("-99"), NumericUtils.numval("-99"));
    }

    @Test
    void numval_shouldReturnZeroForBlank() {
        assertEquals(BigDecimal.ZERO, NumericUtils.numval("   "));
    }

    @Test
    void numval_shouldReturnZeroForNull() {
        assertEquals(BigDecimal.ZERO, NumericUtils.numval(null));
    }

    @Test
    void numval_shouldThrowForNonNumeric() {
        assertThrows(IllegalArgumentException.class, () -> NumericUtils.numval("abc"));
    }

    @Test
    void formatDisplay_shouldPadWithZeros() {
        assertEquals("024", NumericUtils.formatDisplay(24, 3));
        assertEquals("00012", NumericUtils.formatDisplay(12, 5));
    }

    @Test
    void formatSuppressed_shouldPadWithSpaces() {
        assertEquals(" 24", NumericUtils.formatSuppressed(24, 3));
        assertEquals("   12", NumericUtils.formatSuppressed(12, 5));
    }

    @Test
    void compToDisplay_shouldReturnSameValue() {
        // COMP is binary in COBOL, int in Java - direct mapping
        assertEquals(24, NumericUtils.compToDisplay(24));
    }

    @Test
    void comp2Value_shouldReturnSameDouble() {
        // COMP-2 is IEEE 754 double in both COBOL and Java
        assertEquals(12345.63, NumericUtils.comp2Value(12345.63), 0.001);
    }

    @Test
    void comp3Value_shouldConvertString() {
        assertEquals(new BigDecimal("123"), NumericUtils.comp3Value("123"));
    }
}
