package com.cobol.examples.mergesort;

/**
 * Customer class representing a customer record from the COBOL merge sort example.
 * Maps to the COBOL customer record structure with fields:
 * - f-customer-id (pic 9(5)) -> customerId (int)
 * - f-customer-last-name (pic x(50)) -> lastName (String)
 * - f-customer-first-name (pic x(50)) -> firstName (String)
 * - f-customer-contract-id (pic 9(5)) -> contractId (int)
 * - f-customer-comment (pic x(25)) -> comment (String)
 */
public class Customer implements Comparable<Customer> {
    private int customerId;
    private String lastName;
    private String firstName;
    private int contractId;
    private String comment;

    public Customer() {
    }

    public Customer(int customerId, String lastName, String firstName, int contractId, String comment) {
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
    public int compareTo(Customer other) {
        return Integer.compare(this.customerId, other.customerId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Customer customer = (Customer) obj;
        return customerId == customer.customerId &&
               contractId == customer.contractId &&
               lastName.equals(customer.lastName) &&
               firstName.equals(customer.firstName) &&
               comment.equals(customer.comment);
    }

    @Override
    public int hashCode() {
        return customerId * 31 + contractId;
    }

    @Override
    public String toString() {
        return String.format("%05d%-50s%-50s%05d%-25s", 
                           customerId, lastName, firstName, contractId, comment);
    }

    public String toDisplayString() {
        return String.format("ID: %d, Name: %s %s, Contract: %d, Comment: %s",
                           customerId, firstName, lastName, contractId, comment);
    }
}
