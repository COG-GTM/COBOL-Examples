package com.cobolmigration.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for SubProgramService - validates migration of
 * sub_program/sub.cbl and sub_program/main_app.cbl.
 */
class SubProgramServiceTest {

    private SubProgramService service;

    @BeforeEach
    void setUp() {
        service = new SubProgramService();
    }

    @Test
    void callByContent_doesNotModifyOriginalValues() {
        SubProgramService.SubProgramResult result = service.callByContent("hello", "world");
        // BY CONTENT: original values remain unchanged
        assertEquals("hello", result.item1());
        assertEquals("world", result.item2());
    }

    @Test
    void callByContent_updatesWorkingStorage() {
        service.callByContent("hello", "world");
        assertEquals("hello", service.getWsTestItem1());
        assertEquals("world", service.getWsTestItem2());
    }

    @Test
    void callByReference_returnsModifiedValues() {
        SubProgramService.SubProgramResult result = service.callByReference("hello", "world");
        // BY REFERENCE: values are modified by sub program
        assertEquals("replace1", result.item1());
        assertEquals("replace2", result.item2());
    }

    @Test
    void callByReference_updatesWorkingStorage() {
        service.callByReference("hello", "world");
        assertEquals("hello", service.getWsTestItem1());
        assertEquals("world", service.getWsTestItem2());
    }

    @Test
    void workingStorage_persistsBetweenCalls() {
        // First call sets WS values
        service.callByContent("first", "call");
        assertEquals("first", service.getWsTestItem1());

        // Second call: WS values persist from first call
        service.callByContent("second", "call");
        assertEquals("second", service.getWsTestItem1());
    }

    @Test
    void resetState_clearsWorkingStorage() {
        // Set WS values
        service.callByContent("test", "data");
        assertEquals("test", service.getWsTestItem1());

        // CANCEL "sub-app" equivalent
        service.resetState();
        assertEquals("", service.getWsTestItem1());
        assertEquals("", service.getWsTestItem2());
    }

    @Test
    void fullWorkflow_matchesCobolBehavior() {
        // Step 1: Call by content (main_app.cbl lines 34-38)
        SubProgramService.SubProgramResult result1 = service.callByContent("val1", "val2");
        assertEquals("val1", result1.item1()); // unchanged (BY CONTENT)

        // Step 2: Call by reference (main_app.cbl lines 46-49)
        // WS values should retain from previous call
        SubProgramService.SubProgramResult result2 = service.callByReference("val1", "val2");
        assertEquals("replace1", result2.item1()); // modified (BY REFERENCE)

        // Step 3: Cancel and call again (main_app.cbl lines 54-59)
        service.resetState();
        service.callByContent("val1", "val2");
        // WS values should be fresh (reset by cancel)
        assertEquals("val1", service.getWsTestItem1());
    }
}
