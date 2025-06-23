public class Customer {
    private int customerId;
    private String lastName;
    private String firstName;
    private int contractId;
    private String comment;
    
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
    
    public String toFixedWidthString() {
        return String.format("%05d%-50s%-50s%05d%-25s", 
            customerId, 
            padOrTruncate(lastName, 50),
            padOrTruncate(firstName, 50),
            contractId,
            padOrTruncate(comment, 25));
    }
    
    private String padOrTruncate(String str, int length) {
        if (str.length() >= length) {
            return str.substring(0, length);
        }
        return String.format("%-" + length + "s", str);
    }
    
    @Override
    public String toString() {
        return toFixedWidthString();
    }
}
