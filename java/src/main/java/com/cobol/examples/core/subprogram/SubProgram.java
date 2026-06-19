package com.cobol.examples.core.subprogram;

/**
 * Pure-logic port of {@code sub_program/main_app.cbl} + {@code sub.cbl}.
 *
 * <p>The COBOL pair demonstrated three call conventions:
 * <ul>
 *   <li>{@code CALL ... BY CONTENT} — the callee cannot change the caller's data;</li>
 *   <li>{@code CALL ... BY REFERENCE} — the callee can mutate the caller's data;</li>
 *   <li>{@code WORKING-STORAGE} persisting across calls until {@code CANCEL},
 *       whereas {@code LOCAL-STORAGE} is fresh every call.</li>
 * </ul>
 *
 * <p>Java is always pass-by-value of references, so "by content" is modelled by
 * passing immutable strings and "by reference" by passing a mutable
 * {@link MutableField}. The persistent {@code WORKING-STORAGE} is held as
 * instance state; {@link #cancel()} resets it.
 */
public final class SubProgram {

    /** A mutable string cell, standing in for a {@code BY REFERENCE} argument. */
    public static final class MutableField {
        private String value;

        public MutableField(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }

        public void set(String value) {
            this.value = value;
        }
    }

    private String workingStorage1 = "";
    private String workingStorage2 = "";

    /** Snapshot of the persistent working-storage values seen on entry to a call. */
    public record CallResult(String workingStorageOnEntry1, String workingStorageOnEntry2) {
    }

    /**
     * Mirrors the sub program: records the persistent WS values seen on entry,
     * copies the linkage arguments into working storage, then overwrites the
     * (by-reference) arguments with {@code replace1}/{@code replace2}.
     */
    public CallResult call(MutableField arg1, MutableField arg2) {
        CallResult onEntry = new CallResult(workingStorage1, workingStorage2);

        workingStorage1 = arg1.value();
        workingStorage2 = arg2.value();

        arg1.set("replace1");
        arg2.set("replace2");

        return onEntry;
    }

    /** Equivalent of {@code CANCEL "sub-app"}: resets persistent working storage. */
    public void cancel() {
        workingStorage1 = "";
        workingStorage2 = "";
    }
}
