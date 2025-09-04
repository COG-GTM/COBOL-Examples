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
    
    public String getLastName() {
        return lastName;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public int getContractId() {
        return contractId;
    }
    
    public String getComment() {
        return comment;
    }
    
    @Override
    public String toString() {
        return String.format("%05d%-50s%-50s%05d%-25s", 
            customerId, lastName, firstName, contractId, comment);
    }
    
    public static CustomerRecord fromString(String line) {
        if (line.length() < 135) {
            throw new IllegalArgumentException("Invalid record format");
        }
        
        int customerId = Integer.parseInt(line.substring(0, 5));
        String lastName = line.substring(5, 55).trim();
        String firstName = line.substring(55, 105).trim();
        int contractId = Integer.parseInt(line.substring(105, 110));
        String comment = line.substring(110, 135).trim();
        
        return new CustomerRecord(customerId, lastName, firstName, contractId, comment);
    }
}
