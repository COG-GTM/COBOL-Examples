package com.cobolmigration.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utility class replacing COBOL COMP/COMP-3 numeric handling from comp_test/comp_test.cbl.
 *
 * <p>In COBOL, numeric storage types include:
 * <ul>
 *   <li>COMP (binary) - stored as binary integer, used for subscripts and calculations</li>
 *   <li>COMP-2 (double-precision floating point) - used in numval_test/numval_test.cbl</li>
 *   <li>COMP-3 (packed decimal) - BCD encoding for financial calculations</li>
 *   <li>COMP-5 (native binary) - used in sql_example.cbl for variable-length strings</li>
 *   <li>DISPLAY (default) - stored as characters, one byte per digit</li>
 * </ul>
 *
 * <p>In Java, we use BigDecimal for precision-sensitive calculations, replacing all
 * COMP variants to avoid floating-point precision loss.
 *
 * @see comp_test/comp_test.cbl
 * @see numval_test/numval_test.cbl
 */
public final class NumericUtils {

    private NumericUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Converts a COBOL COMP (binary) value representation to BigDecimal.
     * In COBOL, COMP stores values as binary integers (comp_test.cbl line 13: PIC 999 COMP).
     *
     * @param value the integer value
     * @return BigDecimal representation
     */
    public static BigDecimal fromComp(int value) {
        return BigDecimal.valueOf(value);
    }

    /**
     * Converts a COBOL COMP-2 (double-precision float) value to BigDecimal.
     * Used in numval_test.cbl (line 17: ws-total COMP-2).
     *
     * @param value the double value
     * @return BigDecimal representation with controlled precision
     */
    public static BigDecimal fromComp2(double value) {
        return BigDecimal.valueOf(value);
    }

    /**
     * Converts a COBOL COMP-3 (packed decimal) conceptual value to BigDecimal.
     * COMP-3 stores two digits per byte in BCD format.
     * In Java, BigDecimal natively handles decimal precision.
     *
     * @param value  the string representation of the packed decimal value
     * @param scale  the number of decimal places implied by the COBOL PIC clause
     * @return BigDecimal with the correct scale
     */
    public static BigDecimal fromComp3(String value, int scale) {
        BigDecimal result = new BigDecimal(value.trim());
        return result.setScale(scale, RoundingMode.HALF_UP);
    }

    /**
     * Formats a BigDecimal to a COBOL DISPLAY format string.
     * Replaces the COBOL MOVE of COMP to DISPLAY (comp_test.cbl lines 27-28).
     *
     * @param value the BigDecimal to format
     * @param width the total character width (PIC 999 = width 3)
     * @return zero-padded string representation
     */
    public static String toDisplay(BigDecimal value, int width) {
        if (value == null) {
            return "0".repeat(width);
        }
        long longVal = value.longValue();
        return String.format("%0" + width + "d", longVal);
    }

    /**
     * Formats a BigDecimal to a COBOL dynamic display format (suppressed leading zeros).
     * Replaces the PIC ZZ9 format (comp_test.cbl line 17).
     *
     * @param value the BigDecimal to format
     * @param width the total character width
     * @return right-justified string with leading spaces instead of zeros
     */
    public static String toDynamicDisplay(BigDecimal value, int width) {
        if (value == null) {
            return " ".repeat(width - 1) + "0";
        }
        return String.format("%" + width + "d", value.longValue());
    }

    /**
     * Multiplies two values, equivalent to COBOL MULTIPLY ... GIVING.
     * Uses BigDecimal for precision (comp_test.cbl line 24).
     *
     * @param a first operand
     * @param b second operand
     * @return product as BigDecimal
     */
    public static BigDecimal multiply(BigDecimal a, BigDecimal b) {
        return a.multiply(b);
    }

    /**
     * Adds two values, equivalent to COBOL ADD or COMPUTE.
     *
     * @param a first operand
     * @param b second operand
     * @return sum as BigDecimal
     */
    public static BigDecimal add(BigDecimal a, BigDecimal b) {
        return a.add(b);
    }
}
