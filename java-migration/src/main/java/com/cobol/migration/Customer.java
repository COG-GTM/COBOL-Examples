package com.cobol.migration;

import java.util.Objects;

public class Customer {
    private int customerId;
    private String customerLastName;
    private String customerFirstName;
    private int customerContractId;
    private String customerComment;

    public Customer() {
    }

    public Customer(int customerId, String customerLastName, String customerFirstName, 
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

    public String toFileFormat() {
        return String.format("%05d%-50s%-50s%05d%-25s",
                customerId,
                padRight(customerLastName, 50),
                padRight(customerFirstName, 50),
                customerContractId,
                padRight(customerComment, 25));
    }

    public static Customer fromFileFormat(String line) {
        if (line == null || line.length() < 135) {
            throw new IllegalArgumentException("Invalid customer record format");
        }

        Customer customer = new Customer();
        customer.setCustomerId(Integer.parseInt(line.substring(0, 5).trim()));
        customer.setCustomerLastName(line.substring(5, 55).trim());
        customer.setCustomerFirstName(line.substring(55, 105).trim());
        customer.setCustomerContractId(Integer.parseInt(line.substring(105, 110).trim()));
        customer.setCustomerComment(line.substring(110, 135).trim());

        return customer;
    }

    private static String padRight(String str, int length) {
        if (str == null) {
            str = "";
        }
        if (str.length() >= length) {
            return str.substring(0, length);
        }
        return String.format("%-" + length + "s", str);
    }

    @Override
    public String toString() {
        return toFileFormat();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return customerId == customer.customerId &&
                customerContractId == customer.customerContractId &&
                Objects.equals(customerLastName, customer.customerLastName) &&
                Objects.equals(customerFirstName, customer.customerFirstName) &&
                Objects.equals(customerComment, customer.customerComment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, customerLastName, customerFirstName, 
                          customerContractId, customerComment);
    }
}
