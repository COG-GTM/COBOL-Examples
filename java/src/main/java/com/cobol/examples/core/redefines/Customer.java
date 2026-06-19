package com.cobol.examples.core.redefines;

/**
 * Modern replacement for the {@code REDEFINES} overlay in
 * {@code redifines/redefines.cbl}.
 *
 * <p>The COBOL record overlaid a 30-byte corporation name on top of the
 * first-name/last-name fields and used an {@code 88}-level customer type to
 * decide which interpretation was valid. Java's sealed types express the same
 * "one of N shapes" intent type-safely, eliminating the class of bugs the COBOL
 * comment warned about (reading the wrong overlay for the record type).
 */
public sealed interface Customer permits Customer.Person, Customer.Corp {

    Address address();

    record Address(String street, String state, String zip) {
    }

    record Person(String firstName, String lastName, Address address) implements Customer {
    }

    record Corp(String companyName, Address address) implements Customer {
    }
}
