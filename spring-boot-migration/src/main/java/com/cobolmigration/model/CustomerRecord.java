package com.cobolmigration.model;

/**
 * Model representing a customer record for file processing.
 * Replaces the COBOL f-customer-record-sort group item:
 *   05 f-customer-id           pic 9(5)
 *   05 f-customer-last-name    pic x(50)
 *   05 f-customer-first-name   pic x(50)
 *   05 f-customer-contract-id  pic 9(5)
 *   05 f-customer-comment      pic x(25)
 *
 * Total fixed-width record length: 135 characters
 */
public class CustomerRecord {

    public static final int RECORD_LENGTH = 135;
    public static final int ID_LENGTH = 5;
    public static final int NAME_LENGTH = 50;
    public static final int CONTRACT_ID_LENGTH = 5;
    public static final int COMMENT_LENGTH = 25;

    private int customerId;
    private String lastName;
    private String firstName;
    private int contractId;
    private String comment;

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
     * Parse a fixed-width record line into a CustomerRecord.
     * Mirrors the COBOL fixed-width file record layout.
     */
    public static CustomerRecord fromFixedWidth(String line) {
        if (line.length() < RECORD_LENGTH) {
            line = String.format("%-" + RECORD_LENGTH + "s", line);
        }
        int pos = 0;
        int id = Integer.parseInt(line.substring(pos, pos + ID_LENGTH).trim());
        pos += ID_LENGTH;
        String last = line.substring(pos, pos + NAME_LENGTH).trim();
        pos += NAME_LENGTH;
        String first = line.substring(pos, pos + NAME_LENGTH).trim();
        pos += NAME_LENGTH;
        int contractId = Integer.parseInt(line.substring(pos, pos + CONTRACT_ID_LENGTH).trim());
        pos += CONTRACT_ID_LENGTH;
        String comment = line.substring(pos, Math.min(pos + COMMENT_LENGTH, line.length())).trim();

        return new CustomerRecord(id, last, first, contractId, comment);
    }

    /**
     * Convert to fixed-width string matching the COBOL record layout.
     */
    public String toFixedWidth() {
        return String.format("%0" + ID_LENGTH + "d", customerId)
                + String.format("%-" + NAME_LENGTH + "s", lastName).substring(0, NAME_LENGTH)
                + String.format("%-" + NAME_LENGTH + "s", firstName).substring(0, NAME_LENGTH)
                + String.format("%0" + CONTRACT_ID_LENGTH + "d", contractId)
                + String.format("%-" + COMMENT_LENGTH + "s", comment).substring(0, COMMENT_LENGTH);
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
