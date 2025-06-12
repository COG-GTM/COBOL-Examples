public class CustomerRecord {
    private String customerID; // 5 digits
    private String lastName;   // 50 chars
    private String firstName;  // 50 chars
    private String contractID; // 5 digits
    private String comment;    // 25 chars

    public CustomerRecord(String customerID, String lastName, String firstName, String contractID, String comment) {
        this.customerID = customerID;
        this.lastName = lastName;
        this.firstName = firstName;
        this.contractID = contractID;
        this.comment = comment;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
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

    public String getContractID() {
        return contractID;
    }

    public void setContractID(String contractID) {
        this.contractID = contractID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
