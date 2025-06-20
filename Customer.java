public class Customer {
    private int customerId;           // pic 9(5) - 5-digit numeric field
    private String customerLastName;  // pic x(50) - 50-character field
    private String customerFirstName; // pic x(50) - 50-character field
    private int customerContractId;   // pic 9(5) - 5-digit numeric field
    private String customerComment;   // pic x(25) - 25-character field
    
    public Customer() {
    }
    
    public Customer(int customerId, String customerLastName, String customerFirstName, 
                   int customerContractId, String customerComment) {
        this.customerId = customerId;
        this.customerLastName = customerLastName;
        this.customerFirstName = customerFirstName;
        this.customerContractId = customerContractId;
        this.customerComment = customerComment;
    }
    
    public static Customer parseFromFixedWidth(String line) {
        if (line == null || line.length() < 135) {
            throw new IllegalArgumentException("Invalid line format: expected at least 135 characters");
        }
        
        int customerId = Integer.parseInt(line.substring(0, 5));
        String customerLastName = line.substring(5, 55).trim();
        String customerFirstName = line.substring(55, 105).trim();
        int customerContractId = Integer.parseInt(line.substring(105, 110));
        String customerComment = line.substring(110, 135).trim();
        
        return new Customer(customerId, customerLastName, customerFirstName, 
                          customerContractId, customerComment);
    }
    
    public String toFixedWidthString() {
        return String.format("%05d%-50s%-50s%05d%-25s",
                           customerId,
                           padRight(customerLastName, 50),
                           padRight(customerFirstName, 50),
                           customerContractId,
                           padRight(customerComment, 25));
    }
    
    private String padRight(String str, int length) {
        if (str == null) str = "";
        if (str.length() >= length) {
            return str.substring(0, length);
        }
        return str + " ".repeat(length - str.length());
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
        return toFixedWidthString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Customer customer = (Customer) obj;
        return customerId == customer.customerId &&
               customerContractId == customer.customerContractId &&
               java.util.Objects.equals(customerLastName, customer.customerLastName) &&
               java.util.Objects.equals(customerFirstName, customer.customerFirstName) &&
               java.util.Objects.equals(customerComment, customer.customerComment);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(customerId, customerLastName, customerFirstName, 
                                    customerContractId, customerComment);
    }
}
