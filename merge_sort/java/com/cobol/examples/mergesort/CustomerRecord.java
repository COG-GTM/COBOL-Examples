package com.cobol.examples.mergesort;

import java.util.Comparator;
import java.util.Objects;

public class CustomerRecord {
    private int customerId;
    private String lastName;
    private String firstName;
    private int contractId;
    private String comment;

    public CustomerRecord() {
    }

    public CustomerRecord(int customerId, String lastName, String firstName, int contractId, String comment) {
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
        return String.format("%05d%-50s%-50s%05d%-25s", 
            customerId, 
            padRight(lastName, 50), 
            padRight(firstName, 50), 
            contractId, 
            padRight(comment, 25));
    }

    private String padRight(String str, int length) {
        if (str == null) str = "";
        if (str.length() >= length) {
            return str.substring(0, length);
        }
        return str + " ".repeat(length - str.length());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerRecord that = (CustomerRecord) o;
        return customerId == that.customerId &&
               contractId == that.contractId &&
               Objects.equals(lastName, that.lastName) &&
               Objects.equals(firstName, that.firstName) &&
               Objects.equals(comment, that.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, lastName, firstName, contractId, comment);
    }

    public static final Comparator<CustomerRecord> BY_CUSTOMER_ID = 
        Comparator.comparingInt(CustomerRecord::getCustomerId);

    public static final Comparator<CustomerRecord> BY_CONTRACT_ID_DESC = 
        Comparator.comparingInt(CustomerRecord::getContractId).reversed();
}
