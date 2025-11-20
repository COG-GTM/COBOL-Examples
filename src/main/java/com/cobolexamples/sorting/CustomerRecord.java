package com.cobolexamples.sorting;

public class CustomerRecord {
    private int customerId;
    private String customerLastName;
    private String customerFirstName;
    private int customerContractId;
    private String customerComment;
    
    public CustomerRecord(int customerId, String customerLastName, String customerFirstName, 
                         int customerContractId, String customerComment) {
        this.customerId = customerId;
        this.customerLastName = customerLastName;
        this.customerFirstName = customerFirstName;
        this.customerContractId = customerContractId;
        this.customerComment = customerComment;
    }
    
    public int getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    public String getCustomerLastName() {
        return customerLastName;
    }
    
    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }
    
    public String getCustomerFirstName() {
        return customerFirstName;
    }
    
    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }
    
    public int getCustomerContractId() {
        return customerContractId;
    }
    
    public void setCustomerContractId(int customerContractId) {
        this.customerContractId = customerContractId;
    }
    
    public String getCustomerComment() {
        return customerComment;
    }
    
    public void setCustomerComment(String customerComment) {
        this.customerComment = customerComment;
    }
    
    @Override
    public String toString() {
        return String.format("%-5d%-50s%-50s%-5d%-25s", 
            customerId, 
            padRight(customerLastName, 50),
            padRight(customerFirstName, 50),
            customerContractId,
            padRight(customerComment, 25));
    }
    
    public String toFileFormat() {
        return String.format("%05d%-50s%-50s%05d%-25s", 
            customerId, 
            padRight(customerLastName, 50),
            padRight(customerFirstName, 50),
            customerContractId,
            padRight(customerComment, 25));
    }
    
    public static CustomerRecord fromFileFormat(String line) {
        if (line.length() < 135) {
            line = padRight(line, 135);
        }
        
        int customerId = Integer.parseInt(line.substring(0, 5).trim());
        String lastName = line.substring(5, 55).trim();
        String firstName = line.substring(55, 105).trim();
        int contractId = Integer.parseInt(line.substring(105, 110).trim());
        String comment = line.substring(110, 135).trim();
        
        return new CustomerRecord(customerId, lastName, firstName, contractId, comment);
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
