package com.example.cobolmigration.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Replaces trim/trim.cbl and unstring/unstring.cbl.
 *
 * trim.cbl demonstrates FUNCTION TRIM with leading, trailing, and full trim.
 * unstring.cbl demonstrates UNSTRING with various delimiter configurations.
 */
@Service
public class StringUtilService {

    /**
     * Returns a map with "full", "leading", "trailing" trim results.
     * Matches trim.cbl lines 22-25:
     *   FUNCTION TRIM(ws-test-string-1)          — full trim
     *   FUNCTION TRIM(ws-test-string-1 LEADING)  — trim leading spaces only
     *   FUNCTION TRIM(ws-test-string-1 TRAILING) — trim trailing spaces only
     */
    public Map<String, String> trimVariants(String input) {
        Map<String, String> results = new LinkedHashMap<>();
        if (input == null) {
            results.put("full", null);
            results.put("leading", null);
            results.put("trailing", null);
            return results;
        }
        results.put("full", input.strip());
        results.put("leading", input.stripLeading());
        results.put("trailing", input.stripTrailing());
        return results;
    }

    /**
     * Replaces basic UNSTRING (unstring.cbl Example 1, lines 58-61):
     *   UNSTRING ws-source-str DELIMITED BY SPACE INTO ws-part-1 ws-part-2
     */
    public List<String> splitByDelimiter(String source, String delimiter) {
        if (source == null || delimiter == null) {
            return List.of();
        }
        String[] parts = source.split(Pattern.quote(delimiter), -1);
        List<String> result = new ArrayList<>();
        for (String part : parts) {
            result.add(part);
        }
        return result;
    }

    /**
     * Replaces the multi-delimiter UNSTRING examples (unstring.cbl Examples 4-5, lines 155-163).
     * Returns split parts with delimiter info and character counts, matching the COBOL
     * DELIMITER IN, COUNT IN, and TALLYING IN behavior.
     *
     * Each result entry contains: "value", "delimiter", "charCount".
     */
    public Map<String, Object> splitByMultipleDelimiters(String source, List<String> delimiters) {
        List<Map<String, Object>> parts = new ArrayList<>();

        if (source == null || delimiters == null || delimiters.isEmpty()) {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("parts", parts);
            result.put("fieldsFilled", 0);
            return result;
        }

        // Build regex pattern that alternates between all delimiters
        StringBuilder patternBuilder = new StringBuilder();
        for (int i = 0; i < delimiters.size(); i++) {
            if (i > 0) {
                patternBuilder.append("|");
            }
            patternBuilder.append("(").append(Pattern.quote(delimiters.get(i))).append(")");
        }

        Pattern pattern = Pattern.compile(patternBuilder.toString());
        Matcher matcher = pattern.matcher(source);

        int lastEnd = 0;
        while (matcher.find()) {
            String value = source.substring(lastEnd, matcher.start());
            String delimiter = matcher.group();

            Map<String, Object> partInfo = new LinkedHashMap<>();
            partInfo.put("value", value);
            partInfo.put("delimiter", delimiter);
            partInfo.put("charCount", value.length());
            parts.add(partInfo);

            lastEnd = matcher.end();
        }

        // Add the remaining part after the last delimiter
        if (lastEnd <= source.length()) {
            String remaining = source.substring(lastEnd);
            Map<String, Object> partInfo = new LinkedHashMap<>();
            partInfo.put("value", remaining);
            partInfo.put("delimiter", "");
            partInfo.put("charCount", remaining.length());
            parts.add(partInfo);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("parts", parts);
        result.put("fieldsFilled", parts.size());
        return result;
    }

    /**
     * Replaces Example 6 of unstring.cbl (lines 240-252).
     * Parses a formatted currency string like "$123,456.12" by unstringng on ',' and '.'
     * (skipping the leading '$').
     *
     * COBOL:
     *   move 123456.12 to ws-source-num          — formatted as $123,456.12
     *   unstring ws-source-num(2:)               — start at 2 to skip '$'
     *       delimited by ',' or '.'
     *       into ws-dest-num(1) ws-dest-num(2) ws-dest-num(3)
     */
    public List<String> splitFormattedNumber(String formattedNumber) {
        if (formattedNumber == null || formattedNumber.isEmpty()) {
            return List.of();
        }

        // Skip leading '$' if present (matching COBOL's ws-source-num(2:))
        String source = formattedNumber;
        if (source.startsWith("$")) {
            source = source.substring(1);
        }

        // Split by ',' or '.'
        String[] parts = source.split("[,.]", -1);
        List<String> result = new ArrayList<>();
        for (String part : parts) {
            result.add(part);
        }
        return result;
    }
}
