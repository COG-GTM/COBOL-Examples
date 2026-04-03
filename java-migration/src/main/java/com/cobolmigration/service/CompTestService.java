package com.cobolmigration.service;

import org.springframework.stereotype.Service;

/**
 * Replaces comp_test/comp_test.cbl.
 * Demonstrates that Java native int, long, double replace COBOL COMP, COMP-3, COMP-5 types.
 * In COBOL, COMP (binary), COMP-3 (packed decimal), and COMP-5 (native binary) are
 * different internal representations. In Java, these are all handled natively.
 */
@Service
public class CompTestService {

    /**
     * Demonstrates COMP (binary) arithmetic.
     * COBOL: pic 999 comp -> Java: int (binary representation is native)
     */
    public int compMultiply(int value, int multiplier) {
        return value * multiplier;
    }

    /**
     * Converts a COMP value to display format.
     * In COBOL, COMP values need explicit MOVE to display PIC fields.
     * In Java, int to String conversion is automatic.
     */
    public String compToDisplay(int compValue) {
        return String.format("%03d", compValue);
    }

    /**
     * Converts a COMP value to dynamic display (suppressing leading zeros).
     * Replaces COBOL PIC zz9 behavior where leading zeros become spaces.
     */
    public String compToDynamicDisplay(int compValue) {
        return String.format("%3d", compValue);
    }

    /**
     * Demonstrates that Java handles all COBOL numeric types natively:
     * - COMP (binary) -> int/long
     * - COMP-1 (single-precision float) -> float
     * - COMP-2 (double-precision float) -> double
     * - COMP-3 (packed decimal) -> BigDecimal or double
     * - COMP-5 (native binary) -> int/long
     */
    public record ComparisonResult(int compValue, String displayValue, String dynamicDisplayValue) {
    }

    public ComparisonResult demonstrate(int inputValue) {
        int compValue = compMultiply(inputValue, 2);
        String displayValue = compToDisplay(compValue);
        String dynamicDisplayValue = compToDynamicDisplay(compValue);
        return new ComparisonResult(compValue, displayValue, dynamicDisplayValue);
    }
}
