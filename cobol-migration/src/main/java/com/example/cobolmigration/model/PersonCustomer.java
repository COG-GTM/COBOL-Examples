package com.example.cobolmigration.model;

/**
 * Person customer — corresponds to the COBOL ws-customer-name layout
 * (redefines/redefines.cbl lines 23-25) when ws-customer-type is 1 (PERSON).
 * Fields: ws-customer-first-name pic x(10), ws-customer-last-name pic x(20).
 */
public class PersonCustomer extends Customer {

    private String firstName;
    private String lastName;

    public PersonCustomer() {
        setCustomerType(CustomerType.PERSON);
    }

    public PersonCustomer(String firstName, String lastName,
                          String streetAddress, String state, String zipCode) {
        super(CustomerType.PERSON, streetAddress, state, zipCode);
        this.firstName = firstName;
        this.lastName = lastName;
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
}
