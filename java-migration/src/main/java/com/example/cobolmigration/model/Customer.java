package com.example.cobolmigration.model;

/**
 * Abstract base class mapping the COBOL customer record from redefines/redefines.cbl (lines 17-31).
 * The COBOL REDEFINES keyword is replaced by Java inheritance: PersonCustomer and CorpCustomer
 * extend this class and provide their own name handling.
 */
public abstract class Customer {

    private int customerType;
    private String streetAddress; // PIC X(20)
    private String state;        // PIC XX
    private String zipCode;      // PIC 9(5)

    protected Customer() {
    }

    protected Customer(int customerType, String streetAddress, String state, String zipCode) {
        this.customerType = customerType;
        this.streetAddress = streetAddress;
        this.state = state;
        this.zipCode = zipCode;
    }

    /**
     * Returns the display name for this customer.
     * PersonCustomer returns firstName + " " + lastName;
     * CorpCustomer returns corpName.
     */
    public abstract String getDisplayName();

    public int getCustomerType() {
        return customerType;
    }

    public void setCustomerType(int customerType) {
        this.customerType = customerType;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = (streetAddress != null && streetAddress.length() > 20)
                ? streetAddress.substring(0, 20) : streetAddress;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = (state != null && state.length() > 2)
                ? state.substring(0, 2) : state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = (zipCode != null && zipCode.length() > 5)
                ? zipCode.substring(0, 5) : zipCode;
    }
}
