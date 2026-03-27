package com.example.cobolmigration.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Abstract base class modeling the COBOL REDEFINES pattern from redefines/redefines.cbl (lines 17-31).
 *
 * In COBOL, a single memory layout (ws-customer) can be interpreted as either a person
 * (first_name + last_name) or a corporation (corp_name) via the REDEFINES keyword.
 * In Java, this is modeled with inheritance: PersonCustomer and CorpCustomer extend this base.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "customerType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = PersonCustomer.class, name = "PERSON"),
    @JsonSubTypes.Type(value = CorpCustomer.class, name = "CORP")
})
public abstract class Customer {

    public enum CustomerType {
        PERSON,
        CORP
    }

    private CustomerType customerType;
    private String streetAddress;
    private String state;
    private String zipCode;

    protected Customer() {
    }

    protected Customer(CustomerType customerType, String streetAddress, String state, String zipCode) {
        this.customerType = customerType;
        this.streetAddress = streetAddress;
        this.state = state;
        this.zipCode = zipCode;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
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
}
