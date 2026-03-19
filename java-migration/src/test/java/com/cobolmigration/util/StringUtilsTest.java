package com.cobolmigration.util;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for StringUtils validating replacements for COBOL TRIM, UNSTRING,
 * IS NUMERIC, and NUMVAL operations.
 */
class StringUtilsTest {

    // --- TRIM tests (from trim/trim.cbl) ---

    @Test
    @DisplayName("trim removes both leading and trailing spaces")
    void testTrim() {
        assertThat(StringUtils.trim("    hello world       ")).isEqualTo("hello world");
    }

    @Test
    @DisplayName("trimLeading removes only leading spaces")
    void testTrimLeading() {
        assertThat(StringUtils.trimLeading("    hello world       "))
                .isEqualTo("hello world       ");
    }

    @Test
    @DisplayName("trimTrailing removes only trailing spaces")
    void testTrimTrailing() {
        assertThat(StringUtils.trimTrailing("    hello world       "))
                .isEqualTo("    hello world");
    }

    @Test
    @DisplayName("trim handles null input")
    void testTrimNull() {
        assertThat(StringUtils.trim(null)).isEqualTo("");
    }

    @Test
    @DisplayName("trim handles string literal with spaces")
    void testTrimStringLiteral() {
        assertThat(StringUtils.trim("   String literal    ")).isEqualTo("String literal");
    }

    // --- UNSTRING tests (from unstring/unstring.cbl) ---

    @Test
    @DisplayName("unstring splits by single space delimiter")
    void testUnstringSingleDelimiter() {
        List<String> parts = StringUtils.unstring("Hello World", " ");
        assertThat(parts).containsExactly("Hello", "World");
    }

    @Test
    @DisplayName("unstring splits by multiple delimiters")
    void testUnstringMultipleDelimiters() {
        List<String> parts = StringUtils.unstring("A<B>C!D|E", "<", ">", "!", "|");
        assertThat(parts).containsExactly("A", "B", "C", "D", "E");
    }

    @Test
    @DisplayName("unstring handles source with no delimiters")
    void testUnstringNoDelimiters() {
        List<String> parts = StringUtils.unstring("Hello");
        assertThat(parts).containsExactly("Hello");
    }

    @Test
    @DisplayName("unstring handles null source")
    void testUnstringNull() {
        List<String> parts = StringUtils.unstring(null, " ");
        assertThat(parts).isEmpty();
    }

    @Test
    @DisplayName("unstring with comma and dot delimiters for formatted numbers")
    void testUnstringFormattedNumber() {
        // Mirrors unstring.cbl Example 6: unstring formatted number
        List<String> parts = StringUtils.unstring("123,456.12", ",", ".");
        assertThat(parts).containsExactly("123", "456", "12");
    }

    // --- IS NUMERIC tests (from is_numeric/is_numeric.cbl) ---

    @Test
    @DisplayName("isNumeric returns true for digit-only string")
    void testIsNumericDigits() {
        assertThat(StringUtils.isNumeric("12345")).isTrue();
    }

    @Test
    @DisplayName("isNumeric returns true for decimal number")
    void testIsNumericDecimal() {
        assertThat(StringUtils.isNumeric("123.45")).isTrue();
    }

    @Test
    @DisplayName("isNumeric returns true for negative number")
    void testIsNumericNegative() {
        assertThat(StringUtils.isNumeric("-42")).isTrue();
    }

    @Test
    @DisplayName("isNumeric returns false for string with letters")
    void testIsNumericWithLetters() {
        assertThat(StringUtils.isNumeric("abc")).isFalse();
    }

    @Test
    @DisplayName("isNumeric returns false for empty/null input")
    void testIsNumericEmpty() {
        assertThat(StringUtils.isNumeric("")).isFalse();
        assertThat(StringUtils.isNumeric(null)).isFalse();
    }

    @Test
    @DisplayName("isNumeric handles trimmed value with spaces")
    void testIsNumericWithSpaces() {
        // Mirrors the is_numeric.cbl process-trim paragraph
        assertThat(StringUtils.isNumeric("  123  ")).isTrue();
    }

    // --- NUMVAL tests (from numval_test/numval_test.cbl) ---

    @Test
    @DisplayName("numval parses string to BigDecimal")
    void testNumval() {
        BigDecimal result = StringUtils.numval("42");
        assertThat(result).isEqualByComparingTo(new BigDecimal("42"));
    }

    @Test
    @DisplayName("numval handles decimal values")
    void testNumvalDecimal() {
        BigDecimal result = StringUtils.numval("123.45");
        assertThat(result).isEqualByComparingTo(new BigDecimal("123.45"));
    }

    @Test
    @DisplayName("numval trims spaces before parsing")
    void testNumvalWithSpaces() {
        BigDecimal result = StringUtils.numval("  100  ");
        assertThat(result).isEqualByComparingTo(new BigDecimal("100"));
    }

    @Test
    @DisplayName("numval throws exception for non-numeric input")
    void testNumvalInvalid() {
        assertThatThrownBy(() -> StringUtils.numval("abc"))
                .isInstanceOf(NumberFormatException.class);
    }

    // --- padRight tests ---

    @Test
    @DisplayName("padRight pads to specified width")
    void testPadRight() {
        assertThat(StringUtils.padRight("Hello", 10)).isEqualTo("Hello     ");
    }

    @Test
    @DisplayName("padRight truncates if longer than width")
    void testPadRightTruncate() {
        assertThat(StringUtils.padRight("Hello World", 5)).isEqualTo("Hello");
    }
}
