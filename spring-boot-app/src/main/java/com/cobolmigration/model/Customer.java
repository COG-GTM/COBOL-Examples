package com.cobolmigration.model;

/**
 * POJO representing a customer record.
 *
 * <p>Migrated from the COBOL file-descriptor record in
 * {@code merge_sort/merge_sort_test.cbl} (lines 40-45):</p>
 * <pre>
 *   01  f-customer-record-sort.
 *       05  f-customer-id            pic 9(5).
 *       05  f-customer-last-name     pic x(50).
 *       05  f-customer-first-name    pic x(50).
 *       05  f-customer-contract-id   pic 9(5).
 *       05  f-customer-comment       pic x(25).
 * </pre>
 *
 * <p>This is a plain POJO (not JPA-managed) since the original COBOL
 * program uses file-based SORT/MERGE rather than database storage.</p>
 */
public class Customer {

    private int customerId;
    private String lastName;
    private String firstName;
    private int contractId;
    private String comment;

    public Customer() {
    }

    public Customer(int customerId, String lastName, String firstName,
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

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", contractId=" + contractId +
                ", comment='" + comment + '\'' +
                '}';
    }
}
