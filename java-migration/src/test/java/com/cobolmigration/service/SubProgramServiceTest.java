package com.cobolmigration.service;

import com.cobolmigration.service.SubProgramService.ExecutionResult;
import com.cobolmigration.service.SubProgramService.MutableString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SubProgramService.
 * Verifies state retention between calls and parameter passing semantics
 * match COBOL sub_program behavior.
 */
class SubProgramServiceTest {

    private SubProgramService service;

    @BeforeEach
    void setUp() {
        service = new SubProgramService();
    }

    @Test
    void executeByContent_shouldNotModifyCallerVariables() {
        // COBOL: CALL BY CONTENT - caller's variables remain unchanged
        String item1 = "hello";
        String item2 = "world";

        service.executeByContent(item1, item2);

        // Java strings are immutable, so they can't be modified
        assertEquals("hello", item1);
        assertEquals("world", item2);
    }

    @Test
    void executeByReference_shouldModifyCallerVariables() {
        // COBOL: CALL BY REFERENCE - caller's variables are modified
        MutableString ref1 = new MutableString("hello");
        MutableString ref2 = new MutableString("world");

        service.executeByReference(ref1, ref2);

        // Sub program sets values to "replace1" and "replace2"
        assertEquals("replace1", ref1.getValue());
        assertEquals("replace2", ref2.getValue());
    }

    @Test
    void workingStorage_shouldRetainValuesBetweenCalls() {
        // COBOL: Working-storage values persist between calls
        service.executeByContent("first", "call");
        ExecutionResult secondResult = service.executeByContent("second", "call");

        // On second call, working-storage should have values from first call
        assertEquals("first", secondResult.wsItem1AtStart());
        assertEquals("call", secondResult.wsItem2AtStart());
    }

    @Test
    void cancel_shouldResetWorkingStorage() {
        // COBOL: CANCEL resets working-storage to initial values
        service.executeByContent("data1", "data2");

        // Working storage should have values
        assertEquals("data1", service.getWsTestItem1());
        assertEquals("data2", service.getWsTestItem2());

        // Cancel resets everything
        service.cancel();
        assertEquals("", service.getWsTestItem1());
        assertEquals("", service.getWsTestItem2());
    }

    @Test
    void afterCancel_workingStorageShouldBeReset() {
        // Verify working storage is empty after cancel
        service.executeByContent("data1", "data2");
        service.cancel();

        ExecutionResult result = service.executeByContent("new1", "new2");

        // Working storage should be empty at start (was reset by cancel)
        assertEquals("", result.wsItem1AtStart());
        assertEquals("", result.wsItem2AtStart());
    }

    @Test
    void byReferenceCall_shouldRetainWorkingStorageAndModifyParams() {
        // First call sets working storage
        service.executeByContent("initial1", "initial2");

        // Second call by reference: WS should retain values from first call
        MutableString ref1 = new MutableString("ref1");
        MutableString ref2 = new MutableString("ref2");
        ExecutionResult result = service.executeByReference(ref1, ref2);

        // Working storage at start should have values from first call
        assertEquals("initial1", result.wsItem1AtStart());
        assertEquals("initial2", result.wsItem2AtStart());

        // Working storage at end should have new values from reference call
        assertEquals("ref1", result.wsItem1AtEnd());
        assertEquals("ref2", result.wsItem2AtEnd());

        // Mutable refs should be modified
        assertEquals("replace1", ref1.getValue());
        assertEquals("replace2", ref2.getValue());
    }
}
