package com.example.cobolmigration.model;

/**
 * Person variant of the Customer record (customerType = 1).
 * Maps the person branch of the COBOL REDEFINES in redefines/redefines.cbl.
 * Uses separate firstName (PIC X(10)) and lastName (PIC X(20)) fields.
 */
public class PersonCustomer extends Customer {

    private String firstName;  // PIC X(10)
    private String lastName;   // PIC X(20)

    public PersonCustomer() {
        setCustomerType(1);
    }

    public PersonCustomer(String firstName, String lastName,
                          String streetAddress, String state, String zipCode) {
        super(1, streetAddress, state, zipCode);
        setFirstName(firstName);
        setLastName(lastName);
    }

    @Override
    public String getDisplayName() {
        return firstName + " " + lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = (firstName != null && firstName.length() > 10)
                ? firstName.substring(0, 10) : firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = (lastName != null && lastName.length() > 20)
                ? lastName.substring(0, 20) : lastName;
    }
}
