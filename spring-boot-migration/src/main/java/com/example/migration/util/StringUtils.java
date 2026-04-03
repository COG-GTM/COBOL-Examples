package com.example.migration.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class replacing COBOL string processing functions.
 *
 * Replaces:
 *   - trim/trim.cbl: FUNCTION TRIM (leading, trailing, both)
 *   - unstring/unstring.cbl: UNSTRING ... DELIMITED BY
 *   - is_numeric/is_numeric.cbl: IS NUMERIC test
 *   - numval_test/numval_test.cbl: FUNCTION NUMVAL
 */
public final class StringUtils {

    private StringUtils() {
    }

    /**
     * Replaces COBOL: FUNCTION TRIM(ws-test-string-1)
     * Trims both leading and trailing whitespace (trim.cbl line 23).
     */
    public static String trim(String input) {
        if (input == null) {
            return null;
        }
        return input.strip();
    }

    /**
     * Replaces COBOL: FUNCTION TRIM(ws-test-string-1 LEADING)
     * Trims only leading whitespace (trim.cbl line 24).
     */
    public static String trimLeading(String input) {
        if (input == null) {
            return null;
        }
        return input.stripLeading();
    }

    /**
     * Replaces COBOL: FUNCTION TRIM(ws-test-string-1 TRAILING)
     * Trims only trailing whitespace (trim.cbl line 25).
     */
    public static String trimTrailing(String input) {
        if (input == null) {
            return null;
        }
        return input.stripTrailing();
    }

    /**
     * Replaces COBOL: UNSTRING ... DELIMITED BY
     * Splits a source string by one or more delimiters (unstring.cbl).
     *
     * The COBOL UNSTRING statement splits a string using specified delimiters
     * and places results into destination fields. This method replicates that
     * using Java's String.split() with a regex built from the delimiters.
     *
     * @param source the source string to split
     * @param delimiters one or more delimiter strings
     * @return an UnstringResult containing the parts, delimiters found, and counts
     */
    public static UnstringResult unstring(String source, String... delimiters) {
        if (source == null || delimiters == null || delimiters.length == 0) {
            return new UnstringResult(
                    source == null ? List.of() : List.of(source),
                    List.of(),
                    List.of()
            );
        }

        List<String> parts = new ArrayList<>();
        List<String> foundDelimiters = new ArrayList<>();
        List<Integer> charCounts = new ArrayList<>();

        StringBuilder regexBuilder = new StringBuilder();
        for (int i = 0; i < delimiters.length; i++) {
            if (i > 0) {
                regexBuilder.append("|");
            }
            regexBuilder.append("(").append(escapeRegex(delimiters[i])).append(")");
        }
        String regex = regexBuilder.toString();

        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(source);

        int lastEnd = 0;
        while (matcher.find()) {
            String part = source.substring(lastEnd, matcher.start());
            parts.add(part);
            charCounts.add(part.length());
            foundDelimiters.add(matcher.group());
            lastEnd = matcher.end();
        }

        String remaining = source.substring(lastEnd);
        parts.add(remaining);
        charCounts.add(remaining.length());

        return new UnstringResult(parts, foundDelimiters, charCounts);
    }

    /**
     * Replaces COBOL: ws-user-input IS NUMERIC (is_numeric.cbl lines 32, 54, 71).
     *
     * In COBOL, IS NUMERIC checks if a field contains only digits (for alphanumeric fields).
     * Trailing spaces cause the check to fail. The COBOL examples show three approaches:
     *   1. Plain check (fails with spaces)
     *   2. Right-justify + zero-fill, then check
     *   3. TRIM then check
     *
     * This method implements the trimmed version (approach 3), which is the most practical.
     */
    public static boolean isNumeric(String input) {
        if (input == null || input.isBlank()) {
            return false;
        }
        String trimmed = input.strip();
        return trimmed.matches("-?\\d+(\\.\\d+)?");
    }

    /**
     * Replaces COBOL: FUNCTION NUMVAL(ws-x-val) (numval_test.cbl line 28).
     *
     * COBOL NUMVAL converts an alphanumeric string to its numeric value.
     * It handles leading/trailing spaces, optional signs, and decimal points.
     */
    public static double numval(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Cannot convert blank/null value to number");
        }
        String trimmed = input.strip();
        try {
            return Double.parseDouble(trimmed);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Cannot convert '" + input + "' to a numeric value", e);
        }
    }

    private static String escapeRegex(String literal) {
        return java.util.regex.Pattern.quote(literal);
    }

    /**
     * Result of an UNSTRING operation, mirroring the COBOL UNSTRING outputs:
     *   - INTO destinations -> parts
     *   - DELIMITER IN -> delimitersFound
     *   - COUNT IN -> charCounts
     *   - TALLYING IN -> parts.size() (fields filled count)
     */
    public static class UnstringResult {
        private final List<String> parts;
        private final List<String> delimitersFound;
        private final List<Integer> charCounts;

        public UnstringResult(List<String> parts, List<String> delimitersFound,
                              List<Integer> charCounts) {
            this.parts = parts;
            this.delimitersFound = delimitersFound;
            this.charCounts = charCounts;
        }

        public List<String> getParts() {
            return parts;
        }

        public List<String> getDelimitersFound() {
            return delimitersFound;
        }

        public List<Integer> getCharCounts() {
            return charCounts;
        }

        public int getFieldsFilled() {
            return parts.size();
        }
    }
}
