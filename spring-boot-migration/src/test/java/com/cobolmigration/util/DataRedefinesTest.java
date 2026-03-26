package com.cobolmigration.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for DataRedefines verifying Java equivalents of COBOL REDEFINES.
 * Validates behavior matching redifines/redefines.cbl.
 */
class DataRedefinesTest {

    @Test
    void writeAndReadString_shouldPreserveValue() {
        DataRedefines buffer = new DataRedefines(30);
        buffer.writeString(0, 10, "test-first");
        assertEquals("test-first", buffer.readString(0, 10).trim());
    }

    @Test
    void writeString_shouldPadWithSpaces() {
        DataRedefines buffer = new DataRedefines(20);
        buffer.writeString(0, 10, "short");
        String result = buffer.readString(0, 10);
        assertEquals("short     ", result);
    }

    @Test
    void writeAndReadDouble_shouldPreserveValue() {
        DataRedefines buffer = new DataRedefines(10);
        buffer.writeDouble(0, 12345.63);
        assertEquals(12345.63, buffer.readDouble(0), 0.001);
    }

    @Test
    void redefines_sameMemoryArea_shouldOverlap() {
        // Demonstrates the core REDEFINES behavior:
        // Writing to one view of the data affects the other view
        DataRedefines buffer = new DataRedefines(30);

        // Write as first_name + last_name (person record)
        buffer.writeString(0, 10, "test-first");
        buffer.writeString(10, 20, "test-last");

        // Read as corp_name (same 30-byte area)
        String corpView = buffer.readString(0, 30).trim();
        assertTrue(corpView.startsWith("test-first"));

        // Now write as corp_name
        buffer.writeString(0, 30, "no-name corp");

        // Read back as first_name - should be overwritten
        String firstName = buffer.readString(0, 10).trim();
        assertEquals("no-name co", firstName);
    }

    @Test
    void customerNameRedefines_personRecord_shouldWork() {
        DataRedefines.CustomerNameRedefines name = new DataRedefines.CustomerNameRedefines();
        name.setFirstName("test-first");
        name.setLastName("test-last");

        assertEquals("test-first", name.getFirstName());
        assertEquals("test-last", name.getLastName());
    }

    @Test
    void customerNameRedefines_corpRecord_shouldOverwritePersonFields() {
        DataRedefines.CustomerNameRedefines name = new DataRedefines.CustomerNameRedefines();
        name.setCorpName("no-name corp");

        // Corp name occupies the same area as first+last name
        assertEquals("no-name corp", name.getCorpName());
        // First name is the first 10 chars of corp name
        assertEquals("no-name co", name.getFirstName());
    }

    @Test
    void differentDataTypes_redefines_shouldReinterpretBytes() {
        // Mirrors the COBOL example where COMP-2 redefines PIC X(10)
        DataRedefines buffer = new DataRedefines(10);

        // Write as string
        buffer.writeString(0, 10, "ABC123");
        String asString = buffer.readString(0, 10);
        assertTrue(asString.startsWith("ABC123"));

        // Write as double in a fresh buffer
        DataRedefines buffer2 = new DataRedefines(10);
        buffer2.writeDouble(0, 12345.63);
        double asDouble = buffer2.readDouble(0);
        assertEquals(12345.63, asDouble, 0.001);

        // Reading the double bytes as string gives gibberish (expected!)
        String doubleAsString = buffer2.readString(0, 8);
        // Just verify we can read without error
        assertEquals(8, doubleAsString.length());
    }
}
