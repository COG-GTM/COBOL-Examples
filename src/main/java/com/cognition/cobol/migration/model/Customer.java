package com.cognition.cobol.migration.model;

import java.util.Objects;

public class Customer {
    private int customerId;
    private String lastName;
    private String firstName;
    private int contractId;
    private String comment;

    public Customer() {}

    public Customer(int customerId, String lastName, String firstName, int contractId, String comment) {
        this.customerId = validateCustomerId(customerId);
        this.lastName = validateAndTrimString(lastName, 50, "lastName");
        this.firstName = validateAndTrimString(firstName, 50, "firstName");
        this.contractId = validateContractId(contractId);
        this.comment = validateAndTrimString(comment, 25, "comment");
    }

    private int validateCustomerId(int customerId) {
        if (customerId < 0 || customerId > 99999) {
            throw new IllegalArgumentException("Customer ID must be between 0 and 99999");
        }
        return customerId;
    }

    private int validateContractId(int contractId) {
        if (contractId < 0 || contractId > 99999) {
            throw new IllegalArgumentException("Contract ID must be between 0 and 99999");
        }
        return contractId;
    }

    private String validateAndTrimString(String value, int maxLength, String fieldName) {
        if (value == null) {
            return "";
        }
        if (value.length() > maxLength) {
            return value.substring(0, maxLength);
        }
        return value;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = validateCustomerId(customerId);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = validateAndTrimString(lastName, 50, "lastName");
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = validateAndTrimString(firstName, 50, "firstName");
    }

    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = validateContractId(contractId);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = validateAndTrimString(comment, 25, "comment");
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
        Customer customer = (Customer) o;
        return customerId == customer.customerId &&
               contractId == customer.contractId &&
               Objects.equals(lastName, customer.lastName) &&
               Objects.equals(firstName, customer.firstName) &&
               Objects.equals(comment, customer.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, lastName, firstName, contractId, comment);
    }
}
