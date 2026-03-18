package com.migration.datastructures;

/**
 * Phase 2: Data Structures - Customer sealed interface
 *
 * Migrates COBOL REDEFINES pattern from redifines/redefines.cbl.
 *
 * In COBOL, ws-customer uses REDEFINES to interpret ws-customer-name as either:
 * - First + Last name fields (when ws-customer-type = 1, person)
 * - ws-corp-name (when ws-customer-type = 2, corporation)
 *
 * In Java, we use a sealed interface with two record implementations
 * to achieve type-safe polymorphism instead of memory reinterpretation.
 *
 * COBOL:
 *   05 ws-customer-type       PIC 9.
 *       88 ws-customer-type-person  VALUE 1.
 *       88 ws-customer-type-corp    VALUE 2.
 *   05 ws-customer-name.
 *       10 ws-customer-first-name   PIC X(10).
 *       10 ws-customer-last-name    PIC X(20).
 *   05 ws-corp-name REDEFINES ws-customer-name PIC X(30).
 *   05 ws-customer-address.
 *       10 ws-street-address        PIC X(20).
 *       10 ws-state                 PIC XX.
 *       10 ws-zip-code              PIC 9(5).
 */
public sealed interface Customer permits PersonCustomer, CorporateCustomer {

    Address address();

    String displayName();
}
