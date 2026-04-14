package com.cobolmigration.model;

import java.util.Objects;

/**
 * Customer model migrated from redifines/redefines.cbl (lines 17-31).
 *
 * The COBOL program uses REDEFINES to allow ws-customer-name (first+last)
 * to be reinterpreted as ws-corp-name. In Java, we model this with:
 * - An enum CustomerType for the 88-level conditions
 * - Separate fields for person name (first+last) and corp name
 * - A getDisplayName() method that returns the appropriate name based on type
 *
 * COBOL field mappings:
 *   ws-customer-type          (pic 9)      -> CustomerType customerType
 *     88 ws-customer-type-person value 1    -> CustomerType.PERSON
 *     88 ws-customer-type-corp   value 2    -> CustomerType.CORPORATION
 *   ws-customer-first-name    (pic x(10))  -> String firstName
 *   ws-customer-last-name     (pic x(20))  -> String lastName
 *   ws-corp-name REDEFINES ws-customer-name (pic x(30)) -> String corpName
 *   ws-street-address         (pic x(20))  -> String streetAddress
 *   ws-state                  (pic xx)     -> String state
 *   ws-zip-code               (pic 9(5))   -> int zipCode
 */
public class Customer {

    /**
     * Enum representing the 88-level conditions from the COBOL source:
     *   88 ws-customer-type-person value 1
     *   88 ws-customer-type-corp   value 2
     */
    public enum CustomerType {
        PERSON(1),
        CORPORATION(2);

        private final int code;

        CustomerType(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static CustomerType fromCode(int code) {
            for (CustomerType type : values()) {
                if (type.code == code) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown customer type code: " + code);
        }
    }

    private CustomerType customerType;
    private String firstName;
    private String lastName;
    private String corpName;
    private String streetAddress;
    private String state;
    private int zipCode;

    public Customer() {
    }

    /**
     * Constructor for a PERSON customer.
     */
    public Customer(String firstName, String lastName, String streetAddress,
                    String state, int zipCode) {
        this.customerType = CustomerType.PERSON;
        this.firstName = firstName;
        this.lastName = lastName;
        this.streetAddress = streetAddress;
        this.state = state;
        this.zipCode = zipCode;
    }

    /**
     * Constructor for a CORPORATION customer.
     */
    public Customer(String corpName, String streetAddress, String state, int zipCode) {
        this.customerType = CustomerType.CORPORATION;
        this.corpName = corpName;
        this.streetAddress = streetAddress;
        this.state = state;
        this.zipCode = zipCode;
    }

    /**
     * Returns the display name based on customer type.
     * For PERSON: returns "firstName lastName" (trimmed).
     * For CORPORATION: returns corpName (trimmed).
     *
     * This models the COBOL REDEFINES behavior where ws-customer-name
     * (first+last) and ws-corp-name share the same storage.
     */
    public String getDisplayName() {
        if (customerType == CustomerType.CORPORATION && corpName != null) {
            return corpName.trim();
        }
        String first = firstName != null ? firstName.trim() : "";
        String last = lastName != null ? lastName.trim() : "";
        if (first.isEmpty() && last.isEmpty()) {
            return "";
        }
        if (first.isEmpty()) {
            return last;
        }
        if (last.isEmpty()) {
            return first;
        }
        return first + " " + last;
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

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * Returns true if this is a person customer (COBOL: ws-customer-type-person).
     */
    public boolean isPerson() {
        return customerType == CustomerType.PERSON;
    }

    /**
     * Returns true if this is a corporation customer (COBOL: ws-customer-type-corp).
     */
    public boolean isCorporation() {
        return customerType == CustomerType.CORPORATION;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return zipCode == customer.zipCode &&
                customerType == customer.customerType &&
                Objects.equals(getDisplayName(), customer.getDisplayName()) &&
                Objects.equals(streetAddress, customer.streetAddress) &&
                Objects.equals(state, customer.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerType, getDisplayName(), streetAddress, state, zipCode);
    }

    @Override
    public String toString() {
        return String.format("Customer{type=%s, name='%s', address='%s', state='%s', zip=%05d}",
                customerType, getDisplayName(), streetAddress, state, zipCode);
    }
}
