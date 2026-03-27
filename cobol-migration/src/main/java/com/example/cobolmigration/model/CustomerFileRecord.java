package com.example.cobolmigration.model;

/**
 * File-based customer model (not JPA) matching the COBOL record at
 * merge_sort/merge_sort_test.cbl (lines 41-45):
 *   f-customer-id          pic 9(5)
 *   f-customer-last-name   pic x(50)
 *   f-customer-first-name  pic x(50)
 *   f-customer-contract-id pic 9(5)
 *   f-customer-comment     pic x(25)
 */
public class CustomerFileRecord {

    private int customerId;
    private String lastName;
    private String firstName;
    private int contractId;
    private String comment;

    public CustomerFileRecord() {
    }

    public CustomerFileRecord(int customerId, String lastName, String firstName,
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
}
