package com.cobolmigration.util;

import java.util.ArrayList;
import java.util.List;

/**
 * String utility methods migrated from COBOL intrinsic functions.
 *
 * Equivalent of COBOL TRIM function (from trim/trim.cbl)
 * and UNSTRING statement (from unstring/unstring.cbl).
 */
public final class StringUtils {

    private StringUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Trim mode options matching COBOL TRIM function behavior.
     * COBOL: FUNCTION TRIM(string)          -> BOTH
     * COBOL: FUNCTION TRIM(string LEADING)  -> LEADING
     * COBOL: FUNCTION TRIM(string TRAILING) -> TRAILING
     */
    public enum TrimMode {
        BOTH,
        LEADING,
        TRAILING
    }

    /**
     * Trims whitespace from a string, equivalent to COBOL FUNCTION TRIM.
     * Default mode trims both leading and trailing whitespace.
     *
     * From trim/trim.cbl:
     *   FUNCTION TRIM(ws-test-string-1)          -> trim both
     *   FUNCTION TRIM(ws-test-string-1 LEADING)  -> trim leading only
     *   FUNCTION TRIM(ws-test-string-1 TRAILING) -> trim trailing only
     *
     * @param input the string to trim
     * @return the trimmed string, or empty string if input is null
     */
    public static String trim(String input) {
        return trim(input, TrimMode.BOTH);
    }

    /**
     * Trims whitespace from a string with the specified mode.
     *
     * @param input the string to trim
     * @param mode  the trim mode (BOTH, LEADING, or TRAILING)
     * @return the trimmed string, or empty string if input is null
     */
    public static String trim(String input, TrimMode mode) {
        if (input == null) {
            return "";
        }
        switch (mode) {
            case LEADING:
                return trimLeading(input);
            case TRAILING:
                return trimTrailing(input);
            case BOTH:
            default:
                return input.trim();
        }
    }

    private static String trimLeading(String input) {
        int start = 0;
        while (start < input.length() && Character.isWhitespace(input.charAt(start))) {
            start++;
        }
        return input.substring(start);
    }

    private static String trimTrailing(String input) {
        int end = input.length();
        while (end > 0 && Character.isWhitespace(input.charAt(end - 1))) {
            end--;
        }
        return input.substring(0, end);
    }

    /**
     * Result of an UNSTRING operation, containing the split parts
     * along with COBOL-style metadata (delimiter, count, pointer, tallying).
     *
     * Models the COBOL UNSTRING ... INTO ... DELIMITER IN ... COUNT IN ...
     * WITH POINTER ... TALLYING IN ... syntax from unstring/unstring.cbl.
     */
    public static class UnstringResult {
        private final List<String> parts;
        private final List<String> delimiters;
        private final List<Integer> counts;
        private final int pointer;
        private final int fieldsFilled;

        public UnstringResult(List<String> parts, List<String> delimiters,
                              List<Integer> counts, int pointer, int fieldsFilled) {
            this.parts = parts;
            this.delimiters = delimiters;
            this.counts = counts;
            this.pointer = pointer;
            this.fieldsFilled = fieldsFilled;
        }

        public List<String> getParts() {
            return parts;
        }

        public List<String> getDelimiters() {
            return delimiters;
        }

        public List<Integer> getCounts() {
            return counts;
        }

        /** The pointer position after the unstring (1-based, COBOL style). */
        public int getPointer() {
            return pointer;
        }

        /** The number of destination fields that were filled (TALLYING). */
        public int getFieldsFilled() {
            return fieldsFilled;
        }
    }

    /**
     * Simple unstring: splits the input by the given delimiter.
     * Equivalent to COBOL: UNSTRING source DELIMITED BY delimiter INTO part1, part2, ...
     *
     * @param input     the source string to split
     * @param delimiter the delimiter to split on
     * @return list of parts
     */
    public static List<String> unstring(String input, String delimiter) {
        if (input == null || input.isEmpty()) {
            return new ArrayList<>();
        }
        if (delimiter == null || delimiter.isEmpty()) {
            List<String> result = new ArrayList<>();
            result.add(input);
            return result;
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
     * Full unstring with COBOL-style metadata: delimiter tracking, count tracking,
     * pointer position, and tallying.
     *
     * From unstring/unstring.cbl examples 4 & 5:
     *   UNSTRING source
     *     DELIMITED BY delim1 OR delim2 OR ...
     *     INTO dest DELIMITER IN delimOut COUNT IN charCount
     *     WITH POINTER ptr
     *     TALLYING IN fieldsFilled
     *
     * @param input      the source string
     * @param delimiters array of delimiter strings to split on
     * @param startPos   1-based starting position (COBOL POINTER)
     * @param maxFields  maximum number of fields to fill (0 for unlimited)
     * @return UnstringResult with parts, delimiters, counts, pointer, and tallying
     */
    public static UnstringResult unstringFull(String input, String[] delimiters,
                                              int startPos, int maxFields) {
        List<String> parts = new ArrayList<>();
        List<String> delimsFound = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();

        if (input == null || input.isEmpty() || delimiters == null || delimiters.length == 0) {
            if (input != null && !input.isEmpty()) {
                parts.add(input);
                delimsFound.add("");
                counts.add(input.length());
            }
            return new UnstringResult(parts, delimsFound, counts,
                    input == null ? startPos : input.length() + 1, parts.size());
        }

        // Convert from 1-based COBOL pointer to 0-based Java index
        int pos = startPos - 1;
        int fieldsFilled = 0;

        while (pos < input.length() && (maxFields == 0 || fieldsFilled < maxFields)) {
            int nearestDelimPos = input.length();
            String foundDelim = "";

            // Find the nearest delimiter from current position
            for (String delim : delimiters) {
                if (delim == null || delim.isEmpty()) continue;
                int delimIdx = input.indexOf(delim, pos);
                if (delimIdx != -1 && delimIdx < nearestDelimPos) {
                    nearestDelimPos = delimIdx;
                    foundDelim = delim;
                }
            }

            if (nearestDelimPos == input.length()) {
                // No more delimiters found - take the rest of the string
                String part = input.substring(pos);
                parts.add(part);
                delimsFound.add("");
                counts.add(part.length());
                pos = input.length();
            } else {
                String part = input.substring(pos, nearestDelimPos);
                parts.add(part);
                delimsFound.add(foundDelim);
                counts.add(part.length());
                pos = nearestDelimPos + foundDelim.length();
            }
            fieldsFilled++;
        }

        // Convert back to 1-based COBOL pointer
        return new UnstringResult(parts, delimsFound, counts, pos + 1, fieldsFilled);
    }
}
