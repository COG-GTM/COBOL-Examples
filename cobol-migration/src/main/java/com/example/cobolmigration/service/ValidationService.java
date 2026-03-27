package com.example.cobolmigration.service;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Replaces is_numeric/is_numeric.cbl and numval_test/numval_test.cbl.
 *
 * The COBOL is_numeric program demonstrates three approaches to checking if a string is numeric:
 *   1. Plain check — if ws-user-input IS NUMERIC (fails with trailing spaces)
 *   2. Right-justify + zero-fill — INSPECT ... REPLACING LEADING SPACES BY '0' then IS NUMERIC
 *   3. Trim then check — if FUNCTION TRIM(ws-user-input) IS NUMERIC
 *
 * The COBOL NUMVAL function (numval_test.cbl line 28) converts a PIC X string to a numeric value.
 */
@Service
public class ValidationService {

    /**
     * Checks if the input string is numeric using three approaches,
     * mirroring the COBOL is_numeric.cbl behavior.
     *
     * @param input the string to validate
     * @return a map with results for each approach: "plain", "zeroFill", "trimmed"
     */
    public Map<String, Boolean> isNumeric(String input) {
        Map<String, Boolean> results = new LinkedHashMap<>();

        // Approach 1: Plain check (COBOL lines 25-38)
        // In COBOL, trailing spaces cause IS NUMERIC to fail for alphanumeric fields.
        // Java equivalent: check the full string including spaces.
        results.put("plain", input != null && input.matches("^[0-9]+$"));

        // Approach 2: Right-justify + zero-fill (COBOL lines 42-60)
        // COBOL: JUSTIFIED RIGHT + INSPECT REPLACING LEADING SPACES BY '0'
        if (input != null) {
            String rightJustified = String.format("%10s", input.trim());
            String zeroFilled = rightJustified.replace(' ', '0');
            results.put("zeroFill", zeroFilled.matches("^[0-9]+$"));
        } else {
            results.put("zeroFill", false);
        }

        // Approach 3: Trim then check (COBOL lines 64-77)
        // COBOL: FUNCTION TRIM(ws-user-input) IS NUMERIC
        results.put("trimmed", input != null && input.trim().matches("^[0-9]+$"));

        return results;
    }

    /**
     * Parses a numeric value from a string, replacing the COBOL NUMVAL function
     * (numval_test.cbl line 28: FUNCTION NUMVAL(ws-x-val)).
     *
     * NUMVAL converts a PIC X string to a numeric value, handling leading/trailing spaces
     * and optional sign characters.
     *
     * @param input the string to parse
     * @return the parsed numeric value
     * @throws NumberFormatException if the input is not a valid number
     */
    public double parseNumericValue(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new NumberFormatException("Input is null or empty");
        }
        String trimmed = input.trim();
        if (NumberUtils.isCreatable(trimmed)) {
            return NumberUtils.createDouble(trimmed);
        }
        throw new NumberFormatException("Cannot parse numeric value from: " + input);
    }
}
