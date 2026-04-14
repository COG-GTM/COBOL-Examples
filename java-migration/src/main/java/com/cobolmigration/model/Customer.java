package com.cobolmigration.model;

/**
 * Maps the COBOL WORKING-STORAGE record from redefines/redefines.cbl lines 17-31.
 * Models the REDEFINES pattern: when customerType is PERSON, firstName and lastName
 * are used; when customerType is CORP, corpName is used (which occupies the same
 * storage as firstName + lastName in COBOL).
 */
public class Customer {

    private CustomerType customerType;
    private String firstName;   // max 10 chars
    private String lastName;    // max 20 chars
    private String corpName;    // max 30 chars (REDEFINES ws-customer-name)
    private String streetAddress; // max 20 chars
    private String state;       // max 2 chars
    private String zipCode;     // max 5 chars

    public Customer() {
    }

    public Customer(CustomerType customerType, String streetAddress, String state, String zipCode) {
        this.customerType = customerType;
        this.streetAddress = streetAddress;
        this.state = state;
        this.zipCode = zipCode;
    }

    /**
     * Factory method for a PERSON customer.
     */
    public static Customer createPerson(String firstName, String lastName,
                                        String streetAddress, String state, String zipCode) {
        Customer customer = new Customer(CustomerType.PERSON, streetAddress, state, zipCode);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        return customer;
    }

    /**
     * Factory method for a CORP customer.
     */
    public static Customer createCorp(String corpName,
                                      String streetAddress, String state, String zipCode) {
        Customer customer = new Customer(CustomerType.CORP, streetAddress, state, zipCode);
        customer.setCorpName(corpName);
        return customer;
    }

    /**
     * Returns the display name based on customer type.
     * For PERSON: "firstName lastName"
     * For CORP: corpName
     */
    public String getDisplayName() {
        if (customerType == CustomerType.PERSON) {
            return (firstName != null ? firstName.trim() : "") + " " +
                   (lastName != null ? lastName.trim() : "");
        } else {
            return corpName != null ? corpName.trim() : "";
        }
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Customer{type=").append(customerType);
        if (customerType == CustomerType.PERSON) {
            sb.append(", firstName='").append(firstName).append('\'');
            sb.append(", lastName='").append(lastName).append('\'');
        } else {
            sb.append(", corpName='").append(corpName).append('\'');
        }
        sb.append(", streetAddress='").append(streetAddress).append('\'');
        sb.append(", state='").append(state).append('\'');
        sb.append(", zipCode='").append(zipCode).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
