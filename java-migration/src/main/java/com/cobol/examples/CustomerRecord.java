package com.cobol.examples;

/**
 * Java representation of COBOL customer record structure.
 * Maps to COBOL PIC clauses:
 * - f-customer-id: pic 9(5) -> int
 * - f-customer-last-name: pic x(50) -> String
 * - f-customer-first-name: pic x(50) -> String
 * - f-customer-contract-id: pic 9(5) -> int
 * - f-customer-comment: pic x(25) -> String
 */
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

    /**
     * Formats the record to match COBOL display output format.
     * COBOL uses fixed-width fields, so we pad strings to match.
     */
    @Override
    public String toString() {
        return String.format("%05d%-50s%-50s%05d%-25s",
                customerId,
                padRight(lastName, 50),
                padRight(firstName, 50),
                contractId,
                padRight(comment, 25));
    }

    /**
     * Parses a COBOL-formatted line into a CustomerRecord.
     * Expected format: 5-digit ID, 50-char last name, 50-char first name, 5-digit contract ID, 25-char comment
     */
    public static CustomerRecord fromString(String line) {
        if (line == null || line.length() < 135) {
            throw new IllegalArgumentException("Invalid record format");
        }

        CustomerRecord record = new CustomerRecord();
        record.setCustomerId(Integer.parseInt(line.substring(0, 5).trim()));
        record.setLastName(line.substring(5, 55).trim());
        record.setFirstName(line.substring(55, 105).trim());
        record.setContractId(Integer.parseInt(line.substring(105, 110).trim()));
        record.setComment(line.substring(110, 135).trim());

        return record;
    }

    private static String padRight(String s, int n) {
        if (s == null) {
            s = "";
        }
        if (s.length() >= n) {
            return s.substring(0, n);
        }
        return String.format("%-" + n + "s", s);
    }
}
