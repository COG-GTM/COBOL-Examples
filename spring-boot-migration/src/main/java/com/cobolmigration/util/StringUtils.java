package com.cobolmigration.util;

import java.math.BigDecimal;

/**
 * String utility methods replacing COBOL intrinsic functions and statements.
 * Covers: TRIM (trim/), UNSTRING (unstring/), IS NUMERIC (is_numeric/), NUMVAL (numval_test/).
 */
public final class StringUtils {

    private StringUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Trims leading and trailing whitespace from a string.
     * Replaces the COBOL TRIM function demonstrated in trim/trim.cbl.
     * COBOL TRIM removes spaces; Java's strip() removes all Unicode whitespace.
     *
     * @param value the string to trim
     * @return trimmed string, or empty string if input is null
     */
    public static String trim(String value) {
        if (value == null) {
            return "";
        }
        return value.strip();
    }

    /**
     * Splits a source string by a delimiter, similar to COBOL UNSTRING.
     * Replaces the COBOL UNSTRING statement demonstrated in unstring/unstring.cbl.
     *
     * In COBOL, UNSTRING splits a source field into multiple destination fields
     * based on a delimiter. This method provides the same capability using
     * Java's String.split().
     *
     * @param source    the source string to split
     * @param delimiter the delimiter to split on (supports regex)
     * @return array of split parts, or empty array if source is null
     */
    public static String[] unstring(String source, String delimiter) {
        if (source == null || delimiter == null) {
            return new String[0];
        }
        return source.split(java.util.regex.Pattern.quote(delimiter), -1);
    }

    /**
     * Checks if a string value represents a numeric value.
     * Replaces the COBOL IS NUMERIC test demonstrated in is_numeric/is_numeric.cbl.
     *
     * In COBOL, IS NUMERIC checks if an alphanumeric field contains only digits.
     * Spaces cause the test to fail in COBOL (lines 32-36). After TRIM, contiguous
     * digits pass the test (lines 71-75). This implementation trims before checking.
     *
     * @param value the string to check
     * @return true if the trimmed value is numeric, false otherwise
     */
    public static boolean isNumeric(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String trimmed = value.strip();
        try {
            new BigDecimal(trimmed);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Converts a string representation of a number to a BigDecimal.
     * Replaces the COBOL NUMVAL function demonstrated in numval_test/numval_test.cbl.
     *
     * COBOL NUMVAL converts a PIC X field to a numeric value for arithmetic
     * (numval_test.cbl line 28: compute ws-total = function numval(ws-x-val) + ws-9-val).
     *
     * @param value the string to convert
     * @return BigDecimal representation of the value
     * @throws NumberFormatException if the value cannot be parsed as a number
     */
    public static BigDecimal numericValue(String value) {
        if (value == null || value.isBlank()) {
            throw new NumberFormatException("Cannot convert null or blank value to numeric");
        }
        String cleaned = value.strip().replaceAll("[,$]", "");
        return new BigDecimal(cleaned);
    }
}
