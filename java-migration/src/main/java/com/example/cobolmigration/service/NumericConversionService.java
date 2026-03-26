package com.example.cobolmigration.service;

import org.springframework.stereotype.Service;

/**
 * Spring Service mapping comp_test/comp_test.cbl and numval_test/numval_test.cbl.
 * Handles conversions between COBOL COMP (binary) and DISPLAY (character) formats.
 *
 * In Java, {@code int} is already a binary (COMP) representation, so conversions
 * focus on formatting for display output.
 */
@Service
public class NumericConversionService {

    /**
     * Documents that Java int is already binary, equivalent to COBOL COMP.
     * The COBOL {@code COMP} storage format stores values in binary;
     * Java's {@code int} is natively binary.
     *
     * @param displayValue the value to "convert" (returned as-is since Java int is binary)
     * @return the same value
     */
    public int toComp(int displayValue) {
        return displayValue;
    }

    /**
     * Formats an integer according to a PIC 9(n) pattern (zero-padded).
     * Maps the COBOL DISPLAY format where values are stored as character digits.
     * <p>
     * Example: toDisplay(24, "999") returns "024"
     *
     * @param compValue the integer value
     * @param picFormat a PIC 9(n) style format string, e.g. "999" or "9(5)"
     * @return zero-padded string representation
     */
    public String toDisplay(int compValue, String picFormat) {
        int width = parsePicWidth(picFormat);
        return String.format("%0" + width + "d", compValue);
    }

    /**
     * Formats with leading zero suppression, mapping PIC ZZ9 from comp_test.cbl.
     * Leading zeros are replaced with spaces, except the last digit is always shown.
     * <p>
     * Example: toDynamicDisplay(24) returns " 24", toDynamicDisplay(0) returns "  0"
     *
     * @param value the integer value
     * @return formatted string with leading zero suppression (3-digit width)
     */
    public String toDynamicDisplay(int value) {
        String formatted = String.format("%3d", value);
        return formatted;
    }

    /**
     * Parses a COBOL PIC format string to determine the display width.
     * Supports formats like "999", "9(5)", "9(3)".
     */
    private int parsePicWidth(String picFormat) {
        if (picFormat == null || picFormat.isEmpty()) {
            return 1;
        }
        // Handle PIC 9(n) format
        if (picFormat.contains("(") && picFormat.contains(")")) {
            int start = picFormat.indexOf('(');
            int end = picFormat.indexOf(')');
            try {
                return Integer.parseInt(picFormat.substring(start + 1, end));
            } catch (NumberFormatException e) {
                return picFormat.length();
            }
        }
        // Handle PIC 999 format — count the 9s and Zs
        int count = 0;
        for (char c : picFormat.toCharArray()) {
            if (c == '9' || c == 'Z' || c == 'z') {
                count++;
            }
        }
        return count > 0 ? count : picFormat.length();
    }
}
