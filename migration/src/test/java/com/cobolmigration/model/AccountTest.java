package com.cobolmigration.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Account model.
 * Verifies field mappings, JPA annotation behavior, and COBOL-style flag handling.
 */
class AccountTest {

    @Test
    void testDefaultConstructor() {
        Account account = new Account();
        assertEquals(0, account.getId());
        assertNull(account.getFirstName());
        assertNull(account.getLastName());
        assertNull(account.getPhone());
        assertNull(account.getAddress());
        assertFalse(account.isEnabled());
        assertNull(account.getCreateDate());
        assertNull(account.getModDate());
    }

    @Test
    void testParameterizedConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Account account = new Account(1L, "John", "Doe", "1234567890",
                "123 Fake St", true, now, now);

        assertEquals(1L, account.getId());
        assertEquals("John", account.getFirstName());
        assertEquals("Doe", account.getLastName());
        assertEquals("1234567890", account.getPhone());
        assertEquals("123 Fake St", account.getAddress());
        assertTrue(account.isEnabled());
        assertEquals(now, account.getCreateDate());
        assertEquals(now, account.getModDate());
    }

    @Test
    void testEnabledFlagMapping() {
        Account account = new Account();

        // COBOL 88-level: ws-account-enabled value 'Y'
        account.setEnabled(true);
        assertEquals("Y", account.getEnabledFlag());

        // COBOL 88-level: ws-account-disabled value 'N'
        account.setEnabled(false);
        assertEquals("N", account.getEnabledFlag());
    }

    @Test
    void testSetEnabledFromFlag() {
        Account account = new Account();

        account.setEnabledFromFlag("Y");
        assertTrue(account.isEnabled());

        account.setEnabledFromFlag("N");
        assertFalse(account.isEnabled());

        account.setEnabledFromFlag("y");
        assertTrue(account.isEnabled());

        account.setEnabledFromFlag("X");
        assertFalse(account.isEnabled());

        account.setEnabledFromFlag("");
        assertFalse(account.isEnabled());
    }

    @Test
    void testFieldLengthsMatchCobol() {
        // Verify fields can hold COBOL-sized data
        // ws-sql-account-first-name pic x(8)
        Account account = new Account();
        account.setFirstName("12345678");
        assertEquals(8, account.getFirstName().length());

        // ws-sql-account-last-name pic x(8)
        account.setLastName("12345678");
        assertEquals(8, account.getLastName().length());

        // ws-sql-account-phone pic x(10)
        account.setPhone("1234567890");
        assertEquals(10, account.getPhone().length());

        // ws-sql-account-address pic x(22)
        account.setAddress("1234567890123456789012");
        assertEquals(22, account.getAddress().length());
    }

    @Test
    void testEquality() {
        Account a1 = new Account();
        a1.setId(1L);
        a1.setFirstName("John");

        Account a2 = new Account();
        a2.setId(1L);
        a2.setFirstName("Different");

        // Equality based on ID
        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());

        Account a3 = new Account();
        a3.setId(2L);
        assertNotEquals(a1, a3);
    }

    @Test
    void testToString() {
        Account account = new Account(1L, "John", "Doe", "1234567890",
                "123 Fake St", true, null, null);
        String str = account.toString();
        assertTrue(str.contains("John"));
        assertTrue(str.contains("Doe"));
        assertTrue(str.contains("1234567890"));
    }

    @Test
    void testSettersAndGetters() {
        Account account = new Account();
        LocalDateTime createDt = LocalDateTime.of(2022, 1, 1, 0, 0);
        LocalDateTime modDt = LocalDateTime.of(2022, 6, 15, 12, 30);

        account.setId(42L);
        account.setFirstName("Mike");
        account.setLastName("Tester1");
        account.setPhone("1555555012");
        account.setAddress("122 Real St, Nowhere");
        account.setEnabled(true);
        account.setCreateDate(createDt);
        account.setModDate(modDt);

        assertEquals(42L, account.getId());
        assertEquals("Mike", account.getFirstName());
        assertEquals("Tester1", account.getLastName());
        assertEquals("1555555012", account.getPhone());
        assertEquals("122 Real St, Nowhere", account.getAddress());
        assertTrue(account.isEnabled());
        assertEquals(createDt, account.getCreateDate());
        assertEquals(modDt, account.getModDate());
    }
}
