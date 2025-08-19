public class CustomerRecord {
    private String customerID;
    private String lastName;
    private String firstName;
    private String contractID;
    private String comment;
    
    public CustomerRecord() {
    }
    
    public CustomerRecord(String customerID, String lastName, String firstName, String contractID, String comment) {
        this.customerID = padOrTruncate(customerID, 5, true);
        this.lastName = padOrTruncate(lastName, 50, false);
        this.firstName = padOrTruncate(firstName, 50, false);
        this.contractID = padOrTruncate(contractID, 5, true);
        this.comment = padOrTruncate(comment, 25, false);
    }
    
    private String padOrTruncate(String value, int length, boolean isNumeric) {
        if (value == null) {
            value = "";
        }
        
        if (isNumeric) {
            while (value.length() < length) {
                value = "0" + value;
            }
        } else {
            while (value.length() < length) {
                value = value + " ";
            }
        }
        
        if (value.length() > length) {
            value = value.substring(0, length);
        }
        
        return value;
    }
    
    public String getCustomerID() {
        return customerID;
    }
    
    public void setCustomerID(String customerID) {
        this.customerID = padOrTruncate(customerID, 5, true);
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = padOrTruncate(lastName, 50, false);
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = padOrTruncate(firstName, 50, false);
    }
    
    public String getContractID() {
        return contractID;
    }
    
    public void setContractID(String contractID) {
        this.contractID = padOrTruncate(contractID, 5, true);
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = padOrTruncate(comment, 25, false);
    }
    
    public String toFixedWidthString() {
        return customerID + lastName + firstName + contractID + comment;
    }
    
    public static CustomerRecord fromFixedWidthString(String line) {
        if (line == null || line.length() < 135) {
            return null;
        }
        
        CustomerRecord record = new CustomerRecord();
        record.customerID = line.substring(0, 5);
        record.lastName = line.substring(5, 55);
        record.firstName = line.substring(55, 105);
        record.contractID = line.substring(105, 110);
        record.comment = line.substring(110, 135);
        
        return record;
    }
    
    @Override
    public String toString() {
        return toFixedWidthString();
    }
    
    public int getCustomerIDAsInt() {
        return Integer.parseInt(customerID.trim());
    }
    
    public int getContractIDAsInt() {
        return Integer.parseInt(contractID.trim());
    }
}
