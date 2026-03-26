package com.cobolmigration.service;

import com.cobolmigration.model.MutableHolder;
import org.springframework.stereotype.Service;

/**
 * Service demonstrating COBOL subprogram calling conventions.
 * Replaces: sub_program/sub.cbl (sub-app)
 *
 * COBOL subprogram sections and their Java equivalents:
 *   WORKING-STORAGE SECTION -> Service instance fields (persist between calls as Spring singleton)
 *   LOCAL-STORAGE SECTION   -> Method local variables (re-initialized each call)
 *   LINKAGE SECTION         -> Method parameters
 *
 * In COBOL:
 *   Working-storage values persist until CANCEL is called on the subprogram.
 *   Local-storage values are fresh on each call.
 *   Linkage section variables are the parameters passed from the caller.
 */
@Service
public class SubProgramService {

    // Working-storage equivalent: persists between calls (singleton service)
    private String wsTestItem1 = "";
    private String wsTestItem2 = "";

    /**
     * Process items passed BY CONTENT.
     * Replaces: CALL "sub-app" USING BY CONTENT ws-item-1 BY CONTENT ws-item-2
     *
     * The caller's data is NOT modified because Java Strings are immutable
     * and we accept them as plain parameters (copies).
     */
    public SubResult processByContent(String item1, String item2) {
        // Local-storage equivalent: fresh on each call
        String lsTestItem1 = "";
        String lsTestItem2 = "";

        SubResult result = new SubResult();
        result.wsAtStart1 = wsTestItem1;
        result.wsAtStart2 = wsTestItem2;
        result.lsAtStart1 = lsTestItem1;
        result.lsAtStart2 = lsTestItem2;

        // Move linkage values to working-storage and local-storage
        wsTestItem1 = item1;
        wsTestItem2 = item2;
        lsTestItem1 = item1;
        lsTestItem2 = item2;

        result.wsAtEnd1 = wsTestItem1;
        result.wsAtEnd2 = wsTestItem2;
        result.lsAtEnd1 = lsTestItem1;
        result.lsAtEnd2 = lsTestItem2;

        // BY CONTENT: we cannot modify the caller's variables
        // (String is immutable in Java, so this is naturally enforced)

        return result;
    }

    /**
     * Process items passed BY REFERENCE using MutableHolder.
     * Replaces: CALL "sub-app" USING ws-item-1 ws-item-2 (default BY REFERENCE)
     *
     * The caller's data CAN be modified because MutableHolder wraps the value.
     */
    public SubResult processByReference(MutableHolder<String> item1, MutableHolder<String> item2) {
        // Local-storage equivalent: fresh on each call
        String lsTestItem1 = "";
        String lsTestItem2 = "";

        SubResult result = new SubResult();
        result.wsAtStart1 = wsTestItem1;
        result.wsAtStart2 = wsTestItem2;
        result.lsAtStart1 = lsTestItem1;
        result.lsAtStart2 = lsTestItem2;

        // Move linkage values to working-storage and local-storage
        wsTestItem1 = item1.getValue();
        wsTestItem2 = item2.getValue();
        lsTestItem1 = item1.getValue();
        lsTestItem2 = item2.getValue();

        // Modify the caller's variables through the MutableHolder (BY REFERENCE)
        item1.setValue("replace1");
        item2.setValue("replace2");

        result.wsAtEnd1 = wsTestItem1;
        result.wsAtEnd2 = wsTestItem2;
        result.lsAtEnd1 = lsTestItem1;
        result.lsAtEnd2 = lsTestItem2;
        result.outputItem1 = item1.getValue();
        result.outputItem2 = item2.getValue();

        return result;
    }

    /**
     * Reset working-storage to initial values.
     * Replaces: CANCEL "sub-app" which resets all working-storage variables.
     */
    public void cancel() {
        wsTestItem1 = "";
        wsTestItem2 = "";
    }

    public String getWsTestItem1() {
        return wsTestItem1;
    }

    public String getWsTestItem2() {
        return wsTestItem2;
    }

    /**
     * Result of a subprogram call, capturing working-storage and local-storage
     * state at the start and end of the call for inspection.
     */
    public static class SubResult {
        private String wsAtStart1;
        private String wsAtStart2;
        private String lsAtStart1;
        private String lsAtStart2;
        private String wsAtEnd1;
        private String wsAtEnd2;
        private String lsAtEnd1;
        private String lsAtEnd2;
        private String outputItem1;
        private String outputItem2;

        public String getWsAtStart1() { return wsAtStart1; }
        public String getWsAtStart2() { return wsAtStart2; }
        public String getLsAtStart1() { return lsAtStart1; }
        public String getLsAtStart2() { return lsAtStart2; }
        public String getWsAtEnd1() { return wsAtEnd1; }
        public String getWsAtEnd2() { return wsAtEnd2; }
        public String getLsAtEnd1() { return lsAtEnd1; }
        public String getLsAtEnd2() { return lsAtEnd2; }
        public String getOutputItem1() { return outputItem1; }
        public String getOutputItem2() { return outputItem2; }
    }
}
