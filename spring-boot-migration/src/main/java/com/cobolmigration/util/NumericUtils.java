package com.cobolmigration.util;

import java.math.BigDecimal;

/**
 * Numeric utility methods replacing COBOL numeric functions and type mappings.
 *
 * COBOL COMP type mappings to Java:
 *   COMP   (binary)    -> int or long (depending on PIC size)
 *   COMP-2 (IEEE 754)  -> double
 *   COMP-3 (packed BCD) -> BigDecimal
 *   COMP-5 (native binary) -> int or long
 *
 * From comp_test/comp_test.cbl:
 *   01 ws-comp-val    pic 999 comp.    -> int (binary representation)
 *   01 ws-disp-val    pic 999.         -> int (display representation)
 *   01 ws-dyn-disp-val pic zz9.        -> formatted string with leading spaces
 *
 * From numval_test/numval_test.cbl:
 *   FUNCTION NUMVAL(ws-x-val)          -> converts PIC X to numeric
 */
public final class NumericUtils {

    private NumericUtils() {
    }

    /**
     * Convert a COBOL-style numeric string to BigDecimal.
     * Replaces: FUNCTION NUMVAL(ws-x-val) from numval_test/numval_test.cbl
     *
     * NUMVAL handles:
     *   - Leading/trailing spaces
     *   - Leading/trailing signs (+/-)
     *   - Embedded spaces within the number
     */
    public static BigDecimal numval(String cobolNumericString) {
        if (cobolNumericString == null || cobolNumericString.isBlank()) {
            return BigDecimal.ZERO;
        }
        // Remove spaces (COBOL NUMVAL allows embedded spaces)
        String cleaned = cobolNumericString.replaceAll("\\s+", "");
        if (cleaned.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(cleaned);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Cannot convert to numeric: '" + cobolNumericString + "'", e);
        }
    }

    /**
     * Format a number with leading zeros (like COBOL PIC 9(n)).
     * Example: formatDisplay(24, 3) -> "024"
     */
    public static String formatDisplay(long value, int width) {
        return String.format("%0" + width + "d", value);
    }

    /**
     * Format a number with leading spaces for zero suppression (like COBOL PIC Z(n)9).
     * Example: formatSuppressed(24, 3) -> " 24"
     */
    public static String formatSuppressed(long value, int width) {
        return String.format("%" + width + "d", value);
    }

    /**
     * Convert between COBOL COMP (binary) representation and display.
     * In COBOL, COMP stores numbers in binary format.
     * In Java, int/long are already binary, so this is a direct mapping.
     */
    public static int compToDisplay(int compValue) {
        return compValue;
    }

    /**
     * Demonstrates COMP-2 (double-precision floating point) mapping.
     * COMP-2 in COBOL is equivalent to Java's double (IEEE 754).
     */
    public static double comp2Value(double value) {
        return value;
    }

    /**
     * Demonstrates COMP-3 (packed decimal) mapping.
     * COMP-3 in COBOL stores digits in BCD format.
     * In Java, BigDecimal provides equivalent arbitrary-precision arithmetic.
     */
    public static BigDecimal comp3Value(String value) {
        return numval(value);
    }
}
