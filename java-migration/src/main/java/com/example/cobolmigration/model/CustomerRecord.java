package com.example.cobolmigration.model;

import java.util.Comparator;

/**
 * Plain POJO for file-based customer records from merge_sort/merge_sort_test.cbl.
 * Maps the fixed-width record structure:
 *   f-customer-id           PIC 9(5)
 *   f-customer-last-name    PIC X(50)
 *   f-customer-first-name   PIC X(50)
 *   f-customer-contract-id  PIC 9(5)
 *   f-customer-comment      PIC X(25)
 */
public class CustomerRecord implements Comparable<CustomerRecord> {

    /** Record width for each field in the fixed-width file format. */
    public static final int ID_WIDTH = 5;
    public static final int LAST_NAME_WIDTH = 50;
    public static final int FIRST_NAME_WIDTH = 50;
    public static final int CONTRACT_ID_WIDTH = 5;
    public static final int COMMENT_WIDTH = 25;
    public static final int RECORD_WIDTH = ID_WIDTH + LAST_NAME_WIDTH + FIRST_NAME_WIDTH
            + CONTRACT_ID_WIDTH + COMMENT_WIDTH;

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

    /** Default comparison: ascending by customerId (maps MERGE ON ASCENDING KEY). */
    @Override
    public int compareTo(CustomerRecord other) {
        return Integer.compare(this.customerId, other.customerId);
    }

    /** Comparator for ascending sort by customerId. */
    public static Comparator<CustomerRecord> byCustomerIdAsc() {
        return Comparator.comparingInt(CustomerRecord::getCustomerId);
    }

    /** Comparator for descending sort by contractId (maps SORT ON DESCENDING KEY). */
    public static Comparator<CustomerRecord> byContractIdDesc() {
        return Comparator.comparingInt(CustomerRecord::getContractId).reversed();
    }

    /**
     * Formats this record as a fixed-width string matching the COBOL file layout.
     */
    public String toFixedWidth() {
        return String.format("%-" + ID_WIDTH + "s", padRight(String.valueOf(customerId), ID_WIDTH))
                .substring(0, ID_WIDTH)
                + padRight(lastName, LAST_NAME_WIDTH)
                + padRight(firstName, FIRST_NAME_WIDTH)
                + String.format("%-" + CONTRACT_ID_WIDTH + "s",
                        padRight(String.valueOf(contractId), CONTRACT_ID_WIDTH))
                        .substring(0, CONTRACT_ID_WIDTH)
                + padRight(comment, COMMENT_WIDTH);
    }

    /**
     * Parses a fixed-width line into a CustomerRecord.
     */
    public static CustomerRecord fromFixedWidth(String line) {
        if (line.length() < RECORD_WIDTH) {
            line = padRight(line, RECORD_WIDTH);
        }
        int offset = 0;
        int custId = parseIntSafe(line.substring(offset, offset + ID_WIDTH).trim());
        offset += ID_WIDTH;
        String last = line.substring(offset, offset + LAST_NAME_WIDTH).trim();
        offset += LAST_NAME_WIDTH;
        String first = line.substring(offset, offset + FIRST_NAME_WIDTH).trim();
        offset += FIRST_NAME_WIDTH;
        int contId = parseIntSafe(line.substring(offset, offset + CONTRACT_ID_WIDTH).trim());
        offset += CONTRACT_ID_WIDTH;
        String cmt = line.substring(offset, Math.min(offset + COMMENT_WIDTH, line.length())).trim();
        return new CustomerRecord(custId, last, first, contId, cmt);
    }

    private static String padRight(String s, int width) {
        if (s == null) {
            s = "";
        }
        if (s.length() >= width) {
            return s.substring(0, width);
        }
        return String.format("%-" + width + "s", s);
    }

    private static int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
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
        return "CustomerRecord{customerId=" + customerId + ", lastName='" + lastName
                + "', firstName='" + firstName + "', contractId=" + contractId
                + ", comment='" + comment + "'}";
    }
}
