package com.cobolmigration.util;

import java.util.ArrayList;
import java.util.List;

/**
 * String manipulation utilities replacing COBOL TRIM and UNSTRING functions.
 *
 * Migrated from:
 *   - trim/trim_test.cbl (TRIM intrinsic function)
 *   - unstring/unstring_test.cbl (UNSTRING verb)
 *
 * COBOL TRIM function behavior:
 *   FUNCTION TRIM(var)          - removes both leading and trailing spaces
 *   FUNCTION TRIM(var LEADING)  - removes leading spaces only
 *   FUNCTION TRIM(var TRAILING) - removes trailing spaces only
 *
 * COBOL UNSTRING verb:
 *   UNSTRING source DELIMITED BY delim INTO dest1 dest2 ...
 *   Splits a source string into destination fields based on delimiters.
 */
public final class StringUtils {

    private StringUtils() {
    }

    /**
     * Trims both leading and trailing spaces from a string.
     * Equivalent to COBOL: FUNCTION TRIM(ws-string)
     */
    public static String trim(String value) {
        if (value == null) {
            return null;
        }
        return value.strip();
    }

    /**
     * Trims leading spaces only.
     * Equivalent to COBOL: FUNCTION TRIM(ws-string LEADING)
     */
    public static String trimLeading(String value) {
        if (value == null) {
            return null;
        }
        return value.stripLeading();
    }

    /**
     * Trims trailing spaces only.
     * Equivalent to COBOL: FUNCTION TRIM(ws-string TRAILING)
     */
    public static String trimTrailing(String value) {
        if (value == null) {
            return null;
        }
        return value.stripTrailing();
    }

    /**
     * Pads a string to a fixed length with trailing spaces, mimicking
     * COBOL PIC X(n) fixed-length field behavior. If the string is
     * longer than the specified length, it is truncated.
     *
     * @param value  the string to pad
     * @param length the target fixed length
     * @return the padded or truncated string
     */
    public static String padToFixedLength(String value, int length) {
        if (value == null) {
            return " ".repeat(length);
        }
        if (value.length() >= length) {
            return value.substring(0, length);
        }
        return value + " ".repeat(length - value.length());
    }

    /**
     * Splits a string by a single delimiter, equivalent to a simple COBOL UNSTRING.
     *
     * COBOL: UNSTRING source DELIMITED BY delim INTO dest1 dest2 ...
     *
     * @param source    the source string to split
     * @param delimiter the delimiter string
     * @return list of parts after splitting
     */
    public static List<String> unstring(String source, String delimiter) {
        if (source == null || source.isEmpty()) {
            return List.of();
        }
        String[] parts = source.split(java.util.regex.Pattern.quote(delimiter), -1);
        return List.of(parts);
    }

    /**
     * Splits a string by multiple delimiters, returning both the parts and
     * the delimiters found. This matches the COBOL UNSTRING with multiple
     * DELIMITED BY clauses and DELIMITER IN tracking.
     *
     * COBOL: UNSTRING source DELIMITED BY "<" OR ">" OR "!" OR "|"
     *        INTO dest DELIMITER IN ws-delimiter COUNT IN ws-count
     *
     * @param source     the source string to split
     * @param delimiters array of delimiter strings
     * @return list of UnstringResult entries with value, delimiter, and char count
     */
    public static List<UnstringResult> unstringMultipleDelimiters(String source, String... delimiters) {
        List<UnstringResult> results = new ArrayList<>();
        if (source == null || source.isEmpty()) {
            return results;
        }

        int pos = 0;
        while (pos < source.length()) {
            int earliestMatch = source.length();
            String matchedDelimiter = "";

            for (String delim : delimiters) {
                int idx = source.indexOf(delim, pos);
                if (idx >= 0 && idx < earliestMatch) {
                    earliestMatch = idx;
                    matchedDelimiter = delim;
                }
            }

            if (earliestMatch == source.length()) {
                // No more delimiters found; rest of string is last part
                String part = source.substring(pos);
                results.add(new UnstringResult(part, "", part.length()));
                break;
            } else {
                String part = source.substring(pos, earliestMatch);
                results.add(new UnstringResult(part, matchedDelimiter, part.length()));
                pos = earliestMatch + matchedDelimiter.length();
            }
        }

        return results;
    }

    /**
     * Result of an UNSTRING operation with multiple delimiters.
     * Captures the extracted value, the delimiter that was found, and
     * the character count of the extracted value.
     *
     * Mirrors COBOL's DELIMITER IN and COUNT IN clauses.
     */
    public record UnstringResult(String value, String delimiter, int charCount) {
    }
}
