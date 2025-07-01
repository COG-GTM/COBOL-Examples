public class CustomerRecord {
    private int customerId;
    private String lastName;
    private String firstName;
    private int contractId;
    private String comment;
    
    public CustomerRecord() {
    }
    
    public CustomerRecord(int customerId, String lastName, String firstName, int contractId, String comment) {
        this.customerId = customerId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.contractId = contractId;
        this.comment = comment;
    }
    
    public static CustomerRecord fromFileLine(String line) {
        if (line == null || line.length() < 135) {
            throw new IllegalArgumentException("Invalid record line length");
        }
        
        CustomerRecord record = new CustomerRecord();
        record.customerId = Integer.parseInt(line.substring(0, 5));
        record.lastName = line.substring(5, 55).trim();
        record.firstName = line.substring(55, 105).trim();
        record.contractId = Integer.parseInt(line.substring(105, 110));
        record.comment = line.substring(110, 135).trim();
        
        return record;
    }
    
    public String toFileLine() {
        return String.format("%05d%-50s%-50s%05d%-25s",
            customerId,
            lastName.length() > 50 ? lastName.substring(0, 50) : lastName,
            firstName.length() > 50 ? firstName.substring(0, 50) : firstName,
            contractId,
            comment.length() > 25 ? comment.substring(0, 25) : comment);
    }
    
    public String toDisplayLine() {
        return toFileLine();
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
        return toDisplayLine();
    }
}
