package com.cobolmigration.service;

import org.springframework.stereotype.Service;

/**
 * Service replacing the CALL by-content/by-reference pattern from
 * sub_program/main_app.cbl and sub_program/sub.cbl.
 *
 * <p>COBOL subprogram semantics mapping:
 * <ul>
 *   <li>CALL "sub-app" USING BY CONTENT ws-item-1 BY CONTENT ws-item-2
 *       &rarr; Pass immutable/copied values (Java: pass String copies)</li>
 *   <li>CALL "sub-app" USING ws-item-1 ws-item-2 (BY REFERENCE, default)
 *       &rarr; Pass mutable objects (Java: pass mutable wrapper)</li>
 *   <li>CANCEL "sub-app" &rarr; Reset working-storage (Java: {@link #cancel()})</li>
 * </ul>
 *
 * <p>COBOL storage section mapping:
 * <ul>
 *   <li>WORKING-STORAGE SECTION &rarr; Instance fields (persist between calls)</li>
 *   <li>LOCAL-STORAGE SECTION &rarr; Method-local variables (fresh each call)</li>
 *   <li>LINKAGE SECTION &rarr; Method parameters</li>
 * </ul>
 *
 * @see sub_program/main_app.cbl
 * @see sub_program/sub.cbl
 */
@Service
public class SubProgramService {

    // Working-storage: persists between calls until cancel() is invoked
    private String wsTestItem1 = "";
    private String wsTestItem2 = "";

    /**
     * Mutable wrapper for by-reference parameter passing.
     * In COBOL, CALL BY REFERENCE allows the subprogram to modify the caller's variables
     * (sub_program/main_app.cbl lines 47-48).
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
     * Result from a subprogram call, containing the working-storage and local-storage
     * values at the start and end of execution.
     */
    public static class SubProgramResult {
        private final String wsItem1AtStart;
        private final String wsItem2AtStart;
        private final String wsItem1AtEnd;
        private final String wsItem2AtEnd;
        private final String outputItem1;
        private final String outputItem2;

        public SubProgramResult(String wsItem1AtStart, String wsItem2AtStart,
                                String wsItem1AtEnd, String wsItem2AtEnd,
                                String outputItem1, String outputItem2) {
            this.wsItem1AtStart = wsItem1AtStart;
            this.wsItem2AtStart = wsItem2AtStart;
            this.wsItem1AtEnd = wsItem1AtEnd;
            this.wsItem2AtEnd = wsItem2AtEnd;
            this.outputItem1 = outputItem1;
            this.outputItem2 = outputItem2;
        }

        public String getWsItem1AtStart() { return wsItem1AtStart; }
        public String getWsItem2AtStart() { return wsItem2AtStart; }
        public String getWsItem1AtEnd() { return wsItem1AtEnd; }
        public String getWsItem2AtEnd() { return wsItem2AtEnd; }
        public String getOutputItem1() { return outputItem1; }
        public String getOutputItem2() { return outputItem2; }
    }

    /**
     * Call by content: passes copies of the values.
     * The caller's variables are NOT modified (main_app.cbl lines 35-38).
     *
     * @param item1 first value (immutable copy)
     * @param item2 second value (immutable copy)
     * @return the result showing working-storage state
     */
    public SubProgramResult callByContent(String item1, String item2) {
        // Local-storage: fresh on each call (sub.cbl lines 23-25)
        String lsTestItem1 = "";
        String lsTestItem2 = "";

        String wsAtStart1 = wsTestItem1;
        String wsAtStart2 = wsTestItem2;

        // Move linkage values to working-storage and local-storage (sub.cbl lines 45-48)
        wsTestItem1 = item1;
        wsTestItem2 = item2;
        lsTestItem1 = item1;
        lsTestItem2 = item2;

        // By content: replacements don't affect caller (sub.cbl lines 52-53)
        // We return the replaced values but the caller's original strings are unchanged
        return new SubProgramResult(wsAtStart1, wsAtStart2,
                wsTestItem1, wsTestItem2,
                "replace1", "replace2");
    }

    /**
     * Call by reference: passes mutable references.
     * The caller's variables CAN be modified (main_app.cbl lines 47-49).
     *
     * @param item1 first value (mutable reference)
     * @param item2 second value (mutable reference)
     * @return the result showing working-storage state
     */
    public SubProgramResult callByReference(MutableString item1, MutableString item2) {
        // Local-storage: fresh on each call
        String lsTestItem1 = "";
        String lsTestItem2 = "";

        String wsAtStart1 = wsTestItem1;
        String wsAtStart2 = wsTestItem2;

        // Move linkage values to working-storage and local-storage
        wsTestItem1 = item1.getValue();
        wsTestItem2 = item2.getValue();
        lsTestItem1 = item1.getValue();
        lsTestItem2 = item2.getValue();

        // By reference: modifications are visible to the caller (sub.cbl lines 52-53)
        item1.setValue("replace1");
        item2.setValue("replace2");

        return new SubProgramResult(wsAtStart1, wsAtStart2,
                wsTestItem1, wsTestItem2,
                item1.getValue(), item2.getValue());
    }

    /**
     * Cancel/reset the subprogram.
     * Replaces CANCEL "sub-app" (main_app.cbl line 55) which resets
     * all WORKING-STORAGE variables to their initial values.
     */
    public void cancel() {
        wsTestItem1 = "";
        wsTestItem2 = "";
    }

    /**
     * Returns current working-storage item 1 value for inspection.
     */
    public String getWsTestItem1() {
        return wsTestItem1;
    }

    /**
     * Returns current working-storage item 2 value for inspection.
     */
    public String getWsTestItem2() {
        return wsTestItem2;
    }
}
