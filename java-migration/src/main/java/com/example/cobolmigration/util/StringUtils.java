package com.example.cobolmigration.util;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class replacing COBOL intrinsic string operations found across
 * the example modules (trim/, unstring/, is_numeric/, numval_test/).
 */
public final class StringUtils {

    private StringUtils() {
    }

    /**
     * Replaces COBOL TRIM intrinsic function (trim/ module).
     * Removes leading and trailing whitespace.
     */
    public static String cobolTrim(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }

    /**
     * Replaces COBOL UNSTRING ... DELIMITED BY (unstring/ module).
     * Splits the source string by the given delimiter.
     */
    public static List<String> cobolUnstring(String source, String delimiter) {
        if (source == null || source.isEmpty()) {
            return List.of();
        }
        return Arrays.asList(source.split(delimiter, -1));
    }

    /**
     * Replaces COBOL IS NUMERIC test (is_numeric/ module).
     * Returns true if the trimmed string represents a valid number.
     *
     * The COBOL IS NUMERIC check on an alphanumeric field fails if there
     * are trailing spaces; the trimmed variant (process-trim paragraph)
     * works. This method mirrors the trimmed behaviour.
     */
    public static boolean isNumeric(String value) {
        if (value == null) {
            return false;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(trimmed);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Replaces COBOL NUMVAL intrinsic function (numval_test/ module).
     * Converts a string representation of a number to a double.
     *
     * @throws NumberFormatException if the value cannot be parsed
     */
    public static double numval(String value) {
        if (value == null) {
            throw new NumberFormatException("null");
        }
        return Double.parseDouble(value.trim());
    }
}
