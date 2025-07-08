package com.example.mergesort;

import java.util.Objects;

/**
 * Represents a customer record with the same structure as the COBOL implementation.
 * Fields match the COBOL record structure from merge_sort_test.cbl lines 41-45.
 */
public class CustomerRecord implements Comparable<CustomerRecord> {
    private int customerId;
    private String customerLastName;
    private String customerFirstName;
    private int customerContractId;
    private String customerComment;

    public CustomerRecord() {
    }

    public CustomerRecord(int customerId, String customerLastName, String customerFirstName, 
                         int customerContractId, String customerComment) {
        this.customerId = customerId;
        this.customerLastName = customerLastName != null ? customerLastName : "";
        this.customerFirstName = customerFirstName != null ? customerFirstName : "";
        this.customerContractId = customerContractId;
        this.customerComment = customerComment != null ? customerComment : "";
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
        this.customerLastName = customerLastName != null ? customerLastName : "";
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName != null ? customerFirstName : "";
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
        this.customerComment = customerComment != null ? customerComment : "";
    }

    @Override
    public int compareTo(CustomerRecord other) {
        return Integer.compare(this.customerId, other.customerId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CustomerRecord that = (CustomerRecord) obj;
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
        if (str == null) str = "";
        if (str.length() >= length) {
            return str.substring(0, length);
        }
        StringBuilder sb = new StringBuilder(str);
        while (sb.length() < length) {
            sb.append(' ');
        }
        return sb.toString();
    }
}
