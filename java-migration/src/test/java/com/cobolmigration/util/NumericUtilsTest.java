package com.cobolmigration.util;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for NumericUtils validating replacements for COBOL COMP/COMP-3 numeric handling.
 *
 * @see comp_test/comp_test.cbl
 */
class NumericUtilsTest {

    @Test
    @DisplayName("fromComp converts integer to BigDecimal (COMP PIC 999)")
    void testFromComp() {
        BigDecimal result = NumericUtils.fromComp(24);
        assertThat(result).isEqualByComparingTo(new BigDecimal("24"));
    }

    @Test
    @DisplayName("fromComp2 converts double to BigDecimal (COMP-2)")
    void testFromComp2() {
        BigDecimal result = NumericUtils.fromComp2(123.456);
        assertThat(result).isEqualByComparingTo(new BigDecimal("123.456"));
    }

    @Test
    @DisplayName("fromComp3 parses string with scale (COMP-3)")
    void testFromComp3() {
        BigDecimal result = NumericUtils.fromComp3("12345", 2);
        assertThat(result).isEqualByComparingTo(new BigDecimal("12345.00"));
        assertThat(result.scale()).isEqualTo(2);
    }

    @Test
    @DisplayName("toDisplay formats to zero-padded string (PIC 999)")
    void testToDisplay() {
        String result = NumericUtils.toDisplay(new BigDecimal("24"), 3);
        assertThat(result).isEqualTo("024");
    }

    @Test
    @DisplayName("toDynamicDisplay formats with leading spaces (PIC ZZ9)")
    void testToDynamicDisplay() {
        String result = NumericUtils.toDynamicDisplay(new BigDecimal("24"), 3);
        assertThat(result).isEqualTo(" 24");
    }

    @Test
    @DisplayName("multiply performs COBOL MULTIPLY...GIVING operation")
    void testMultiply() {
        // Mirrors comp_test.cbl: MOVE 12 TO ws-comp-val, MULTIPLY ws-comp-val BY 2
        BigDecimal result = NumericUtils.multiply(new BigDecimal("12"), new BigDecimal("2"));
        assertThat(result).isEqualByComparingTo(new BigDecimal("24"));
    }

    @Test
    @DisplayName("add performs COBOL ADD/COMPUTE operation")
    void testAdd() {
        BigDecimal result = NumericUtils.add(new BigDecimal("100"), new BigDecimal("200"));
        assertThat(result).isEqualByComparingTo(new BigDecimal("300"));
    }

    @Test
    @DisplayName("toDisplay handles null value")
    void testToDisplayNull() {
        String result = NumericUtils.toDisplay(null, 3);
        assertThat(result).isEqualTo("000");
    }

    @Test
    @DisplayName("toDynamicDisplay handles null value")
    void testToDynamicDisplayNull() {
        String result = NumericUtils.toDynamicDisplay(null, 3);
        assertThat(result).isEqualTo("  0");
    }
}
