package com.example.migration.service;

import org.springframework.stereotype.Service;

/**
 * Service replacing the COBOL subprogram pattern from sub_program/.
 *
 * In COBOL (sub_program/main_app.cbl lines 35-38):
 *   CALL "sub-app" USING BY CONTENT ws-item-1 BY CONTENT ws-item-2
 *   CALL "sub-app" USING ws-item-1 ws-item-2  (by reference, default)
 *   CANCEL "sub-app"  (resets working-storage)
 *
 * The subprogram (sub_program/sub.cbl) has three storage sections:
 *   - WORKING-STORAGE: persists between calls until CANCEL
 *   - LOCAL-STORAGE: reinitialized on each call
 *   - LINKAGE: parameters passed from caller
 *
 * In Java/Spring:
 *   - WORKING-STORAGE -> instance fields (persist while bean lives)
 *   - LOCAL-STORAGE -> local variables in methods (fresh each call)
 *   - LINKAGE -> method parameters
 *   - BY CONTENT -> pass immutable copies (defensive copy)
 *   - BY REFERENCE -> pass mutable objects (caller sees changes)
 *   - CANCEL -> reset() method to reinitialize state
 */
@Service
public class SubProgramService {

    // Working-storage equivalents: persist between calls
    private String wsTestItem1 = "";
    private String wsTestItem2 = "";

    /**
     * Process by content (BY CONTENT in COBOL).
     * Parameters are treated as copies - the caller's variables are not modified.
     * This mirrors: CALL "sub-app" USING BY CONTENT ws-item-1 BY CONTENT ws-item-2
     *
     * @param item1 first parameter (passed by value)
     * @param item2 second parameter (passed by value)
     * @return result containing the processed values and working-storage state
     */
    public synchronized SubProgramResult processByContent(String item1, String item2) {
        // Local-storage equivalents: fresh on each call
        String lsTestItem1 = "";
        String lsTestItem2 = "";

        SubProgramResult result = new SubProgramResult();
        result.wsItem1AtStart = wsTestItem1;
        result.wsItem2AtStart = wsTestItem2;
        result.lsItem1AtStart = lsTestItem1;
        result.lsItem2AtStart = lsTestItem2;

        // Move linkage values to working-storage and local-storage
        wsTestItem1 = item1;
        wsTestItem2 = item2;
        lsTestItem1 = item1;
        lsTestItem2 = item2;

        result.wsItem1AtEnd = wsTestItem1;
        result.wsItem2AtEnd = wsTestItem2;
        result.lsItem1AtEnd = lsTestItem1;
        result.lsItem2AtEnd = lsTestItem2;

        // BY CONTENT: return replaced values but don't modify caller's copies
        result.outputItem1 = "replace1";
        result.outputItem2 = "replace2";

        return result;
    }

    /**
     * Process by reference (default CALL in COBOL).
     * Parameters can be modified and the caller sees changes.
     * This mirrors: CALL "sub-app" USING ws-item-1 ws-item-2
     *
     * @param items mutable array of [item1, item2] that will be modified
     * @return result containing the processed values and working-storage state
     */
    public synchronized SubProgramResult processByReference(String[] items) {
        String lsTestItem1 = "";
        String lsTestItem2 = "";

        SubProgramResult result = new SubProgramResult();
        result.wsItem1AtStart = wsTestItem1;
        result.wsItem2AtStart = wsTestItem2;
        result.lsItem1AtStart = lsTestItem1;
        result.lsItem2AtStart = lsTestItem2;

        wsTestItem1 = items[0];
        wsTestItem2 = items[1];
        lsTestItem1 = items[0];
        lsTestItem2 = items[1];

        result.wsItem1AtEnd = wsTestItem1;
        result.wsItem2AtEnd = wsTestItem2;
        result.lsItem1AtEnd = lsTestItem1;
        result.lsItem2AtEnd = lsTestItem2;

        // BY REFERENCE: modify the caller's variables
        items[0] = "replace1";
        items[1] = "replace2";
        result.outputItem1 = items[0];
        result.outputItem2 = items[1];

        return result;
    }

    /**
     * Resets working-storage to initial values.
     * Replaces: CANCEL "sub-app" (main_app.cbl line 55)
     */
    public synchronized void reset() {
        wsTestItem1 = "";
        wsTestItem2 = "";
    }

    public static class SubProgramResult {
        public String wsItem1AtStart;
        public String wsItem2AtStart;
        public String lsItem1AtStart;
        public String lsItem2AtStart;
        public String wsItem1AtEnd;
        public String wsItem2AtEnd;
        public String lsItem1AtEnd;
        public String lsItem2AtEnd;
        public String outputItem1;
        public String outputItem2;
    }
}
