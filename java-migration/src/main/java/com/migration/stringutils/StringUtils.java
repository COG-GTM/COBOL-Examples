package com.migration.stringutils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Phase 1: String Utilities
 *
 * Migrates COBOL string operations to Java equivalents:
 * - FUNCTION TRIM (trim/trim_test.cbl) -> String.strip/stripLeading/stripTrailing
 * - UNSTRING ... DELIMITED BY ... INTO ... (unstring/unstring_test.cbl) -> String.split / regex
 * - IS NUMERIC (is_numeric/is_numeric_test.cbl) -> regex / parseDouble
 * - FUNCTION NUMVAL (numval_test/numval_test.cbl) -> Double.parseDouble
 */
public final class StringUtils {

    private static final Pattern NUMERIC_PATTERN = Pattern.compile("^-?\\d+(\\.\\d+)?$");

    private StringUtils() {
        // utility class
    }

    // ---- Trim operations (COBOL: FUNCTION TRIM) ----

    /**
     * Equivalent to COBOL: FUNCTION TRIM(value)
     * Removes both leading and trailing whitespace.
     */
    public static String trim(String value) {
        if (value == null) {
            return null;
        }
        return value.strip();
    }

    /**
     * Equivalent to COBOL: FUNCTION TRIM(value LEADING)
     * Removes leading whitespace only.
     */
    public static String trimLeading(String value) {
        if (value == null) {
            return null;
        }
        return value.stripLeading();
    }

    /**
     * Equivalent to COBOL: FUNCTION TRIM(value TRAILING)
     * Removes trailing whitespace only.
     */
    public static String trimTrailing(String value) {
        if (value == null) {
            return null;
        }
        return value.stripTrailing();
    }

    // ---- Unstring operations (COBOL: UNSTRING ... DELIMITED BY) ----

    /**
     * Equivalent to COBOL: UNSTRING source DELIMITED BY delimiter INTO dest1, dest2, ...
     *
     * Splits a string by a single delimiter and returns the parts.
     * COBOL pads results to field width with spaces; Java returns exact strings.
     */
    public static String[] unstring(String source, String delimiter) {
        if (source == null) {
            return new String[0];
        }
        return source.split(Pattern.quote(delimiter), -1);
    }

    /**
     * Equivalent to COBOL: UNSTRING source DELIMITED BY ALL delimiter INTO ...
     *
     * The ALL keyword treats consecutive delimiters as one.
     * Maps to Java regex split with '+' quantifier.
     */
    public static String[] unstringAll(String source, String delimiter) {
        if (source == null) {
            return new String[0];
        }
        return source.split(Pattern.quote(delimiter) + "+", -1);
    }

    /**
     * Equivalent to COBOL: UNSTRING source DELIMITED BY d1 OR d2 OR ... INTO ...
     *
     * Splits by multiple delimiters. Returns an UnstringResult with parts,
     * delimiters found, and character counts (mirroring COBOL TALLYING/DELIMITER/COUNT).
     */
    public static UnstringResult unstringMultipleDelimiters(String source, String... delimiters) {
        if (source == null || delimiters == null || delimiters.length == 0) {
            return new UnstringResult(new String[]{source}, new String[0], new int[0], 0);
        }

        // Build regex alternation from delimiters, escaped for literal matching
        StringBuilder regexBuilder = new StringBuilder();
        for (int i = 0; i < delimiters.length; i++) {
            if (i > 0) {
                regexBuilder.append("|");
            }
            regexBuilder.append(Pattern.quote(delimiters[i]));
        }
        Pattern pattern = Pattern.compile("(" + regexBuilder + ")");

        List<String> parts = new ArrayList<>();
        List<String> foundDelimiters = new ArrayList<>();
        List<Integer> charCounts = new ArrayList<>();

        var matcher = pattern.matcher(source);
        int lastEnd = 0;

        while (matcher.find()) {
            String part = source.substring(lastEnd, matcher.start());
            parts.add(part);
            charCounts.add(part.length());
            foundDelimiters.add(matcher.group());
            lastEnd = matcher.end();
        }

        // Add the remaining part after the last delimiter
        String remaining = source.substring(lastEnd);
        parts.add(remaining);
        charCounts.add(remaining.length());

        return new UnstringResult(
                parts.toArray(new String[0]),
                foundDelimiters.toArray(new String[0]),
                charCounts.stream().mapToInt(Integer::intValue).toArray(),
                parts.size()
        );
    }

    // ---- IsNumeric (COBOL: IS NUMERIC) ----

    /**
     * Equivalent to COBOL: IF value IS NUMERIC
     *
     * In COBOL, a PIC X field with trailing spaces is NOT numeric even if
     * the non-space characters are all digits. This method checks the raw
     * value (including spaces). Use isNumericTrimmed() for the COBOL
     * TRIM + IS NUMERIC pattern.
     */
    public static boolean isNumeric(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return NUMERIC_PATTERN.matcher(value).matches();
    }

    /**
     * Equivalent to COBOL: IF FUNCTION TRIM(value) IS NUMERIC
     *
     * Trims the value first, then checks if it is numeric.
     * This mirrors the COBOL pattern in is_numeric.cbl process-trim paragraph.
     */
    public static boolean isNumericTrimmed(String value) {
        if (value == null) {
            return false;
        }
        return isNumeric(value.strip());
    }

    /**
     * Equivalent to COBOL zero-fill + IS NUMERIC pattern.
     *
     * Right-justifies the value, replaces leading spaces with '0',
     * then checks if numeric. Mirrors the process-zero-fill paragraph.
     */
    public static boolean isNumericZeroFilled(String value, int fieldWidth) {
        if (value == null) {
            return false;
        }
        String trimmed = value.strip();
        String rightJustified = String.format("%" + fieldWidth + "s", trimmed);
        String zeroFilled = rightJustified.replace(' ', '0');
        return isNumeric(zeroFilled);
    }

    // ---- Numval (COBOL: FUNCTION NUMVAL) ----

    /**
     * Equivalent to COBOL: FUNCTION NUMVAL(value)
     *
     * Converts a string to a double, stripping spaces and handling
     * COBOL-specific formatting (leading/trailing spaces, signs).
     *
     * @throws NumberFormatException if the value cannot be parsed
     */
    public static double numval(String value) {
        if (value == null) {
            throw new NumberFormatException("Cannot convert null to number");
        }
        String cleaned = value.strip();
        if (cleaned.isEmpty()) {
            throw new NumberFormatException("Cannot convert empty string to number");
        }
        // Handle COBOL sign conventions: trailing sign (e.g., "123-")
        if (cleaned.endsWith("-") && !cleaned.startsWith("-")) {
            cleaned = "-" + cleaned.substring(0, cleaned.length() - 1);
        } else if (cleaned.endsWith("+")) {
            cleaned = cleaned.substring(0, cleaned.length() - 1);
        }
        return Double.parseDouble(cleaned);
    }

    /**
     * Result of an UNSTRING operation with multiple delimiters.
     * Mirrors COBOL's TALLYING IN, DELIMITER IN, and COUNT IN clauses.
     */
    public record UnstringResult(
            String[] parts,
            String[] delimitersFound,
            int[] charCounts,
            int fieldsFilled
    ) {}
}
