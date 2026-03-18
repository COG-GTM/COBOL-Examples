package com.migration.datastructures;

/**
 * Record implementation for corporate-type customers.
 *
 * COBOL equivalent: ws-customer with ws-customer-type-corp (value 2)
 * Uses ws-corp-name (REDEFINES ws-customer-name) as PIC X(30).
 */
public record CorporateCustomer(
        String corpName,
        Address address
) implements Customer {

    @Override
    public String displayName() {
        return corpName.strip();
    }
}
