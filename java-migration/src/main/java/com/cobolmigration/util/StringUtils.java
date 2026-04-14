package com.cobolmigration.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class replacing COBOL string operations:
 * - trim/trim.cbl: FUNCTION TRIM behavior
 * - unstring/unstring.cbl: UNSTRING/DELIMITED BY logic
 * - is_numeric/is_numeric.cbl: IS NUMERIC check
 * - numval_test/numval_test.cbl: FUNCTION NUMVAL
 */
public final class StringUtils {

    private StringUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Replaces COBOL FUNCTION TRIM behavior from trim/trim.cbl.
     * Trims leading and trailing spaces from the input string.
     *
     * @param input the string to trim
     * @return trimmed string, or empty string if input is null
     */
    public static String trim(String input) {
        if (input == null) {
            return "";
        }
        return input.trim();
    }

    /**
     * Replaces COBOL FUNCTION TRIM with LEADING option.
     * Removes leading spaces only.
     *
     * @param input the string to trim
     * @return string with leading spaces removed
     */
    public static String trimLeading(String input) {
        if (input == null) {
            return "";
        }
        return input.stripLeading();
    }

    /**
     * Replaces COBOL FUNCTION TRIM with TRAILING option.
     * Removes trailing spaces only.
     *
     * @param input the string to trim
     * @return string with trailing spaces removed
     */
    public static String trimTrailing(String input) {
        if (input == null) {
            return "";
        }
        return input.stripTrailing();
    }

    /**
     * Replaces COBOL UNSTRING/DELIMITED BY logic from unstring/unstring.cbl.
     * Splits the input string by the given delimiter and returns a list of parts.
     *
     * @param input     the source string to split
     * @param delimiter the delimiter to split on
     * @return list of string parts after splitting
     */
    public static List<String> unstring(String input, String delimiter) {
        if (input == null || input.isEmpty()) {
            return List.of();
        }
        if (delimiter == null || delimiter.isEmpty()) {
            return List.of(input);
        }
        List<String> result = new ArrayList<>();
        int start = 0;
        int idx;
        while ((idx = input.indexOf(delimiter, start)) != -1) {
            result.add(input.substring(start, idx));
            start = idx + delimiter.length();
        }
        result.add(input.substring(start));
        return result;
    }

    /**
     * Replaces COBOL UNSTRING with multiple delimiters.
     * Splits the input string by any of the given delimiters.
     *
     * @param input      the source string to split
     * @param delimiters array of delimiter strings
     * @return list of string parts after splitting
     */
    public static List<String> unstringMultipleDelimiters(String input, String... delimiters) {
        if (input == null || input.isEmpty()) {
            return List.of();
        }
        if (delimiters == null || delimiters.length == 0) {
            return List.of(input);
        }

        List<String> result = new ArrayList<>();
        int pos = 0;

        while (pos < input.length()) {
            int nearestIdx = input.length();
            int delimLen = 0;

            for (String delim : delimiters) {
                int idx = input.indexOf(delim, pos);
                if (idx != -1 && idx < nearestIdx) {
                    nearestIdx = idx;
                    delimLen = delim.length();
                }
            }

            if (nearestIdx == input.length()) {
                result.add(input.substring(pos));
                break;
            } else {
                result.add(input.substring(pos, nearestIdx));
                pos = nearestIdx + delimLen;
            }
        }

        return result;
    }

    /**
     * Replaces COBOL IS NUMERIC check from is_numeric/is_numeric.cbl.
     * Checks if the trimmed input string represents a numeric value.
     *
     * @param input the string to check
     * @return true if the string is numeric (after trimming)
     */
    public static boolean isNumeric(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        String trimmed = input.trim();
        try {
            Double.parseDouble(trimmed);
            return true;
        } catch (NumberFormatException e) {
            return trimmed.chars().allMatch(Character::isDigit);
        }
    }

    /**
     * Replaces COBOL FUNCTION NUMVAL from numval_test/numval_test.cbl.
     * Converts a string to its numeric double value.
     * COBOL NUMVAL converts alphanumeric (PIC X) fields to numeric values.
     *
     * @param input the string to convert
     * @return the numeric value as a double
     * @throws NumberFormatException if the string cannot be parsed
     */
    public static double numval(String input) {
        if (input == null || input.trim().isEmpty()) {
            return 0.0;
        }
        return Double.parseDouble(input.trim());
    }
}
