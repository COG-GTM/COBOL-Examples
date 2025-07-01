package com.example.mergesort;

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
        this.lastName = lastName != null ? lastName : "";
        this.firstName = firstName != null ? firstName : "";
        this.contractId = contractId;
        this.comment = comment != null ? comment : "";
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
        this.lastName = lastName != null ? lastName : "";
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName != null ? firstName : "";
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
        this.comment = comment != null ? comment : "";
    }

    public String toFileFormat() {
        return String.format("%05d%-50s%-50s%05d%-25s",
                customerId,
                padRight(lastName, 50),
                padRight(firstName, 50),
                contractId,
                padRight(comment, 25));
    }

    public static CustomerRecord fromFileFormat(String line) {
        if (line == null || line.length() < 135) {
            throw new IllegalArgumentException("Invalid line format: line must be at least 135 characters");
        }

        try {
            int customerId = Integer.parseInt(line.substring(0, 5));
            String lastName = line.substring(5, 55).trim();
            String firstName = line.substring(55, 105).trim();
            int contractId = Integer.parseInt(line.substring(105, 110));
            String comment = line.substring(110, 135).trim();

            return new CustomerRecord(customerId, lastName, firstName, contractId, comment);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in line: " + line, e);
        }
    }

    private static String padRight(String str, int length) {
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

    @Override
    public String toString() {
        return String.format("CustomerRecord{customerId=%d, lastName='%s', firstName='%s', contractId=%d, comment='%s'}",
                customerId, lastName, firstName, contractId, comment);
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
}
