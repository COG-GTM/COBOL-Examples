package com.mergesort;

import java.util.Objects;

public class CustomerRecord {

    private int customerId;
    private String customerLastName;
    private String customerFirstName;
    private int customerContractId;
    private String customerComment;

    public CustomerRecord(int customerId, String customerLastName,
                          String customerFirstName, int customerContractId,
                          String customerComment) {
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

    public String toFixedWidthString() {
        return String.format("%-5d%-50s%-50s%-5d%-25s",
                customerId, customerLastName, customerFirstName,
                customerContractId, customerComment);
    }

    public static CustomerRecord fromFixedWidthString(String line) {
        if (line.length() < 135) {
            line = String.format("%-135s", line);
        }
        int id = Integer.parseInt(line.substring(0, 5).trim());
        String lastName = line.substring(5, 55).trim();
        String firstName = line.substring(55, 105).trim();
        int contractId = Integer.parseInt(line.substring(105, 110).trim());
        String comment = line.substring(110, 135).trim();
        return new CustomerRecord(id, lastName, firstName, contractId, comment);
    }

    @Override
    public String toString() {
        return toFixedWidthString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerRecord that = (CustomerRecord) o;
        return customerId == that.customerId
                && customerContractId == that.customerContractId
                && Objects.equals(customerLastName, that.customerLastName)
                && Objects.equals(customerFirstName, that.customerFirstName)
                && Objects.equals(customerComment, that.customerComment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, customerLastName, customerFirstName,
                customerContractId, customerComment);
    }
}
