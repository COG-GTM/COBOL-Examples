package com.example.cobolmigration.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests numeric validation with the same inputs from is_numeric/is_numeric.cbl.
 */
class ValidationServiceTest {

    private ValidationService service;

    @BeforeEach
    void setUp() {
        service = new ValidationService();
    }

    @Test
    void isNumeric_pureDigits_allApproachesReturnTrue() {
        // Input "12345" — all digits, no spaces
        Map<String, Boolean> result = service.isNumeric("12345");
        assertTrue(result.get("plain"));
        assertTrue(result.get("zeroFill"));
        assertTrue(result.get("trimmed"));
    }

    @Test
    void isNumeric_digitsWithTrailingSpaces_plainFails() {
        // In COBOL, "123   " in a PIC X(10) field with trailing spaces fails IS NUMERIC
        // for the plain approach (is_numeric.cbl lines 25-38)
        Map<String, Boolean> result = service.isNumeric("123       ");
        assertFalse(result.get("plain"));
        // Zero-fill and trim approaches should succeed
        assertTrue(result.get("zeroFill"));
        assertTrue(result.get("trimmed"));
    }

    @Test
    void isNumeric_alphabeticInput_allFail() {
        Map<String, Boolean> result = service.isNumeric("abc");
        assertFalse(result.get("plain"));
        assertFalse(result.get("zeroFill"));
        assertFalse(result.get("trimmed"));
    }

    @Test
    void isNumeric_mixedAlphaNumeric_allFail() {
        Map<String, Boolean> result = service.isNumeric("12ab34");
        assertFalse(result.get("plain"));
        assertFalse(result.get("zeroFill"));
        assertFalse(result.get("trimmed"));
    }

    @Test
    void isNumeric_nullInput_allFail() {
        Map<String, Boolean> result = service.isNumeric(null);
        assertFalse(result.get("plain"));
        assertFalse(result.get("zeroFill"));
        assertFalse(result.get("trimmed"));
    }

    @Test
    void parseNumericValue_validInteger() {
        // Replaces NUMVAL function (numval_test.cbl line 28)
        assertEquals(42.0, service.parseNumericValue("42"));
    }

    @Test
    void parseNumericValue_validDecimal() {
        assertEquals(3.14, service.parseNumericValue("3.14"));
    }

    @Test
    void parseNumericValue_withLeadingSpaces() {
        // NUMVAL handles leading/trailing spaces
        assertEquals(100.0, service.parseNumericValue("  100  "));
    }

    @Test
    void parseNumericValue_negativeNumber() {
        assertEquals(-25.0, service.parseNumericValue("-25"));
    }

    @Test
    void parseNumericValue_invalidInput_throwsException() {
        assertThrows(NumberFormatException.class, () -> service.parseNumericValue("abc"));
    }

    @Test
    void parseNumericValue_nullInput_throwsException() {
        assertThrows(NumberFormatException.class, () -> service.parseNumericValue(null));
    }

    @Test
    void parseNumericValue_emptyInput_throwsException() {
        assertThrows(NumberFormatException.class, () -> service.parseNumericValue(""));
    }
}
