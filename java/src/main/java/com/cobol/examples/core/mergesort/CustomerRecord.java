package com.cobol.examples.core.mergesort;

/**
 * Customer record used by the merge/sort example.
 *
 * <p>Ported from the {@code f-customer-record} group item in
 * {@code merge_sort/merge_sort_test.cbl}.
 */
public record CustomerRecord(
        int id,
        String lastName,
        String firstName,
        int contractId,
        String comment) {

    @Override
    public String toString() {
        return "%05d %-50s %-50s %05d %-25s".formatted(id, lastName, firstName, contractId, comment);
    }
}
