package com.cobolexamples.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class PicTest {

    @Test
    void xPadsAndTruncates() {
        assertEquals("ab   ", Pic.x("ab", 5));
        assertEquals("abcde", Pic.x("abcdef", 5));
        assertEquals("     ", Pic.x("", 5));
    }

    @Test
    void nineZeroFillsAndTruncates() {
        assertEquals("007", Pic.nine(7, 3));
        assertEquals("345", Pic.nine(12345, 3));
    }

    @Test
    void zZeroSuppresses() {
        assertEquals("  7", Pic.z(7, 3));
        assertEquals("123", Pic.z(123, 3));
    }

    @Test
    void justifiedRightShifts() {
        assertEquals("       abc", Pic.justifiedRight("abc", 10));
        assertEquals("cdef", Pic.justifiedRight("abcdef", 4));
    }

    @Test
    void isNumericRules() {
        assertTrue(Pic.isNumeric("123"));
        assertFalse(Pic.isNumeric("12 "));
        assertFalse(Pic.isNumeric("+12"));
        assertFalse(Pic.isNumeric(""));
        assertFalse(Pic.isNumeric("12a"));
    }

    @Test
    void numvalParses() {
        assertEquals(new BigDecimal("12.5"), Pic.numval("  12.5  "));
        assertEquals(new BigDecimal("-3"), Pic.numval("-3"));
        assertEquals(BigDecimal.ZERO, Pic.numval("   "));
    }

    @Test
    void trimVariants() {
        assertEquals("abc", Pic.trim("  abc  "));
        assertEquals("abc  ", Pic.trimLeading("  abc  "));
        assertEquals("  abc", Pic.trimTrailing("  abc  "));
    }

    @Test
    void zeroFillTransform() {
        String v = Pic.justifiedRight("42", 10).replace(' ', '0');
        assertEquals("0000000042", v);
        assertTrue(Pic.isNumeric(v));
    }
}
