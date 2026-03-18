package com.migration.services;

import org.springframework.stereotype.Service;

/**
 * Phase 6: Subprogram Architecture -> Service Layer
 *
 * Migrates COBOL sub_program/sub.cbl to a Spring @Service bean.
 *
 * COBOL subprogram patterns mapped to Java/Spring:
 *
 * 1. WORKING-STORAGE SECTION (persistent between calls):
 *    -> @Service singleton scope with instance fields.
 *    COBOL: ws-test-item-1, ws-test-item-2 retain values between CALL statements.
 *    Java:  workingStorageItem1, workingStorageItem2 persist as long as the bean lives.
 *
 * 2. LOCAL-STORAGE SECTION (fresh each call):
 *    -> Local variables within methods. Each method invocation gets fresh values.
 *    COBOL: ls-test-item-1, ls-test-item-2 are reinitialized on every CALL.
 *    Java:  Local variables inside processCall() are initialized each time.
 *
 * 3. LINKAGE SECTION (parameters):
 *    -> Method parameters.
 *    COBOL: l-test-item-1, l-test-item-2 passed via CALL ... USING.
 *    Java:  Method parameters on processCall().
 *
 * 4. CANCEL (reset working storage):
 *    -> reset() method that reinitializes all instance fields.
 *    COBOL: CANCEL "sub-app" resets ws-test-item-1/2 to initial values.
 *    Java:  subAppService.reset() clears all stateful fields.
 */
@Service
public class SubAppService {

    // WORKING-STORAGE equivalents (persist between calls)
    private String workingStorageItem1 = "";
    private String workingStorageItem2 = "";

    /**
     * Result of a subprogram call, containing both the working-storage
     * state and the potentially modified reference parameters.
     */
    public record CallResult(
            String workingStorageItem1,
            String workingStorageItem2,
            String localStorageItem1,
            String localStorageItem2,
            String outputItem1,
            String outputItem2
    ) {}

    /**
     * Processes a call to the sub-application.
     *
     * Equivalent to COBOL: CALL "sub-app" USING l-test-item-1 l-test-item-2
     *
     * @param item1 First parameter (LINKAGE SECTION l-test-item-1)
     * @param item2 Second parameter (LINKAGE SECTION l-test-item-2)
     * @param byContent If true, parameters are passed BY CONTENT (copy semantics,
     *                  originals not modified). If false, BY REFERENCE (reference
     *                  semantics, modifications returned via CallResult).
     * @return CallResult with the state after processing
     */
    public CallResult processCall(String item1, String item2, boolean byContent) {
        // LOCAL-STORAGE equivalents (fresh each call)
        String localStorageItem1 = "";
        String localStorageItem2 = "";

        // Move linkage values to working-storage and local-storage
        // COBOL: MOVE l-test-item-1 TO ws-test-item-1
        workingStorageItem1 = item1;
        workingStorageItem2 = item2;
        localStorageItem1 = item1;
        localStorageItem2 = item2;

        // Determine output values based on call semantics
        String outputItem1;
        String outputItem2;

        if (byContent) {
            // BY CONTENT: original values are not modified
            outputItem1 = item1;
            outputItem2 = item2;
        } else {
            // BY REFERENCE: sub-program can modify the values
            // COBOL: MOVE "replace1" TO l-test-item-1
            outputItem1 = "replace1";
            outputItem2 = "replace2";
        }

        return new CallResult(
                workingStorageItem1,
                workingStorageItem2,
                localStorageItem1,
                localStorageItem2,
                outputItem1,
                outputItem2
        );
    }

    /**
     * Resets working-storage to initial values.
     * Equivalent to COBOL: CANCEL "sub-app"
     *
     * After CANCEL, the next CALL to the sub-program will find
     * working-storage variables at their initial (empty) values.
     */
    public void reset() {
        workingStorageItem1 = "";
        workingStorageItem2 = "";
    }

    /**
     * Returns the current working-storage item 1 value.
     * Useful for verifying state persistence between calls.
     */
    public String getWorkingStorageItem1() {
        return workingStorageItem1;
    }

    /**
     * Returns the current working-storage item 2 value.
     */
    public String getWorkingStorageItem2() {
        return workingStorageItem2;
    }
}
