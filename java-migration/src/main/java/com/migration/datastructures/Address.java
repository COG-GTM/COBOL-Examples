package com.migration.datastructures;

/**
 * Address record mapping to COBOL ws-customer-address group.
 *
 * COBOL:
 *   05 ws-customer-address.
 *       10 ws-street-address   PIC X(20).
 *       10 ws-state            PIC XX.
 *       10 ws-zip-code         PIC 9(5).
 */
public record Address(
        String street,
        String state,
        String zipCode
) {

    public String formatted() {
        return street.strip() + ", " + state.strip() + " " + zipCode.strip();
    }
}
