package com.cobolmigration.util;

/**
 * Numeric utility methods migrated from COBOL intrinsic functions.
 *
 * Equivalent of COBOL IS NUMERIC test (from is_numeric/is_numeric.cbl)
 * and FUNCTION NUMVAL (from numval_test/numval_test.cbl).
 */
public final class NumericUtils {

    private NumericUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Tests whether a string represents a numeric value.
     * Equivalent to COBOL "IS NUMERIC" test from is_numeric/is_numeric.cbl.
     *
     * COBOL behavior notes (from is_numeric.cbl):
     * - An alphanumeric value with trailing spaces is NOT numeric in COBOL
     * - After TRIM, a contiguous sequence of digits IS numeric
     * - Right-justified and zero-filled values ARE numeric
     *
     * This implementation checks if the trimmed input can be parsed as a number.
     * It accepts: integers, decimals, and optionally signed numbers.
     *
     * @param input the string to test
     * @return true if the string represents a numeric value
     */
    public static boolean isNumeric(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return false;
        }

        // Match COBOL IS NUMERIC: digits only (no signs, no decimal points)
        // for strict COBOL compatibility with PIC X fields
        return trimmed.matches("\\d+");
    }

    /**
     * Extended numeric check that also accepts decimal points and signs.
     * More permissive than strict COBOL IS NUMERIC, closer to NUMVAL behavior.
     *
     * @param input the string to test
     * @return true if the string represents any numeric format
     */
    public static boolean isNumericExtended(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return false;
        }

        // Accept optional leading/trailing signs, digits, and one decimal point
        return trimmed.matches("[+-]?\\d*\\.?\\d+([eE][+-]?\\d+)?");
    }

    /**
     * Converts a COBOL-formatted numeric string to a double value.
     * Equivalent to COBOL FUNCTION NUMVAL from numval_test/numval_test.cbl.
     *
     * COBOL NUMVAL behavior:
     * - Strips leading and trailing spaces
     * - Handles optional leading or trailing sign (+ or -)
     * - Handles optional decimal point
     * - Returns a numeric (double) value
     *
     * From numval_test.cbl:
     *   COMPUTE ws-total = FUNCTION NUMVAL(ws-x-val) + ws-9-val
     *
     * @param input the COBOL-formatted numeric string
     * @return the parsed double value
     * @throws NumberFormatException if the input cannot be parsed
     */
    public static double numval(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new NumberFormatException("Cannot parse null or empty string");
        }

        String trimmed = input.trim();

        // Handle COBOL-style trailing sign: "123-" -> "-123"
        if (trimmed.endsWith("-")) {
            trimmed = "-" + trimmed.substring(0, trimmed.length() - 1).trim();
        } else if (trimmed.endsWith("+")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1).trim();
        }

        // Handle COBOL-style leading sign with spaces: "+ 123" or "- 123"
        if (trimmed.startsWith("+") || trimmed.startsWith("-")) {
            String sign = trimmed.substring(0, 1);
            String rest = trimmed.substring(1).trim();
            trimmed = sign + rest;
        }

        // Remove any commas (COBOL numeric editing)
        trimmed = trimmed.replace(",", "");

        // Remove currency symbols
        trimmed = trimmed.replace("$", "");

        return Double.parseDouble(trimmed);
    }

    /**
     * Converts a COBOL-formatted numeric string to a long value.
     * Similar to numval but returns an integer type.
     *
     * @param input the COBOL-formatted numeric string
     * @return the parsed long value
     * @throws NumberFormatException if the input cannot be parsed
     */
    public static long numvalInt(String input) {
        double value = numval(input);
        return Math.round(value);
    }
}
