package com.cobol.examples.core.text;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure-logic port of {@code trim/trim.cbl} and {@code unstring/unstring.cbl}.
 */
public final class TextUtils {

    /** Which edge(s) {@link #trim} should strip, mirroring the COBOL keyword. */
    public enum TrimMode { BOTH, LEADING, TRAILING }

    private TextUtils() {
    }

    /** Equivalent of {@code FUNCTION TRIM(value [LEADING|TRAILING])}. */
    public static String trim(String value, TrimMode mode) {
        if (value == null) {
            return "";
        }
        int start = 0;
        int end = value.length();
        if (mode != TrimMode.TRAILING) {
            while (start < end && value.charAt(start) == ' ') {
                start++;
            }
        }
        if (mode != TrimMode.LEADING) {
            while (end > start && value.charAt(end - 1) == ' ') {
                end--;
            }
        }
        return value.substring(start, end);
    }

    /**
     * Equivalent of {@code UNSTRING ... DELIMITED BY [ALL] d1 OR d2 ...}.
     *
     * @param collapseConsecutive {@code true} reproduces {@code DELIMITED BY ALL}
     *                            where runs of delimiters yield no empty fields.
     */
    public static List<String> unstring(String source, boolean collapseConsecutive, char... delimiters) {
        List<String> parts = new ArrayList<>();
        if (source == null || source.isEmpty()) {
            return parts;
        }
        StringBuilder current = new StringBuilder();
        boolean previousWasDelimiter = false;
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (isDelimiter(c, delimiters)) {
                if (collapseConsecutive && previousWasDelimiter) {
                    continue;
                }
                parts.add(current.toString());
                current.setLength(0);
                previousWasDelimiter = true;
            } else {
                current.append(c);
                previousWasDelimiter = false;
            }
        }
        parts.add(current.toString());
        return parts;
    }

    private static boolean isDelimiter(char c, char[] delimiters) {
        for (char d : delimiters) {
            if (c == d) {
                return true;
            }
        }
        return false;
    }
}
