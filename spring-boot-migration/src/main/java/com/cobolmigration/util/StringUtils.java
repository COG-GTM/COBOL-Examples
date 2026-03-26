package com.cobolmigration.util;

import java.util.ArrayList;
import java.util.List;

/**
 * String utility methods replacing COBOL intrinsic string functions.
 */
public final class StringUtils {

    private StringUtils() {
    }

    /**
     * Trim a string with COBOL-style leading/trailing/both options.
     * Replaces: FUNCTION TRIM(ws-test-value LEADING/TRAILING) from trim/trim.cbl
     *
     * In COBOL:
     *   FUNCTION TRIM(value)          -> trims both leading and trailing
     *   FUNCTION TRIM(value LEADING)  -> trims leading only
     *   FUNCTION TRIM(value TRAILING) -> trims trailing only
     */
    public static String cobolTrim(String input) {
        if (input == null) return null;
        return input.trim();
    }

    public static String cobolTrimLeading(String input) {
        if (input == null) return null;
        return input.stripLeading();
    }

    public static String cobolTrimTrailing(String input) {
        if (input == null) return null;
        return input.stripTrailing();
    }

    /**
     * Split a string by delimiter(s), similar to COBOL UNSTRING.
     * Replaces: UNSTRING ws-source-str DELIMITED BY delimiter
     *           INTO ws-part-1 ws-part-2 from unstring/unstring.cbl
     *
     * In COBOL, UNSTRING splits a source string by one or more delimiters
     * into destination fields, tracking the delimiter found and character counts.
     */
    public static String[] cobolUnstring(String input, String delimiter) {
        if (input == null) return new String[0];
        if (delimiter == null || delimiter.isEmpty()) {
            return new String[]{input};
        }
        return input.split(java.util.regex.Pattern.quote(delimiter), -1);
    }

    /**
     * Split a string by multiple delimiters, similar to COBOL UNSTRING with OR.
     * Replaces: UNSTRING ws-source-str DELIMITED BY "<" OR ">" OR "!" OR ws-delimiter
     */
    public static UnstringResult cobolUnstringMultiple(String input, String... delimiters) {
        if (input == null) {
            return new UnstringResult(new String[0], new String[0], new int[0]);
        }

        List<String> parts = new ArrayList<>();
        List<String> foundDelimiters = new ArrayList<>();
        List<Integer> charCounts = new ArrayList<>();

        int pos = 0;
        StringBuilder current = new StringBuilder();

        while (pos < input.length()) {
            boolean delimiterFound = false;
            for (String delim : delimiters) {
                if (input.startsWith(delim, pos)) {
                    parts.add(current.toString());
                    charCounts.add(current.length());
                    foundDelimiters.add(delim);
                    current = new StringBuilder();
                    pos += delim.length();
                    delimiterFound = true;
                    break;
                }
            }
            if (!delimiterFound) {
                current.append(input.charAt(pos));
                pos++;
            }
        }

        parts.add(current.toString());
        charCounts.add(current.length());
        foundDelimiters.add("");

        return new UnstringResult(
                parts.toArray(new String[0]),
                foundDelimiters.toArray(new String[0]),
                charCounts.stream().mapToInt(Integer::intValue).toArray()
        );
    }

    /**
     * Check if a string is numeric, similar to COBOL IS NUMERIC class condition.
     * Replaces: ws-user-input IS NUMERIC from is_numeric/is_numeric.cbl
     *
     * In COBOL, IS NUMERIC checks if an alphanumeric field contains only digits.
     * Spaces cause the check to fail (unlike Java's built-in methods).
     * The trimmed version matches: FUNCTION TRIM(ws-user-input) IS NUMERIC
     */
    public static boolean isNumeric(String input) {
        if (input == null || input.isEmpty()) return false;
        for (char c : input.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    /**
     * Result of an UNSTRING operation with multiple delimiters.
     * Captures the parts, delimiters found, and character counts
     * (similar to COBOL DELIMITER IN and COUNT IN clauses).
     */
    public static class UnstringResult {
        private final String[] parts;
        private final String[] delimiters;
        private final int[] charCounts;

        public UnstringResult(String[] parts, String[] delimiters, int[] charCounts) {
            this.parts = parts;
            this.delimiters = delimiters;
            this.charCounts = charCounts;
        }

        public String[] getParts() {
            return parts;
        }

        public String[] getDelimiters() {
            return delimiters;
        }

        public int[] getCharCounts() {
            return charCounts;
        }

        public int getFieldsFilled() {
            return parts.length;
        }
    }
}
