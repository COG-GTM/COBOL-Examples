package com.cobolmigration.model;

/**
 * Maps the customer record structure from merge_sort/merge_sort_test.cbl lines 40-46.
 * Used for file-based merge and sort operations.
 */
public class CustomerRecord {

    private int customerId;         // pic 9(5)
    private String lastName;        // pic x(50)
    private String firstName;       // pic x(50)
    private int contractId;         // pic 9(5)
    private String comment;         // pic x(25)

    public CustomerRecord() {
    }

    public CustomerRecord(int customerId, String lastName, String firstName,
                          int contractId, String comment) {
        this.customerId = customerId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.contractId = contractId;
        this.comment = comment;
    }

    /**
     * Parses a fixed-width record line (135 chars total):
     * customerId(5) + lastName(50) + firstName(50) + contractId(5) + comment(25)
     */
    public static CustomerRecord fromFixedWidth(String line) {
        if (line == null || line.length() < 135) {
            String padded = line != null ? String.format("%-135s", line) : String.format("%-135s", "");
            line = padded;
        }
        int customerId = Integer.parseInt(line.substring(0, 5).trim());
        String lastName = line.substring(5, 55);
        String firstName = line.substring(55, 105);
        int contractId = Integer.parseInt(line.substring(105, 110).trim());
        String comment = line.substring(110, 135);
        return new CustomerRecord(customerId, lastName, firstName, contractId, comment);
    }

    /**
     * Formats this record as a fixed-width string (135 chars total).
     */
    public String toFixedWidth() {
        return String.format("%05d%-50s%-50s%05d%-25s",
                customerId,
                lastName != null ? lastName : "",
                firstName != null ? firstName : "",
                contractId,
                comment != null ? comment : "");
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
        return toFixedWidth();
    }
}
