package com.cobolmigration.service;

import org.springframework.stereotype.Service;

/**
 * Replaces sub_program/sub.cbl and sub_program/main_app.cbl.
 *
 * COBOL concepts mapped to Java:
 * - CALL "sub-app" USING BY CONTENT -> method call with immutable parameters (pass copies)
 * - CALL "sub-app" USING BY REFERENCE -> method call that returns modified values
 * - WORKING-STORAGE state retention between calls -> instance variables (Spring singleton)
 * - LOCAL-STORAGE fresh on each call -> local variables in the method
 * - CANCEL "sub-app" -> resetState() method to clear working storage
 */
@Service
public class SubProgramService {

    // WORKING-STORAGE SECTION equivalents - persist between calls (Spring singleton)
    private String wsTestItem1 = "";
    private String wsTestItem2 = "";

    /**
     * Result holder for sub-program execution.
     */
    public record SubProgramResult(String item1, String item2,
                                   String wsItem1, String wsItem2,
                                   String lsItem1, String lsItem2) {
    }

    /**
     * Simulates CALL BY CONTENT - parameters are not modified.
     * The caller's variables remain unchanged after the call.
     * Source: main_app.cbl lines 34-38
     */
    public SubProgramResult callByContent(String item1, String item2) {
        // LOCAL-STORAGE equivalents - fresh on each call
        String lsItem1 = "";
        String lsItem2 = "";

        // Move linkage values to working and local storage
        wsTestItem1 = item1;
        wsTestItem2 = item2;
        lsItem1 = item1;
        lsItem2 = item2;

        // BY CONTENT: input parameters are copies, originals not modified
        // We return a result but the caller's variables are unchanged
        return new SubProgramResult(
                item1, item2,      // original values (unchanged for BY CONTENT)
                wsTestItem1, wsTestItem2,
                lsItem1, lsItem2
        );
    }

    /**
     * Simulates CALL BY REFERENCE - parameters can be modified.
     * The caller sees the modified values after the call.
     * Source: main_app.cbl lines 46-49
     */
    public SubProgramResult callByReference(String item1, String item2) {
        // LOCAL-STORAGE equivalents - fresh on each call
        String lsItem1 = "";
        String lsItem2 = "";

        // Move linkage values to working and local storage
        wsTestItem1 = item1;
        wsTestItem2 = item2;
        lsItem1 = item1;
        lsItem2 = item2;

        // BY REFERENCE: modify the input values (sub.cbl lines 52-53)
        String modifiedItem1 = "replace1";
        String modifiedItem2 = "replace2";

        return new SubProgramResult(
                modifiedItem1, modifiedItem2,  // modified values returned to caller
                wsTestItem1, wsTestItem2,
                lsItem1, lsItem2
        );
    }

    /**
     * Simulates CANCEL "sub-app" - resets WORKING-STORAGE to initial values.
     * Source: main_app.cbl lines 54-55
     */
    public void resetState() {
        wsTestItem1 = "";
        wsTestItem2 = "";
    }

    /**
     * Returns current WORKING-STORAGE values.
     */
    public String getWsTestItem1() {
        return wsTestItem1;
    }

    public String getWsTestItem2() {
        return wsTestItem2;
    }
}
