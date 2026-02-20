package com.mergesort;

import java.util.Comparator;

public class CustomerRecord {

    public static final Comparator<CustomerRecord> BY_CUSTOMER_ID_ASC =
            Comparator.comparingInt(CustomerRecord::getCustomerId);

    public static final Comparator<CustomerRecord> BY_CONTRACT_ID_DESC =
            Comparator.comparingInt(CustomerRecord::getContractId).reversed();

    private int customerId;
    private String lastName;
    private String firstName;
    private int contractId;
    private String comment;

    public CustomerRecord(int customerId, String lastName, String firstName,
                          int contractId, String comment) {
        this.customerId = customerId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.contractId = contractId;
        this.comment = comment;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public int getContractId() {
        return contractId;
    }

    public String getComment() {
        return comment;
    }

    public String toFileString() {
        return String.format("%05d%-50s%-50s%05d%-25s",
                customerId, lastName, firstName, contractId, comment);
    }

    public static CustomerRecord fromFileString(String line) {
        if (line.length() < 135) {
            line = String.format("%-135s", line);
        }
        int id = Integer.parseInt(line.substring(0, 5).trim());
        String last = line.substring(5, 55).trim();
        String first = line.substring(55, 105).trim();
        int contract = Integer.parseInt(line.substring(105, 110).trim());
        String cmt = line.substring(110, 135).trim();
        return new CustomerRecord(id, last, first, contract, cmt);
    }

    @Override
    public String toString() {
        return toFileString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerRecord that = (CustomerRecord) o;
        return customerId == that.customerId
                && contractId == that.contractId
                && lastName.equals(that.lastName)
                && firstName.equals(that.firstName)
                && comment.equals(that.comment);
    }

    @Override
    public int hashCode() {
        int result = customerId;
        result = 31 * result + lastName.hashCode();
        result = 31 * result + firstName.hashCode();
        result = 31 * result + contractId;
        result = 31 * result + comment.hashCode();
        return result;
    }
}
