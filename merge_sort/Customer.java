public class Customer implements Comparable<Customer> {
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
    
    @Override
    public int compareTo(Customer other) {
        return Integer.compare(this.customerId, other.customerId);
    }
    
    @Override
    public String toString() {
        return String.format("%05d%-50s%-50s%05d%-25s", 
            customerId, lastName, firstName, contractId, comment);
    }
    
    public static Customer fromString(String line) {
        if (line.length() < 135) {
            throw new IllegalArgumentException("Invalid customer record format");
        }
        
        int customerId = Integer.parseInt(line.substring(0, 5).trim());
        String lastName = line.substring(5, 55).trim();
        String firstName = line.substring(55, 105).trim();
        int contractId = Integer.parseInt(line.substring(105, 110).trim());
        String comment = line.substring(110, 135).trim();
        
        return new Customer(customerId, lastName, firstName, contractId, comment);
    }
}
