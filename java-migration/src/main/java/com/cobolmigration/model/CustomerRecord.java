package com.cobolmigration.model;

/**
 * Model mapping from the COBOL FD customer record layout in merge_sort/merge_sort_test.cbl.
 *
 * <p>COBOL field mapping:
 * <ul>
 *   <li>f-customer-id (PIC 9(5)) &rarr; customerId (int)</li>
 *   <li>f-customer-last-name (PIC X(50)) &rarr; lastName (String)</li>
 *   <li>f-customer-first-name (PIC X(50)) &rarr; firstName (String)</li>
 *   <li>f-customer-contract-id (PIC 9(5)) &rarr; contractId (int)</li>
 *   <li>f-customer-comment (PIC X(25)) &rarr; comment (String)</li>
 * </ul>
 *
 * @see merge_sort/merge_sort_test.cbl lines 40-46
 */
public class CustomerRecord implements Comparable<CustomerRecord> {

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
    public int compareTo(CustomerRecord other) {
        return Integer.compare(this.customerId, other.customerId);
    }

    @Override
    public String toString() {
        return String.format("%-5d%-50s%-50s%-5d%-25s",
                customerId,
                lastName != null ? lastName : "",
                firstName != null ? firstName : "",
                contractId,
                comment != null ? comment : "");
    }

    /**
     * Parses a fixed-width record line matching the COBOL FD layout.
     * Total width: 5 + 50 + 50 + 5 + 25 = 135 characters.
     *
     * @param line the fixed-width line to parse
     * @return a CustomerRecord parsed from the line
     */
    public static CustomerRecord fromFixedWidth(String line) {
        if (line == null || line.length() < 110) {
            throw new IllegalArgumentException("Line too short for customer record: " + line);
        }
        String padded = String.format("%-135s", line);
        CustomerRecord record = new CustomerRecord();
        record.setCustomerId(Integer.parseInt(padded.substring(0, 5).trim()));
        record.setLastName(padded.substring(5, 55).trim());
        record.setFirstName(padded.substring(55, 105).trim());
        record.setContractId(Integer.parseInt(padded.substring(105, 110).trim()));
        record.setComment(padded.substring(110, 135).trim());
        return record;
    }
}
