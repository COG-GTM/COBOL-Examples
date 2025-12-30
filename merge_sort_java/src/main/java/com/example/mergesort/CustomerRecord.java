package com.example.mergesort;

import java.util.Objects;

/**
 * Customer record data structure matching the COBOL record layout:
 * - customer-id: 5 digits (pic 9(5))
 * - customer-last-name: 50 characters (pic x(50))
 * - customer-first-name: 50 characters (pic x(50))
 * - customer-contract-id: 5 digits (pic 9(5))
 * - customer-comment: 25 characters (pic x(25))
 */
public class CustomerRecord {
    private int customerId;
    private String customerLastName;
    private String customerFirstName;
    private int customerContractId;
    private String customerComment;

    public CustomerRecord(int customerId, String customerLastName, String customerFirstName,
                          int customerContractId, String customerComment) {
        this.customerId = customerId;
        this.customerLastName = customerLastName;
        this.customerFirstName = customerFirstName;
        this.customerContractId = customerContractId;
        this.customerComment = customerComment;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public int getCustomerContractId() {
        return customerContractId;
    }

    public void setCustomerContractId(int customerContractId) {
        this.customerContractId = customerContractId;
    }

    public String getCustomerComment() {
        return customerComment;
    }

    public void setCustomerComment(String customerComment) {
        this.customerComment = customerComment;
    }

    /**
     * Format the record similar to COBOL fixed-width output.
     * Matches the COBOL record layout with proper field widths.
     */
    @Override
    public String toString() {
        return String.format("%05d%-50s%-50s%05d%-25s",
                customerId,
                padRight(customerLastName, 50),
                padRight(customerFirstName, 50),
                customerContractId,
                padRight(customerComment, 25));
    }

    private String padRight(String str, int length) {
        if (str == null) {
            str = "";
        }
        if (str.length() >= length) {
            return str.substring(0, length);
        }
        return String.format("%-" + length + "s", str);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerRecord that = (CustomerRecord) o;
        return customerId == that.customerId &&
                customerContractId == that.customerContractId &&
                Objects.equals(customerLastName, that.customerLastName) &&
                Objects.equals(customerFirstName, that.customerFirstName) &&
                Objects.equals(customerComment, that.customerComment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, customerLastName, customerFirstName, customerContractId, customerComment);
    }
}
