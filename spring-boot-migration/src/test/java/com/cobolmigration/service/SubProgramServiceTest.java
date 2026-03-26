package com.cobolmigration.service;

import com.cobolmigration.model.MutableHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for SubProgramService verifying COBOL subprogram calling conventions:
 *   - BY CONTENT doesn't modify originals
 *   - BY REFERENCE does modify originals
 *   - Working-storage persists between calls
 *   - CANCEL resets working-storage
 *
 * Validates behavior matching sub_program/main_app.cbl and sub_program/sub.cbl
 */
class SubProgramServiceTest {

    private SubProgramService subProgramService;

    @BeforeEach
    void setUp() {
        subProgramService = new SubProgramService();
    }

    @Test
    void processByContent_shouldNotModifyOriginals() {
        String item1 = "value1";
        String item2 = "value2";

        subProgramService.processByContent(item1, item2);

        // BY CONTENT: originals should not be modified
        assertEquals("value1", item1);
        assertEquals("value2", item2);
    }

    @Test
    void processByContent_shouldUpdateWorkingStorage() {
        subProgramService.processByContent("val1", "val2");

        assertEquals("val1", subProgramService.getWsTestItem1());
        assertEquals("val2", subProgramService.getWsTestItem2());
    }

    @Test
    void processByReference_shouldModifyOriginals() {
        MutableHolder<String> item1 = new MutableHolder<>("value1");
        MutableHolder<String> item2 = new MutableHolder<>("value2");

        subProgramService.processByReference(item1, item2);

        // BY REFERENCE: originals should be modified to "replace1"/"replace2"
        assertEquals("replace1", item1.getValue());
        assertEquals("replace2", item2.getValue());
    }

    @Test
    void processByReference_shouldUpdateWorkingStorage() {
        MutableHolder<String> item1 = new MutableHolder<>("val1");
        MutableHolder<String> item2 = new MutableHolder<>("val2");

        subProgramService.processByReference(item1, item2);

        assertEquals("val1", subProgramService.getWsTestItem1());
        assertEquals("val2", subProgramService.getWsTestItem2());
    }

    @Test
    void workingStorage_shouldPersistBetweenCalls() {
        // First call sets working-storage
        subProgramService.processByContent("first1", "first2");

        // Second call should see previous working-storage values
        SubProgramService.SubResult result = subProgramService.processByContent("second1", "second2");

        assertEquals("first1", result.getWsAtStart1());
        assertEquals("first2", result.getWsAtStart2());
    }

    @Test
    void localStorage_shouldBeResetEachCall() {
        // First call
        subProgramService.processByContent("val1", "val2");

        // Second call - local-storage should start empty
        SubProgramService.SubResult result = subProgramService.processByContent("val3", "val4");

        assertEquals("", result.getLsAtStart1());
        assertEquals("", result.getLsAtStart2());
    }

    @Test
    void cancel_shouldResetWorkingStorage() {
        // Set some working-storage values
        subProgramService.processByContent("val1", "val2");
        assertEquals("val1", subProgramService.getWsTestItem1());

        // Cancel should reset
        subProgramService.cancel();

        assertEquals("", subProgramService.getWsTestItem1());
        assertEquals("", subProgramService.getWsTestItem2());
    }

    @Test
    void cancel_thenCall_shouldStartWithEmptyWorkingStorage() {
        // Set working-storage
        subProgramService.processByContent("val1", "val2");

        // Cancel
        subProgramService.cancel();

        // Next call should see empty working-storage
        SubProgramService.SubResult result = subProgramService.processByContent("new1", "new2");

        assertEquals("", result.getWsAtStart1());
        assertEquals("", result.getWsAtStart2());
    }

    @Test
    void fullDemoSequence_shouldMatchCobolBehavior() {
        // Simulates the full flow from main_app.cbl

        // Step 1: Call by content
        String orig1 = "test1";
        String orig2 = "test2";
        subProgramService.processByContent(orig1, orig2);
        // Originals unchanged
        assertEquals("test1", orig1);
        assertEquals("test2", orig2);
        // WS updated
        assertEquals("test1", subProgramService.getWsTestItem1());

        // Step 2: Call by reference (WS should retain values from step 1)
        MutableHolder<String> ref1 = new MutableHolder<>("test1");
        MutableHolder<String> ref2 = new MutableHolder<>("test2");
        SubProgramService.SubResult refResult = subProgramService.processByReference(ref1, ref2);
        // WS at start should have values from step 1
        assertEquals("test1", refResult.getWsAtStart1());
        // Originals should be modified
        assertEquals("replace1", ref1.getValue());
        assertEquals("replace2", ref2.getValue());

        // Step 3: Cancel and call again
        subProgramService.cancel();
        SubProgramService.SubResult resetResult = subProgramService.processByContent("new1", "new2");
        // WS should be empty at start (was cancelled)
        assertEquals("", resetResult.getWsAtStart1());
        assertEquals("", resetResult.getWsAtStart2());
    }
}
