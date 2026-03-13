package com.cobolmigration.service;

import org.springframework.stereotype.Service;

/**
 * Singleton service that retains state between calls, mimicking COBOL
 * subprogram working-storage behavior.
 *
 * Migrated from: sub_program/sub.cbl
 *
 * In COBOL:
 *   - WORKING-STORAGE values persist between calls until CANCEL is issued
 *   - LOCAL-STORAGE values are reinitialized on each call
 *   - LINKAGE SECTION defines parameters passed from the caller
 *   - When called BY REFERENCE, changes to linkage variables are visible to the caller
 *   - When called BY CONTENT, caller's variables are not modified
 *
 * In Java, this @Service is singleton-scoped by default, so instance fields
 * persist between method calls (like COBOL working-storage).
 */
@Service
public class SubProgramService {

    // Working-storage equivalent: persists between calls (singleton scope)
    private String wsTestItem1 = "";
    private String wsTestItem2 = "";

    /**
     * Mutable holder class for pass-by-reference semantics.
     * When the caller passes a MutableString, modifications made inside
     * this method are visible to the caller (like COBOL CALL BY REFERENCE).
     */
    public static class MutableString {
        private String value;

        public MutableString(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Result class capturing the state at various points during execution,
     * analogous to the COBOL DISPLAY statements in the sub program.
     */
    public record ExecutionResult(
            String inputItem1,
            String inputItem2,
            String wsItem1AtStart,
            String wsItem2AtStart,
            String wsItem1AtEnd,
            String wsItem2AtEnd
    ) {
    }

    /**
     * Executes the subprogram logic with pass-by-reference parameters.
     * Modifications to param1 and param2 are visible to the caller.
     *
     * Mirrors the COBOL sub program behavior:
     *   1. Displays input linkage values
     *   2. Displays working-storage values (persisted from prior calls)
     *   3. Moves linkage values to working-storage
     *   4. Sets linkage values to new replacement values
     *
     * @param param1 mutable reference parameter (like CALL BY REFERENCE)
     * @param param2 mutable reference parameter (like CALL BY REFERENCE)
     * @return execution result with state snapshots
     */
    public ExecutionResult executeByReference(MutableString param1, MutableString param2) {
        String wsItem1AtStart = wsTestItem1;
        String wsItem2AtStart = wsTestItem2;

        // Move linkage values to working-storage
        wsTestItem1 = param1.getValue();
        wsTestItem2 = param2.getValue();

        String inputItem1 = param1.getValue();
        String inputItem2 = param2.getValue();

        // Modify the linkage parameters (visible to caller via reference)
        param1.setValue("replace1");
        param2.setValue("replace2");

        return new ExecutionResult(
                inputItem1, inputItem2,
                wsItem1AtStart, wsItem2AtStart,
                wsTestItem1, wsTestItem2
        );
    }

    /**
     * Executes the subprogram logic with pass-by-content (immutable) parameters.
     * The original string values cannot be modified by this method.
     *
     * @param item1 immutable value (like CALL BY CONTENT)
     * @param item2 immutable value (like CALL BY CONTENT)
     * @return execution result with state snapshots
     */
    public ExecutionResult executeByContent(String item1, String item2) {
        String wsItem1AtStart = wsTestItem1;
        String wsItem2AtStart = wsTestItem2;

        // Move values to working-storage
        wsTestItem1 = item1;
        wsTestItem2 = item2;

        return new ExecutionResult(
                item1, item2,
                wsItem1AtStart, wsItem2AtStart,
                wsTestItem1, wsTestItem2
        );
    }

    /**
     * Resets working-storage to initial values.
     * Equivalent to COBOL's CANCEL statement which resets working-storage
     * of a subprogram back to its initial values.
     */
    public void cancel() {
        wsTestItem1 = "";
        wsTestItem2 = "";
    }

    /**
     * Returns the current working-storage item 1 value.
     */
    public String getWsTestItem1() {
        return wsTestItem1;
    }

    /**
     * Returns the current working-storage item 2 value.
     */
    public String getWsTestItem2() {
        return wsTestItem2;
    }
}
