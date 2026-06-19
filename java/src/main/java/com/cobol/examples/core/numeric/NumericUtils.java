package com.cobol.examples.core.numeric;

import java.math.BigDecimal;

/**
 * Pure-logic port of the numeric examples: {@code numval_test/numval_test.cbl},
 * {@code is_numeric/is_numeric.cbl} and {@code comp_test/comp_test.cbl}.
 *
 * <p>COBOL packed/binary ({@code COMP}, {@code COMP-3}) fields and the
 * {@code NUMVAL} intrinsic are all represented with {@link BigDecimal} to keep
 * fixed-point precision exactly as the mainframe code intended.
 */
public final class NumericUtils {

    private NumericUtils() {
    }

    /**
     * Equivalent of {@code FUNCTION NUMVAL}. Strips currency symbols, grouping
     * commas and surrounding whitespace, then parses the remaining numeric text.
     */
    public static BigDecimal numval(String text) {
        if (text == null) {
            return BigDecimal.ZERO;
        }
        String cleaned = text.replace(",", "").replace("$", "").trim();
        if (cleaned.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(cleaned);
    }

    /**
     * Equivalent of {@code IF ws-user-input IS NUMERIC} on a plain alphanumeric
     * field: any embedded or trailing space makes the test fail.
     */
    public static boolean isNumericPlain(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Equivalent of right-justifying into a fixed width and replacing leading
     * spaces with {@code '0'} before the numeric test.
     */
    public static boolean isNumericZeroFilled(String value, int width) {
        if (value == null) {
            return false;
        }
        String trimmed = value.trim();
        if (trimmed.length() > width) {
            return false;
        }
        String padded = "0".repeat(width - trimmed.length()) + trimmed;
        return isNumericPlain(padded);
    }

    /** Equivalent of {@code IF FUNCTION TRIM(ws-user-input) IS NUMERIC}. */
    public static boolean isNumericTrimmed(String value) {
        return value != null && isNumericPlain(value.trim());
    }

    /** Equivalent of {@code COMPUTE ws-total = FUNCTION NUMVAL(x) + y}. */
    public static BigDecimal sum(String first, String second) {
        return numval(first).add(numval(second));
    }
}
