package com.cobolmigration.service;

import com.cobolmigration.service.MainAppService.CallDemoResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MainAppService.
 * Verifies the full demo flow matches COBOL main_app.cbl behavior.
 */
class MainAppServiceTest {

    private SubProgramService subProgramService;
    private MainAppService mainAppService;

    @BeforeEach
    void setUp() {
        subProgramService = new SubProgramService();
        mainAppService = new MainAppService(subProgramService);
    }

    @Test
    void runDemo_shouldPreserveValuesAfterByContentCall() {
        CallDemoResult result = mainAppService.runDemo("hello", "world");

        // After by-content call, original values should be unchanged
        assertEquals("hello", result.afterContentItem1());
        assertEquals("world", result.afterContentItem2());
    }

    @Test
    void runDemo_shouldModifyValuesAfterByReferenceCall() {
        CallDemoResult result = mainAppService.runDemo("hello", "world");

        // After by-reference call, values should be modified by subprogram
        assertEquals("replace1", result.afterReferenceItem1());
        assertEquals("replace2", result.afterReferenceItem2());
    }

    @Test
    void runDemo_byReferenceCallShouldRetainWorkingStorage() {
        CallDemoResult result = mainAppService.runDemo("hello", "world");

        // The reference call is the second call to the sub program
        // Working storage should have values from the first (by-content) call
        assertEquals("hello", result.referenceCallResult().wsItem1AtStart());
        assertEquals("world", result.referenceCallResult().wsItem2AtStart());
    }

    @Test
    void runDemo_afterCancelWorkingStorageShouldBeReset() {
        CallDemoResult result = mainAppService.runDemo("hello", "world");

        // After cancel, working storage should be reset to empty
        assertEquals("", result.afterCancelCallResult().wsItem1AtStart());
        assertEquals("", result.afterCancelCallResult().wsItem2AtStart());
    }
}
