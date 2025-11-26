/**
 * CustomerRecord class representing a customer record.
 * Equivalent to the COBOL record structure:
 * - customer-id: 5 digits
 * - customer-last-name: 50 chars
 * - customer-first-name: 50 chars
 * - customer-contract-id: 5 digits
 * - customer-comment: 25 chars
 *
 * @author Erik Eriksen (original COBOL)
 * @author Devin AI (Java port)
 */
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
        return String.format("%05d%-50s%-50s%05d%-25s",
                customerId, customerLastName, customerFirstName,
                customerContractId, customerComment);
    }

    public static CustomerRecord fromString(String line) {
        if (line.length() < 135) {
            line = String.format("%-135s", line);
        }
        int customerId = Integer.parseInt(line.substring(0, 5).trim());
        String lastName = line.substring(5, 55).trim();
        String firstName = line.substring(55, 105).trim();
        int contractId = Integer.parseInt(line.substring(105, 110).trim());
        String comment = line.substring(110, 135).trim();
        return new CustomerRecord(customerId, lastName, firstName, contractId, comment);
    }
}
