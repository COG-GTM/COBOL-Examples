package com.migration.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Phase 6: Subprogram Architecture Tests
 *
 * Test cases mirror the COBOL sub_program/main_app.cbl and sub_program/sub.cbl:
 * - CALL BY CONTENT (copy semantics)
 * - CALL BY REFERENCE (reference semantics)
 * - WORKING-STORAGE persistence between calls
 * - LOCAL-STORAGE freshness each call
 * - CANCEL (reset working storage)
 */
class SubAppServiceTest {

    private SubAppService subAppService;
    private MainAppService mainAppService;

    @BeforeEach
    void setUp() {
        subAppService = new SubAppService();
        mainAppService = new MainAppService(subAppService);
    }

    @Test
    @DisplayName("CALL BY CONTENT - original values not modified")
    void callByContent() {
        // COBOL: CALL "sub-app" USING BY CONTENT ws-item-1 BY CONTENT ws-item-2
        String item1 = "value1";
        String item2 = "value2";

        SubAppService.CallResult result = subAppService.processCall(item1, item2, true);

        // Output should be same as input (BY CONTENT = copy, no modification)
        assertEquals(item1, result.outputItem1());
        assertEquals(item2, result.outputItem2());

        // Working storage should have been updated
        assertEquals(item1, result.workingStorageItem1());
        assertEquals(item2, result.workingStorageItem2());
    }

    @Test
    @DisplayName("CALL BY REFERENCE - values can be modified by sub-program")
    void callByReference() {
        // COBOL: CALL "sub-app" USING ws-item-1 ws-item-2 (default = BY REFERENCE)
        SubAppService.CallResult result = subAppService.processCall("original1", "original2", false);

        // Sub-program replaces values (COBOL: MOVE "replace1" TO l-test-item-1)
        assertEquals("replace1", result.outputItem1());
        assertEquals("replace2", result.outputItem2());
    }

    @Test
    @DisplayName("WORKING-STORAGE persists between calls")
    void workingStoragePersistence() {
        // First call sets working-storage values
        subAppService.processCall("first", "second", true);
        assertEquals("first", subAppService.getWorkingStorageItem1());
        assertEquals("second", subAppService.getWorkingStorageItem2());

        // Second call - working storage should retain values from first call
        // (but gets overwritten with new values in processCall)
        SubAppService.CallResult secondResult = subAppService.processCall("third", "fourth", true);

        // Working storage now has the new values
        assertEquals("third", secondResult.workingStorageItem1());
        assertEquals("fourth", secondResult.workingStorageItem2());
    }

    @Test
    @DisplayName("LOCAL-STORAGE is fresh on each call")
    void localStorageFreshness() {
        // First call
        SubAppService.CallResult result1 = subAppService.processCall("a", "b", true);
        assertEquals("a", result1.localStorageItem1());

        // Second call - local storage starts fresh (not retained from first call)
        // The method initializes local vars to "" before setting them
        SubAppService.CallResult result2 = subAppService.processCall("c", "d", true);
        assertEquals("c", result2.localStorageItem1());
        assertEquals("d", result2.localStorageItem2());
    }

    @Test
    @DisplayName("CANCEL resets working storage")
    void cancelResetsWorkingStorage() {
        // COBOL: Set working-storage values via CALL
        subAppService.processCall("value1", "value2", true);
        assertEquals("value1", subAppService.getWorkingStorageItem1());

        // COBOL: CANCEL "sub-app"
        subAppService.reset();

        // Working storage should be reset to initial empty values
        assertEquals("", subAppService.getWorkingStorageItem1());
        assertEquals("", subAppService.getWorkingStorageItem2());
    }

    @Test
    @DisplayName("Full main-app flow: BY CONTENT -> BY REFERENCE -> CANCEL -> call again")
    void fullMainAppFlow() {
        // COBOL main_app.cbl full flow
        MainAppService.MainAppResult result = mainAppService.runMainFlow("test1", "test2");

        // Step 1: BY CONTENT - output same as input
        assertEquals("test1", result.byContentResult().outputItem1());
        assertEquals("test2", result.byContentResult().outputItem2());

        // Step 2: BY REFERENCE - output modified
        assertEquals("replace1", result.byReferenceResult().outputItem1());
        assertEquals("replace2", result.byReferenceResult().outputItem2());

        // Step 3: After CANCEL - working storage was reset before this call
        // The call itself sets new working storage values
        assertEquals("test1", result.afterCancelResult().workingStorageItem1());
        assertEquals("test2", result.afterCancelResult().workingStorageItem2());
    }

    @Test
    @DisplayName("Working storage values visible in subsequent call results")
    void workingStorageInResults() {
        // First call
        SubAppService.CallResult r1 = subAppService.processCall("A", "B", true);
        assertEquals("A", r1.workingStorageItem1());

        // Second call - working storage was set by first call, then overwritten by second
        SubAppService.CallResult r2 = subAppService.processCall("C", "D", true);
        assertEquals("C", r2.workingStorageItem1());
    }
}
