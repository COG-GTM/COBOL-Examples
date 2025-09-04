/**
 * CustomerRecord represents a customer data structure that mirrors the COBOL
 * f-customer-record-sort structure from merge_sort_test.cbl.
 * 
 * This class implements Comparable to enable sorting by customer ID as the primary key,
 * with additional comparison logic for other fields as tie-breakers.
 */
public class CustomerRecord implements Comparable<CustomerRecord> {
    private int customerId;
    private String customerLastName;
    private String customerFirstName;
    private int customerContractId;
    private String customerComment;
    
    /**
     * Default constructor
     */
    public CustomerRecord() {
        this.customerId = 0;
        this.customerLastName = "";
        this.customerFirstName = "";
        this.customerContractId = 0;
        this.customerComment = "";
    }
    
    /**
     * Constructor with all fields
     * 
     * @param customerId 5-digit customer ID
     * @param customerLastName Customer's last name (max 50 chars)
     * @param customerFirstName Customer's first name (max 50 chars)
     * @param customerContractId 5-digit contract ID
     * @param customerComment Customer comment (max 25 chars)
     */
    public CustomerRecord(int customerId, String customerLastName, String customerFirstName, 
                         int customerContractId, String customerComment) {
        this.customerId = customerId;
        this.customerLastName = truncateString(customerLastName, 50);
        this.customerFirstName = truncateString(customerFirstName, 50);
        this.customerContractId = customerContractId;
        this.customerComment = truncateString(customerComment, 25);
    }
    
    /**
     * Helper method to truncate strings to specified length to match COBOL field sizes
     */
    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength) : str;
    }
    
    public int getCustomerId() { return customerId; }
    public String getCustomerLastName() { return customerLastName; }
    public String getCustomerFirstName() { return customerFirstName; }
    public int getCustomerContractId() { return customerContractId; }
    public String getCustomerComment() { return customerComment; }
    
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public void setCustomerLastName(String customerLastName) { 
        this.customerLastName = truncateString(customerLastName, 50); 
    }
    public void setCustomerFirstName(String customerFirstName) { 
        this.customerFirstName = truncateString(customerFirstName, 50); 
    }
    public void setCustomerContractId(int customerContractId) { 
        this.customerContractId = customerContractId; 
    }
    public void setCustomerComment(String customerComment) { 
        this.customerComment = truncateString(customerComment, 25); 
    }
    
    /**
     * Compares CustomerRecord objects primarily by customer ID.
     * If customer IDs are equal, compares by last name, then first name, then contract ID.
     * 
     * @param other The CustomerRecord to compare to
     * @return negative if this < other, 0 if equal, positive if this > other
     */
    @Override
    public int compareTo(CustomerRecord other) {
        if (other == null) return 1;
        
        int result = Integer.compare(this.customerId, other.customerId);
        if (result != 0) return result;
        
        result = this.customerLastName.compareTo(other.customerLastName);
        if (result != 0) return result;
        
        result = this.customerFirstName.compareTo(other.customerFirstName);
        if (result != 0) return result;
        
        return Integer.compare(this.customerContractId, other.customerContractId);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        CustomerRecord that = (CustomerRecord) obj;
        return customerId == that.customerId &&
               customerContractId == that.customerContractId &&
               customerLastName.equals(that.customerLastName) &&
               customerFirstName.equals(that.customerFirstName) &&
               customerComment.equals(that.customerComment);
    }
    
    @Override
    public int hashCode() {
        int result = customerId;
        result = 31 * result + customerLastName.hashCode();
        result = 31 * result + customerFirstName.hashCode();
        result = 31 * result + customerContractId;
        result = 31 * result + customerComment.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return String.format("CustomerRecord{id=%d, lastName='%s', firstName='%s', contractId=%d, comment='%s'}",
                           customerId, customerLastName, customerFirstName, customerContractId, customerComment);
    }
}
