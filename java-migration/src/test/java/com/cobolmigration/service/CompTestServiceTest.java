package com.cobolmigration.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CompTestService - validates migration of comp_test/comp_test.cbl.
 * Demonstrates Java native types replace COBOL COMP types.
 */
class CompTestServiceTest {

    private CompTestService service;

    @BeforeEach
    void setUp() {
        service = new CompTestService();
    }

    @Test
    void compMultiply_matchesCobolBehavior() {
        // COBOL: move 12 to ws-comp-val, multiply ws-comp-val by 2
        assertEquals(24, service.compMultiply(12, 2));
    }

    @Test
    void compToDisplay_formatsWithLeadingZeros() {
        // COBOL: pic 999 display format
        assertEquals("024", service.compToDisplay(24));
        assertEquals("001", service.compToDisplay(1));
        assertEquals("100", service.compToDisplay(100));
    }

    @Test
    void compToDynamicDisplay_suppressesLeadingZeros() {
        // COBOL: pic zz9 behavior - leading zeros become spaces
        assertEquals(" 24", service.compToDynamicDisplay(24));
        assertEquals("  1", service.compToDynamicDisplay(1));
        assertEquals("100", service.compToDynamicDisplay(100));
    }

    @Test
    void demonstrate_fullWorkflow() {
        // COBOL: move 12 to ws-comp-val, multiply by 2, display in all formats
        CompTestService.ComparisonResult result = service.demonstrate(12);
        assertEquals(24, result.compValue());
        assertEquals("024", result.displayValue());
        assertEquals(" 24", result.dynamicDisplayValue());
    }

    @Test
    void demonstrate_withUserInput() {
        // COBOL: accept ws-input, move ws-input to ws-comp-val
        CompTestService.ComparisonResult result = service.demonstrate(50);
        assertEquals(100, result.compValue());
        assertEquals("100", result.displayValue());
        assertEquals("100", result.dynamicDisplayValue());
    }
}
