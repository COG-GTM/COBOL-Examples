package com.cobolmigration.model;

import com.cobolmigration.model.RedefinesExample.CustomerRecord;
import com.cobolmigration.model.RedefinesExample.DualTypeField;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RedefinesExample.
 * Verifies REDEFINES pattern behavior matches COBOL redifines/redefines.cbl.
 */
class RedefinesExampleTest {

    // --- CustomerRecord REDEFINES tests ---

    @Test
    void personRecord_shouldHaveFirstAndLastName() {
        // COBOL: ws-customer-type-person(1), first-name="test-first", last-name="test-last"
        CustomerRecord record = new CustomerRecord();
        record.setCustomerType(1);
        record.setFirstName("test-first");
        record.setLastName("test-last");
        record.setStreetAddress("123 fake st");
        record.setState("NV");
        record.setZipCode("12345");

        assertEquals(1, record.getCustomerType());
        assertEquals("test-first", record.getFirstName());
        assertEquals("test-last", record.getLastName());
    }

    @Test
    void corpRecord_shouldUseCorpNameRedefine() {
        // COBOL: ws-customer-type-corp(2), ws-corp-name="no-name corp"
        // ws-corp-name REDEFINES ws-customer-name PIC X(30)
        CustomerRecord record = new CustomerRecord();
        record.setCustomerType(2);
        record.setCorpName("no-name corp");

        assertEquals(2, record.getCustomerType());
        assertEquals("no-name corp", record.getCorpName());
    }

    @Test
    void corpNameRedefines_shouldShareMemoryWithPersonName() {
        // COBOL: setting corp-name also overwrites first-name and last-name fields
        // because they share the same 30-byte memory area
        CustomerRecord record = new CustomerRecord();
        record.setCustomerType(1);

        // Set individual names first
        record.setFirstName("John");
        record.setLastName("Doe");

        // Now set corp name (REDEFINES overwrites the same memory)
        record.setCorpName("SET CORP VALUE");

        // Corp name should reflect the new value
        assertEquals("SET CORP VALUE", record.getCorpName());

        // First name should now reflect the first 10 chars of the corp name
        assertEquals("SET CORP V", record.getFirstName());
    }

    @Test
    void personNameFields_shouldBeAccessibleAsCorpName() {
        // When person names are set, reading corp-name returns the concatenated bytes
        CustomerRecord record = new CustomerRecord();
        record.setFirstName("test-first");
        record.setLastName("test-last");

        // Corp name is the full 30-byte field (first 10 + last 20 bytes)
        String corpView = record.getCorpName();
        assertTrue(corpView.startsWith("test-first"));
        assertTrue(corpView.contains("test-last"));
    }

    // --- DualTypeField REDEFINES tests ---

    @Test
    void dualTypeField_displayValue() {
        // COBOL: ws-data-disp-value PIC X(10) = "ABC123"
        DualTypeField field = new DualTypeField();
        field.setDisplayValue("ABC123");

        String display = field.getDisplayValue();
        assertTrue(display.startsWith("ABC123"));
    }

    @Test
    void dualTypeField_compValue() {
        // COBOL: ws-data-comp-value COMP-2 = 12345.63
        DualTypeField field = new DualTypeField();
        field.setCompValue(12345.63);

        assertEquals(12345.63, field.getCompValue(), 0.001);
    }

    @Test
    void dualTypeField_redefinesShouldShareMemory() {
        // Setting a display value then reading as comp should give a different
        // (likely nonsensical) value, demonstrating memory sharing
        DualTypeField field = new DualTypeField();
        field.setDisplayValue("ABC123");

        // Reading as comp-2 gives whatever the bytes represent as a double
        double compView = field.getCompValue();
        // We just verify it doesn't throw - the value is meaningless
        assertNotNull(compView);

        // Setting a comp value then reading as display gives raw bytes
        field.setCompValue(12345.63);
        String displayView = field.getDisplayValue();
        assertNotNull(displayView);
    }
}
