package com.example.migration.model;

/**
 * Replaces the COBOL file record structure from merge_sort/merge_sort_test.cbl (lines 48-53).
 *
 * COBOL structure (shared across all file descriptors):
 *   05 f-customer-id           pic 9(5).
 *   05 f-customer-last-name    pic x(50).
 *   05 f-customer-first-name   pic x(50).
 *   05 f-customer-contract-id  pic 9(5).
 *   05 f-customer-comment      pic x(25).
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

    @Override
    public int compareTo(CustomerRecord other) {
        return Integer.compare(this.customerId, other.customerId);
    }

    /**
     * Formats the record as a fixed-width string similar to COBOL DISPLAY output.
     */
    public String toFixedWidthString() {
        return String.format("%05d%-50s%-50s%05d%-25s",
                customerId,
                lastName != null ? lastName : "",
                firstName != null ? firstName : "",
                contractId,
                comment != null ? comment : "");
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
}
