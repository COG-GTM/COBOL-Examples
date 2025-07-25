package com.cognition.cobol.mergesort;

/**
 * Customer record class that matches the COBOL record structure:
 * f-customer-record with fields for ID, names, contract ID, and comment
 */
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
    
    /**
     * Formats the customer record to match COBOL display format
     * Customer ID (5 digits) + Last Name (50 chars) + First Name (50 chars) + Contract ID (5 digits) + Comment (25 chars)
     */
    @Override
    public String toString() {
        return String.format("%05d%-50s%-50s%05d%-25s", 
            customerId, 
            truncateOrPad(lastName, 50),
            truncateOrPad(firstName, 50),
            contractId,
            truncateOrPad(comment, 25));
    }
    
    /**
     * Helper method to truncate or pad strings to match COBOL field lengths
     */
    private String truncateOrPad(String str, int length) {
        if (str == null) {
            str = "";
        }
        if (str.length() > length) {
            return str.substring(0, length);
        } else {
            return String.format("%-" + length + "s", str);
        }
    }
    
    /**
     * Parse a customer record from a formatted string (for reading from files)
     */
    public static Customer fromString(String line) {
        if (line == null || line.length() < 135) {
            throw new IllegalArgumentException("Invalid customer record format");
        }
        
        Customer customer = new Customer();
        customer.setCustomerId(Integer.parseInt(line.substring(0, 5)));
        customer.setLastName(line.substring(5, 55).trim());
        customer.setFirstName(line.substring(55, 105).trim());
        customer.setContractId(Integer.parseInt(line.substring(105, 110)));
        customer.setComment(line.substring(110, 135).trim());
        
        return customer;
    }
}
