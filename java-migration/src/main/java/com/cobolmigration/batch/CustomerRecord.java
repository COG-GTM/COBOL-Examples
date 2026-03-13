package com.cobolmigration.batch;

/**
 * Model class for customer records used in batch merge/sort operations.
 *
 * Migrated from: merge_sort/merge_sort_test.cbl (lines 40-77)
 *
 * Original COBOL FD structure (same across all file descriptors):
 *   05  f-customer-id            PIC 9(5).
 *   05  f-customer-last-name     PIC X(50).
 *   05  f-customer-first-name    PIC X(50).
 *   05  f-customer-contract-id   PIC 9(5).
 *   05  f-customer-comment       PIC X(25).
 *
 * Total record length: 135 characters (fixed-width)
 */
public class CustomerRecord implements Comparable<CustomerRecord> {

    private int customerId;
    private String lastName;
    private String firstName;
    private int contractId;
    private String comment;

    public CustomerRecord() {
    }

    public CustomerRecord(int customerId, String lastName, String firstName,
                          int contractId, String comment) {
        this.customerId = customerId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.contractId = contractId;
        this.comment = comment;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Default comparison by customer-id ascending.
     * Matches COBOL: MERGE ... ON ASCENDING KEY f-customer-id
     */
    @Override
    public int compareTo(CustomerRecord other) {
        return Integer.compare(this.customerId, other.customerId);
    }

    /**
     * Formats the record as a fixed-width string matching COBOL's record layout.
     * customer-id(5) + last-name(50) + first-name(50) + contract-id(5) + comment(25) = 135 chars
     */
    public String toFixedWidthString() {
        return String.format("%05d%-50s%-50s%05d%-25s",
                customerId,
                padOrTruncate(lastName, 50),
                padOrTruncate(firstName, 50),
                contractId,
                padOrTruncate(comment, 25));
    }

    /**
     * Parses a CustomerRecord from a fixed-width string.
     */
    public static CustomerRecord fromFixedWidthString(String line) {
        if (line.length() < 135) {
            line = String.format("%-135s", line);
        }
        CustomerRecord record = new CustomerRecord();
        record.setCustomerId(Integer.parseInt(line.substring(0, 5).trim()));
        record.setLastName(line.substring(5, 55).trim());
        record.setFirstName(line.substring(55, 105).trim());
        record.setContractId(Integer.parseInt(line.substring(105, 110).trim()));
        record.setComment(line.substring(110, 135).trim());
        return record;
    }

    @Override
    public String toString() {
        return toFixedWidthString();
    }

    private static String padOrTruncate(String value, int length) {
        if (value == null) {
            return " ".repeat(length);
        }
        if (value.length() > length) {
            return value.substring(0, length);
        }
        return String.format("%-" + length + "s", value);
    }
}
