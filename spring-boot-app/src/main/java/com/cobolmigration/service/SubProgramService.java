package com.cobolmigration.service;

import org.springframework.stereotype.Service;

/**
 * Demonstrates the Java equivalent of COBOL subprogram CALL patterns.
 *
 * <p>Migrated from {@code sub_program/main_app.cbl} and
 * {@code sub_program/sub.cbl} which demonstrate CALL BY CONTENT
 * and CALL BY REFERENCE.</p>
 *
 * <h3>COBOL-to-Java Mapping</h3>
 * <ul>
 *   <li><b>CALL BY CONTENT</b> &rarr; Pass primitives or defensive copies
 *       (callee cannot modify caller's data)</li>
 *   <li><b>CALL BY REFERENCE</b> &rarr; Pass mutable objects
 *       (callee can modify the shared state)</li>
 *   <li><b>CANCEL</b> &rarr; Reset internal state to initial values</li>
 * </ul>
 *
 * <p>In COBOL, subprogram WORKING-STORAGE variables persist between calls
 * until a CANCEL is issued. In Java, this is modeled using instance fields
 * that retain state between method calls.</p>
 */
@Service
public class SubProgramService {

    private String workingStorageItem1 = "";
    private String workingStorageItem2 = "";

    /**
     * Simulates CALL BY CONTENT: accepts immutable values.
     * The caller's variables are not modified because Java passes
     * String values (immutable) rather than references to mutable storage.
     *
     * <p>Equivalent to:
     * <pre>
     *   CALL "sub-app" USING BY CONTENT ws-item-1 BY CONTENT ws-item-2
     * </pre>
     *
     * @param item1 first value (not modified by this method)
     * @param item2 second value (not modified by this method)
     * @return result containing the processed values
     */
    public SubProgramResult callByContent(String item1, String item2) {
        this.workingStorageItem1 = item1;
        this.workingStorageItem2 = item2;

        return new SubProgramResult(item1, item2,
                workingStorageItem1, workingStorageItem2);
    }

    /**
     * Simulates CALL BY REFERENCE: accepts a mutable holder that can be
     * modified by this method, just as the COBOL subprogram modifies
     * linkage-section variables.
     *
     * <p>Equivalent to:
     * <pre>
     *   CALL "sub-app" USING ws-item-1 ws-item-2
     * </pre>
     *
     * @param holder mutable container whose values may be changed
     * @return result containing the processed values
     */
    public SubProgramResult callByReference(MutableHolder holder) {
        this.workingStorageItem1 = holder.getItem1();
        this.workingStorageItem2 = holder.getItem2();

        holder.setItem1("replace1");
        holder.setItem2("replace2");

        return new SubProgramResult(holder.getItem1(), holder.getItem2(),
                workingStorageItem1, workingStorageItem2);
    }

    /**
     * Resets internal working-storage state, equivalent to COBOL CANCEL.
     *
     * <p>Equivalent to:
     * <pre>
     *   CANCEL "sub-app"
     * </pre>
     */
    public void cancel() {
        this.workingStorageItem1 = "";
        this.workingStorageItem2 = "";
    }

    public String getWorkingStorageItem1() {
        return workingStorageItem1;
    }

    public String getWorkingStorageItem2() {
        return workingStorageItem2;
    }

    /**
     * Mutable holder to simulate CALL BY REFERENCE parameter passing.
     */
    public static class MutableHolder {
        private String item1;
        private String item2;

        public MutableHolder() {
        }

        public MutableHolder(String item1, String item2) {
            this.item1 = item1;
            this.item2 = item2;
        }

        public String getItem1() {
            return item1;
        }

        public void setItem1(String item1) {
            this.item1 = item1;
        }

        public String getItem2() {
            return item2;
        }

        public void setItem2(String item2) {
            this.item2 = item2;
        }
    }

    /**
     * Immutable result returned from subprogram calls.
     */
    public static class SubProgramResult {
        private final String linkageItem1;
        private final String linkageItem2;
        private final String workingStorageItem1;
        private final String workingStorageItem2;

        public SubProgramResult(String linkageItem1, String linkageItem2,
                                String workingStorageItem1, String workingStorageItem2) {
            this.linkageItem1 = linkageItem1;
            this.linkageItem2 = linkageItem2;
            this.workingStorageItem1 = workingStorageItem1;
            this.workingStorageItem2 = workingStorageItem2;
        }

        public String getLinkageItem1() {
            return linkageItem1;
        }

        public String getLinkageItem2() {
            return linkageItem2;
        }

        public String getWorkingStorageItem1() {
            return workingStorageItem1;
        }

        public String getWorkingStorageItem2() {
            return workingStorageItem2;
        }
    }
}
