package com.cobolmigration.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Numeric validation and conversion utilities replacing COBOL IS NUMERIC,
 * NUMVAL, and NUMVAL-C intrinsic functions.
 *
 * Migrated from:
 *   - is_numeric/is_numeric_test.cbl (IS NUMERIC class condition)
 *   - numval_test/numval_test.cbl (NUMVAL/NUMVAL-C intrinsic functions)
 *
 * COBOL IS NUMERIC:
 *   Tests if a PIC X field contains only numeric characters.
 *   Note: COBOL's IS NUMERIC on a PIC X(n) field fails if there are
 *   any non-digit characters including trailing spaces (unless the field
 *   is fully filled with digits).
 *
 * COBOL NUMVAL:
 *   Converts a PIC X string to a numeric value, handling leading/trailing
 *   spaces, optional sign, and decimal point.
 *
 * COBOL NUMVAL-C:
 *   Like NUMVAL but also handles currency symbols and comma separators.
 */
public final class NumericUtils {

    private NumericUtils() {
    }

    /**
     * Tests if a string is numeric (contains only digits).
     * Mimics COBOL's IS NUMERIC behavior for PIC X fields where the entire
     * field must be digits (no spaces allowed).
     *
     * @param value the string to test
     * @return true if the string contains only digit characters
     */
    public static boolean isNumeric(String value) {
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
     * Tests if a trimmed string is numeric.
     * Mimics the COBOL pattern of TRIM then IS NUMERIC, which allows
     * leading/trailing spaces to be ignored.
     *
     * @param value the string to test
     * @return true if the trimmed string contains only digit characters
     */
    public static boolean isNumericTrimmed(String value) {
        if (value == null) {
            return false;
        }
        return isNumeric(value.strip());
    }

    /**
     * Tests if a right-justified, zero-filled string is numeric.
     * Mimics the COBOL pattern of JUSTIFIED RIGHT + INSPECT REPLACING
     * LEADING SPACES BY '0' + IS NUMERIC.
     *
     * @param value  the string to test
     * @param length the fixed field length for right-justification
     * @return true if the zero-filled value is numeric
     */
    public static boolean isNumericZeroFilled(String value, int length) {
        if (value == null) {
            return false;
        }
        String rightJustified = String.format("%" + length + "s", value.strip());
        String zeroFilled = rightJustified.replace(' ', '0');
        return isNumeric(zeroFilled);
    }

    /**
     * Converts a string to BigDecimal, equivalent to COBOL NUMVAL function.
     * Handles leading/trailing spaces, optional sign (+/-), and decimal point.
     *
     * COBOL NUMVAL accepts strings like: "  123  ", " -45.67 ", "+100"
     *
     * @param value the string to convert
     * @return the numeric value as BigDecimal
     * @throws NumberFormatException if the string cannot be parsed
     */
    public static BigDecimal numval(String value) {
        if (value == null || value.isBlank()) {
            throw new NumberFormatException("Cannot convert blank/null to number");
        }
        String cleaned = value.strip();
        return new BigDecimal(cleaned);
    }

    /**
     * Converts a currency-formatted string to BigDecimal, equivalent to COBOL NUMVAL-C.
     * Strips currency symbols ($), commas, and handles sign characters.
     *
     * COBOL NUMVAL-C accepts strings like: "$1,234.56", "$-500.00", "1,000"
     *
     * @param value          the currency-formatted string to convert
     * @param currencySymbol the currency symbol to strip (e.g., "$")
     * @return the numeric value as BigDecimal
     * @throws NumberFormatException if the string cannot be parsed
     */
    public static BigDecimal numvalC(String value, String currencySymbol) {
        if (value == null || value.isBlank()) {
            throw new NumberFormatException("Cannot convert blank/null to number");
        }
        String cleaned = value.strip()
                .replace(currencySymbol, "")
                .replace(",", "");
        return new BigDecimal(cleaned);
    }

    /**
     * Convenience overload using "$" as the default currency symbol.
     */
    public static BigDecimal numvalC(String value) {
        return numvalC(value, "$");
    }

    /**
     * Adds two numeric values with BigDecimal precision, matching COBOL
     * COMPUTE semantics that use exact decimal arithmetic.
     *
     * @param a first value (string, parsed via numval)
     * @param b second value (string, parsed via numval)
     * @return the sum as BigDecimal
     */
    public static BigDecimal computeAdd(String a, String b) {
        return numval(a).add(numval(b));
    }
}
