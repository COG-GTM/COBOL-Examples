package com.cobol.examples;

public class CustomerRecord {
    private int customerId;
    private String lastName;
    private String firstName;
    private int contractId;
    private String comment;
    
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
    
    public static CustomerRecord parseFromFixedWidth(String line) {
        if (line.length() < 135) {
            line = String.format("%-135s", line);
        }
        
        if (line.length() < 135) {
            throw new IllegalArgumentException("Line too short: expected 135 chars, got " + line.length());
        }
        
        int customerId = Integer.parseInt(line.substring(0, 5).trim());
        String lastName = line.substring(5, 55);
        String firstName = line.substring(55, 105);
        int contractId = Integer.parseInt(line.substring(105, 110).trim());
        String comment = line.substring(110, 135);
        
        return new CustomerRecord(customerId, lastName, firstName, contractId, comment);
    }
    
    public String toFixedWidth() {
        return String.format("%05d%-50s%-50s%05d%-25s",
            customerId,
            lastName.trim(),
            firstName.trim(),
            contractId,
            comment.trim()
        );
    }
    
    @Override
    public String toString() {
        return toFixedWidth();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CustomerRecord that = (CustomerRecord) obj;
        return customerId == that.customerId &&
               contractId == that.contractId &&
               lastName.equals(that.lastName) &&
               firstName.equals(that.firstName) &&
               comment.equals(that.comment);
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
