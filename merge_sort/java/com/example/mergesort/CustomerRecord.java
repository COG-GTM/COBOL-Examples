package com.example.mergesort;

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
        this.lastName = padOrTruncate(lastName, 50);
        this.firstName = padOrTruncate(firstName, 50);
        this.contractId = contractId;
        this.comment = padOrTruncate(comment, 25);
    }

    private String padOrTruncate(String value, int length) {
        if (value == null) {
            value = "";
        }
        if (value.length() > length) {
            return value.substring(0, length);
        }
        return String.format("%-" + length + "s", value);
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
        this.lastName = padOrTruncate(lastName, 50);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = padOrTruncate(firstName, 50);
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
        this.comment = padOrTruncate(comment, 25);
    }

    @Override
    public String toString() {
        return String.format("%05d%s%s%05d%s", 
            customerId, lastName, firstName, contractId, comment);
    }

    public static CustomerRecord fromString(String line) {
        if (line == null || line.length() < 135) {
            throw new IllegalArgumentException("Invalid record format");
        }
        
        CustomerRecord record = new CustomerRecord();
        record.setCustomerId(Integer.parseInt(line.substring(0, 5).trim()));
        record.setLastName(line.substring(5, 55));
        record.setFirstName(line.substring(55, 105));
        record.setContractId(Integer.parseInt(line.substring(105, 110).trim()));
        record.setComment(line.substring(110, 135));
        
        return record;
    }
}
