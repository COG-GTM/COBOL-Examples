public class CustomerRecord {
    private int customerId;
    private String customerLastName;
    private String customerFirstName;
    private int customerContractId;
    private String customerComment;
    
    public CustomerRecord() {}
    
    public CustomerRecord(int customerId, String customerLastName, String customerFirstName, 
                         int customerContractId, String customerComment) {
        this.customerId = customerId;
        this.customerLastName = customerLastName;
        this.customerFirstName = customerFirstName;
        this.customerContractId = customerContractId;
        this.customerComment = customerComment;
    }
    
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    
    public String getCustomerLastName() { return customerLastName; }
    public void setCustomerLastName(String customerLastName) { this.customerLastName = customerLastName; }
    
    public String getCustomerFirstName() { return customerFirstName; }
    public void setCustomerFirstName(String customerFirstName) { this.customerFirstName = customerFirstName; }
    
    public int getCustomerContractId() { return customerContractId; }
    public void setCustomerContractId(int customerContractId) { this.customerContractId = customerContractId; }
    
    public String getCustomerComment() { return customerComment; }
    public void setCustomerComment(String customerComment) { this.customerComment = customerComment; }
    
    public String toFixedWidthString() {
        return String.format("%05d%-50s%-50s%05d%-25s",
            customerId,
            customerLastName != null ? customerLastName : "",
            customerFirstName != null ? customerFirstName : "",
            customerContractId,
            customerComment != null ? customerComment : "");
    }
    
    public static CustomerRecord fromFixedWidthString(String line) {
        if (line == null || line.length() < 135) {
            return null;
        }
        
        CustomerRecord record = new CustomerRecord();
        record.customerId = Integer.parseInt(line.substring(0, 5));
        record.customerLastName = line.substring(5, 55).trim();
        record.customerFirstName = line.substring(55, 105).trim();
        record.customerContractId = Integer.parseInt(line.substring(105, 110));
        record.customerComment = line.substring(110, 135).trim();
        
        return record;
    }
    
    @Override
    public String toString() {
        return toFixedWidthString();
    }
}
