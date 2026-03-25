package com.cobolmigration.service;

import org.springframework.stereotype.Service;

/**
 * Service replacing the COBOL subprogram call pattern in sub_program/.
 * Demonstrates the migration of COBOL CALL BY CONTENT, CALL BY REFERENCE,
 * and CANCEL patterns to Spring service method calls.
 *
 * COBOL concepts mapped:
 *   WORKING-STORAGE (persistent state) -> Spring @Service singleton bean fields
 *   LOCAL-STORAGE (per-call fresh state) -> method-local variables
 *   LINKAGE SECTION (parameters) -> method parameters
 *   CALL BY CONTENT (immutable) -> parameters passed as copies/immutable
 *   CALL BY REFERENCE (mutable) -> mutable wrapper objects or return values
 *   CANCEL (reset state) -> reset() method
 *
 * @see sub_program/main_app.cbl (lines 34-38, 47-49, 54-55)
 * @see sub_program/sub.cbl
 */
@Service
public class SubProgramService {

    // Replaces WORKING-STORAGE variables in sub.cbl (lines 18-19).
    // These persist across method calls until reset() is called.
    private String wsTestItem1 = "";
    private String wsTestItem2 = "";

    /**
     * Processes items by content (immutable parameters).
     * Replaces CALL "sub-app" USING BY CONTENT in main_app.cbl (lines 35-37).
     * The original parameters are NOT modified - copies are used internally.
     *
     * @param item1 first item value (treated as immutable copy)
     * @param item2 second item value (treated as immutable copy)
     * @return result containing the working-storage state
     */
    public SubProgramResult callByContent(String item1, String item2) {
        // LOCAL-STORAGE equivalent: fresh variables per call (sub.cbl lines 24-25)
        String lsTestItem1 = "";
        String lsTestItem2 = "";

        // Move linkage values to working-storage and local-storage (sub.cbl lines 45-48)
        wsTestItem1 = item1;
        wsTestItem2 = item2;
        lsTestItem1 = item1;
        lsTestItem2 = item2;

        return SubProgramResult.builder()
                .wsItem1(wsTestItem1)
                .wsItem2(wsTestItem2)
                .lsItem1(lsTestItem1)
                .lsItem2(lsTestItem2)
                .outputItem1(item1)
                .outputItem2(item2)
                .build();
    }

    /**
     * Processes items by reference (mutable parameters).
     * Replaces CALL "sub-app" USING (by reference) in main_app.cbl (lines 47-48).
     * The parameters can be modified and the caller sees the changes via the return value.
     *
     * @param item1 first item value (will be replaced in output)
     * @param item2 second item value (will be replaced in output)
     * @return result with modified output values (simulating by-reference modification)
     */
    public SubProgramResult callByReference(String item1, String item2) {
        // LOCAL-STORAGE equivalent: fresh per call
        String lsTestItem1 = "";
        String lsTestItem2 = "";

        // Move linkage values to working-storage and local-storage
        wsTestItem1 = item1;
        wsTestItem2 = item2;
        lsTestItem1 = item1;
        lsTestItem2 = item2;

        // Simulate sub.cbl lines 52-53: setting input variables to new values
        String modifiedItem1 = "replace1";
        String modifiedItem2 = "replace2";

        return SubProgramResult.builder()
                .wsItem1(wsTestItem1)
                .wsItem2(wsTestItem2)
                .lsItem1(lsTestItem1)
                .lsItem2(lsTestItem2)
                .outputItem1(modifiedItem1)
                .outputItem2(modifiedItem2)
                .build();
    }

    /**
     * Resets the service's persistent state.
     * Replaces CANCEL "sub-app" in main_app.cbl (lines 54-55).
     * After CANCEL in COBOL, the next call to the subprogram starts with
     * fresh WORKING-STORAGE values.
     */
    public void reset() {
        wsTestItem1 = "";
        wsTestItem2 = "";
    }

    /**
     * Returns current working-storage state for inspection.
     *
     * @return current values of persistent state fields
     */
    public String getWsTestItem1() {
        return wsTestItem1;
    }

    public String getWsTestItem2() {
        return wsTestItem2;
    }

    /**
     * Result object replacing COBOL linkage section output and working-storage inspection.
     */
    @lombok.Builder
    @lombok.Getter
    public static class SubProgramResult {
        private final String wsItem1;
        private final String wsItem2;
        private final String lsItem1;
        private final String lsItem2;
        private final String outputItem1;
        private final String outputItem2;
    }
}
