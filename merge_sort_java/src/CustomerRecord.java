/**
 * Represents a customer record with fixed-width format support.
 * Mirrors the COBOL record structure:
 * - customerID: 5 digits (pic 9(5))
 * - lastName: 50 characters (pic x(50))
 * - firstName: 50 characters (pic x(50))
 * - contractID: 5 digits (pic 9(5))
 * - comment: 25 characters (pic x(25))
 * Total record length: 135 characters
 */
public class CustomerRecord implements Comparable<CustomerRecord> {
    
    public static final int CUSTOMER_ID_LENGTH = 5;
    public static final int LAST_NAME_LENGTH = 50;
    public static final int FIRST_NAME_LENGTH = 50;
    public static final int CONTRACT_ID_LENGTH = 5;
    public static final int COMMENT_LENGTH = 25;
    public static final int RECORD_LENGTH = CUSTOMER_ID_LENGTH + LAST_NAME_LENGTH + 
                                            FIRST_NAME_LENGTH + CONTRACT_ID_LENGTH + COMMENT_LENGTH;
    
    private int customerId;
    private String lastName;
    private String firstName;
    private int contractId;
    private String comment;
    
    public CustomerRecord() {
        this.customerId = 0;
        this.lastName = "";
        this.firstName = "";
        this.contractId = 0;
        this.comment = "";
    }
    
    public CustomerRecord(int customerId, String lastName, String firstName, 
                          int contractId, String comment) {
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
        StringBuilder sb = new StringBuilder();
        sb.append(padLeft(String.valueOf(customerId), CUSTOMER_ID_LENGTH, '0'));
        sb.append(padRight(lastName, LAST_NAME_LENGTH));
        sb.append(padRight(firstName, FIRST_NAME_LENGTH));
        sb.append(padLeft(String.valueOf(contractId), CONTRACT_ID_LENGTH, '0'));
        sb.append(padRight(comment, COMMENT_LENGTH));
        return sb.toString();
    }
    
    public static CustomerRecord fromFixedWidthString(String line) {
        if (line == null || line.length() < RECORD_LENGTH) {
            if (line != null) {
                line = padRight(line, RECORD_LENGTH);
            } else {
                return null;
            }
        }
        
        CustomerRecord record = new CustomerRecord();
        int pos = 0;
        
        String customerIdStr = line.substring(pos, pos + CUSTOMER_ID_LENGTH).trim();
        record.customerId = customerIdStr.isEmpty() ? 0 : Integer.parseInt(customerIdStr);
        pos += CUSTOMER_ID_LENGTH;
        
        record.lastName = line.substring(pos, pos + LAST_NAME_LENGTH).trim();
        pos += LAST_NAME_LENGTH;
        
        record.firstName = line.substring(pos, pos + FIRST_NAME_LENGTH).trim();
        pos += FIRST_NAME_LENGTH;
        
        String contractIdStr = line.substring(pos, pos + CONTRACT_ID_LENGTH).trim();
        record.contractId = contractIdStr.isEmpty() ? 0 : Integer.parseInt(contractIdStr);
        pos += CONTRACT_ID_LENGTH;
        
        record.comment = line.substring(pos, Math.min(pos + COMMENT_LENGTH, line.length())).trim();
        
        return record;
    }
    
    private static String padLeft(String str, int length, char padChar) {
        if (str.length() >= length) {
            return str.substring(0, length);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = str.length(); i < length; i++) {
            sb.append(padChar);
        }
        sb.append(str);
        return sb.toString();
    }
    
    private static String padRight(String str, int length) {
        if (str == null) {
            str = "";
        }
        if (str.length() >= length) {
            return str.substring(0, length);
        }
        StringBuilder sb = new StringBuilder(str);
        while (sb.length() < length) {
            sb.append(' ');
        }
        return sb.toString();
    }
    
    @Override
    public int compareTo(CustomerRecord other) {
        return Integer.compare(this.customerId, other.customerId);
    }
    
    @Override
    public String toString() {
        return toFixedWidthString();
    }
}
