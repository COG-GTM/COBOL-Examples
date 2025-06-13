package com.example.mergesort;

public class Customer {
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
        StringBuilder sb = new StringBuilder(str);
        while (sb.length() < length) {
            sb.append(' ');
        }
        return sb.toString();
    }

    public static Customer fromString(String line) {
        if (line == null || line.length() < 135) {
            throw new IllegalArgumentException("Invalid customer record format");
        }
        
        int customerId = Integer.parseInt(line.substring(0, 5));
        String lastName = line.substring(5, 55).trim();
        String firstName = line.substring(55, 105).trim();
        int contractId = Integer.parseInt(line.substring(105, 110));
        String comment = line.substring(110, 135).trim();
        
        return new Customer(customerId, lastName, firstName, contractId, comment);
    }
}
