package com.migration.datastructures;

/**
 * Record implementation for person-type customers.
 *
 * COBOL equivalent: ws-customer with ws-customer-type-person (value 1)
 * Uses first name + last name fields from ws-customer-name group.
 */
public record PersonCustomer(
        String firstName,
        String lastName,
        Address address
) implements Customer {

    @Override
    public String displayName() {
        return firstName.strip() + " " + lastName.strip();
    }
}
