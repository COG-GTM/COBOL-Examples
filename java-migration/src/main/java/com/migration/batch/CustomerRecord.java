package com.migration.batch;

/**
 * Phase 4: File Processing - Customer Record
 *
 * Maps to the COBOL file record structure from merge_sort/merge_sort_test.cbl:
 *
 * COBOL:
 *   05 f-customer-id            PIC 9(5).
 *   05 f-customer-last-name     PIC X(50).
 *   05 f-customer-first-name    PIC X(50).
 *   05 f-customer-contract-id   PIC 9(5).
 *   05 f-customer-comment       PIC X(25).
 */
public record CustomerRecord(
        int customerId,
        String lastName,
        String firstName,
        int contractId,
        String comment
) implements Comparable<CustomerRecord> {

    @Override
    public int compareTo(CustomerRecord other) {
        return Integer.compare(this.customerId, other.customerId);
    }

    /**
     * Formats the record as a fixed-width string matching COBOL output format.
     * Total width: 5 + 50 + 50 + 5 + 25 = 135 characters.
     */
    public String toFixedWidth() {
        return String.format("%-5d%-50s%-50s%-5d%-25s",
                customerId,
                lastName,
                firstName,
                contractId,
                comment);
    }

    /**
     * Parses a fixed-width line back into a CustomerRecord.
     */
    public static CustomerRecord fromFixedWidth(String line) {
        if (line.length() < 135) {
            line = String.format("%-135s", line);
        }
        int id = Integer.parseInt(line.substring(0, 5).strip());
        String last = line.substring(5, 55);
        String first = line.substring(55, 105);
        int contract = Integer.parseInt(line.substring(105, 110).strip());
        String cmt = line.substring(110, 135);
        return new CustomerRecord(id, last, first, contract, cmt);
    }
}
