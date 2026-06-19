package com.cobol.examples.core.numeric;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class NumericUtilsTest {

    @Test
    void numvalStripsFormatting() {
        assertEquals(new BigDecimal("1234.56"), NumericUtils.numval("$1,234.56"));
        assertEquals(BigDecimal.ZERO, NumericUtils.numval("   "));
    }

    @Test
    void sumAddsTwoFormattedNumbers() {
        assertEquals(new BigDecimal("1244.56"), NumericUtils.sum("$1,234.56", "10"));
    }

    @Test
    void plainNumericRejectsSpaces() {
        assertTrue(NumericUtils.isNumericPlain("12345"));
        assertFalse(NumericUtils.isNumericPlain("123 45"));
        assertFalse(NumericUtils.isNumericPlain("123  "));
    }

    @Test
    void zeroFilledAcceptsRightJustifiedDigits() {
        assertTrue(NumericUtils.isNumericZeroFilled("123", 10));
        assertFalse(NumericUtils.isNumericZeroFilled("12a", 10));
    }

    @Test
    void trimmedAcceptsSurroundingSpaces() {
        assertTrue(NumericUtils.isNumericTrimmed("  789  "));
        assertFalse(NumericUtils.isNumericTrimmed("  7 9  "));
    }
}
