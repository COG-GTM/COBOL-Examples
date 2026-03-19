package com.cobolmigration.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class replacing COBOL string/data operations:
 * <ul>
 *   <li>TRIM intrinsic function &rarr; {@link #trim(String)}, {@link #trimLeading(String)},
 *       {@link #trimTrailing(String)} (from trim/trim.cbl)</li>
 *   <li>UNSTRING statement &rarr; {@link #unstring(String, String...)} (from unstring/unstring.cbl)</li>
 *   <li>IS NUMERIC class condition &rarr; {@link #isNumeric(String)} (from is_numeric/is_numeric.cbl)</li>
 *   <li>NUMVAL intrinsic function &rarr; {@link #numval(String)} (from numval_test/numval_test.cbl)</li>
 * </ul>
 *
 * @see trim/trim.cbl
 * @see unstring/unstring.cbl
 * @see is_numeric/is_numeric.cbl
 * @see numval_test/numval_test.cbl
 */
public final class StringUtils {

    private static final Pattern NUMERIC_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");

    private StringUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Replaces COBOL FUNCTION TRIM(value).
     * Removes both leading and trailing spaces, equivalent to COBOL's default TRIM.
     *
     * @see trim/trim.cbl line 23
     */
    public static String trim(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }

    /**
     * Replaces COBOL FUNCTION TRIM(value LEADING).
     * Removes leading spaces only.
     *
     * @see trim/trim.cbl line 24
     */
    public static String trimLeading(String value) {
        if (value == null) {
            return "";
        }
        return value.stripLeading();
    }

    /**
     * Replaces COBOL FUNCTION TRIM(value TRAILING).
     * Removes trailing spaces only.
     *
     * @see trim/trim.cbl line 25
     */
    public static String trimTrailing(String value) {
        if (value == null) {
            return "";
        }
        return value.stripTrailing();
    }

    /**
     * Replaces the COBOL UNSTRING statement with multiple delimiters.
     * Splits a source string by one or more delimiter patterns.
     *
     * <p>In COBOL, UNSTRING splits a source string into destination fields
     * based on specified delimiters (unstring/unstring.cbl lines 58-61).
     *
     * @param source     the source string to split
     * @param delimiters one or more delimiter strings
     * @return list of parts after splitting
     * @see unstring/unstring.cbl
     */
    public static List<String> unstring(String source, String... delimiters) {
        if (source == null || delimiters == null || delimiters.length == 0) {
            List<String> result = new ArrayList<>();
            if (source != null) {
                result.add(source);
            }
            return result;
        }

        // Build a regex pattern from all delimiters, escaping special regex chars
        StringBuilder patternBuilder = new StringBuilder();
        for (int i = 0; i < delimiters.length; i++) {
            if (i > 0) {
                patternBuilder.append("|");
            }
            patternBuilder.append(Pattern.quote(delimiters[i]));
        }

        String[] parts = source.split(patternBuilder.toString(), -1);
        List<String> result = new ArrayList<>();
        for (String part : parts) {
            result.add(part);
        }
        return result;
    }

    /**
     * Replaces the COBOL IS NUMERIC class condition.
     * In COBOL, a PIC X field IS NUMERIC only if all characters are contiguous digits
     * (is_numeric/is_numeric.cbl). Spaces cause the test to fail unless the value is trimmed first.
     *
     * @param value the string to test
     * @return true if the trimmed value represents a valid number
     * @see is_numeric/is_numeric.cbl
     */
    public static boolean isNumeric(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        return NUMERIC_PATTERN.matcher(value.trim()).matches();
    }

    /**
     * Replaces the COBOL FUNCTION NUMVAL(value).
     * Converts an alphanumeric PIC X value to a numeric BigDecimal.
     * In COBOL, NUMVAL converts a string representation of a number to its numeric
     * equivalent (numval_test/numval_test.cbl line 28).
     *
     * @param value the string to parse
     * @return the numeric value as BigDecimal
     * @throws NumberFormatException if the value cannot be parsed
     * @see numval_test/numval_test.cbl
     */
    public static BigDecimal numval(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new NumberFormatException("Cannot parse null or empty value");
        }
        return new BigDecimal(value.trim());
    }

    /**
     * Pads a string to a fixed width with trailing spaces, mimicking COBOL's PIC X(n) behavior.
     * COBOL alphanumeric fields are always space-padded to their defined length.
     *
     * @param value the string to pad
     * @param width the target width
     * @return the padded string
     */
    public static String padRight(String value, int width) {
        if (value == null) {
            return " ".repeat(width);
        }
        if (value.length() >= width) {
            return value.substring(0, width);
        }
        return value + " ".repeat(width - value.length());
    }
}
