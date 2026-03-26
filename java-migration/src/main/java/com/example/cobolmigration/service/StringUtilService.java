package com.example.cobolmigration.service;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Spring Service mapping string manipulation COBOL programs.
 * Maps: trim/trim.cbl, unstring/unstring.cbl, is_numeric/is_numeric.cbl.
 */
@Service
public class StringUtilService {

    /**
     * Trims both leading and trailing whitespace.
     * Maps {@code FUNCTION TRIM(ws-test-string-1)} from trim/trim.cbl.
     */
    public String trimBoth(String input) {
        if (input == null) {
            return null;
        }
        return input.strip();
    }

    /**
     * Trims leading whitespace only.
     * Maps {@code FUNCTION TRIM(ws-test-string-1 LEADING)} from trim/trim.cbl.
     */
    public String trimLeading(String input) {
        if (input == null) {
            return null;
        }
        return input.stripLeading();
    }

    /**
     * Trims trailing whitespace only.
     * Maps {@code FUNCTION TRIM(ws-test-string-1 TRAILING)} from trim/trim.cbl.
     */
    public String trimTrailing(String input) {
        if (input == null) {
            return null;
        }
        return input.stripTrailing();
    }

    /**
     * Splits the source string by the given delimiter and returns the parts.
     * Maps {@code UNSTRING ws-source-str DELIMITED BY space INTO ws-part-1 ws-part-2}
     * from unstring/unstring.cbl.
     *
     * @param source    the source string to split
     * @param delimiter the delimiter to split on
     * @return list of non-empty parts after splitting
     */
    public List<String> unstring(String source, String delimiter) {
        if (source == null || delimiter == null) {
            return List.of();
        }
        String[] parts = source.split(java.util.regex.Pattern.quote(delimiter), -1);
        return Arrays.stream(parts)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    /**
     * Returns true if the string contains only digits.
     * Maps {@code ws-user-input IS NUMERIC} from is_numeric/is_numeric.cbl.
     * In COBOL, a PIC X field with trailing spaces is NOT considered numeric.
     */
    public boolean isNumeric(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        for (char c : input.toCharArray()) {
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    /**
     * Trims the input and then checks if it is numeric.
     * Maps the trimmed variant in is_numeric/is_numeric.cbl (process-trim paragraph).
     */
    public boolean isNumericTrimmed(String input) {
        if (input == null) {
            return false;
        }
        return isNumeric(input.strip());
    }
}
