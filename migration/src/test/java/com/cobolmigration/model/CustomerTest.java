package com.cobolmigration.model;

import com.cobolmigration.model.Customer.CustomerType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Customer model.
 * Verifies REDEFINES polymorphism (person vs corp name resolution)
 * matching COBOL behavior from redifines/redefines.cbl.
 */
class CustomerTest {

    @Test
    void testPersonConstructor() {
        // From redefines.cbl line 47-52: Person record with first/last name
        Customer person = new Customer("test-first", "test-last",
                "123 fake st", "NV", 12345);

        assertEquals(CustomerType.PERSON, person.getCustomerType());
        assertTrue(person.isPerson());
        assertFalse(person.isCorporation());
        assertEquals("test-first", person.getFirstName());
        assertEquals("test-last", person.getLastName());
        assertEquals("123 fake st", person.getStreetAddress());
        assertEquals("NV", person.getState());
        assertEquals(12345, person.getZipCode());
    }

    @Test
    void testCorporationConstructor() {
        // From redefines.cbl line 55-59: Corp record with corp name
        Customer corp = new Customer("no-name corp",
                "567 real st", "NY", 11795);

        assertEquals(CustomerType.CORPORATION, corp.getCustomerType());
        assertFalse(corp.isPerson());
        assertTrue(corp.isCorporation());
        assertEquals("no-name corp", corp.getCorpName());
        assertEquals("567 real st", corp.getStreetAddress());
        assertEquals("NY", corp.getState());
        assertEquals(11795, corp.getZipCode());
    }

    @Test
    void testGetDisplayNameForPerson() {
        // COBOL behavior: for person type, display first+last name
        Customer person = new Customer("test-first", "test-last",
                "123 fake st", "NV", 12345);
        assertEquals("test-first test-last", person.getDisplayName());
    }

    @Test
    void testGetDisplayNameForCorporation() {
        // COBOL behavior: for corp type, display corp name (REDEFINES)
        Customer corp = new Customer("no-name corp",
                "567 real st", "NY", 11795);
        assertEquals("no-name corp", corp.getDisplayName());
    }

    @Test
    void testRedefinesBehavior() {
        // From redefines.cbl line 61-66: Person record with corp name entered
        // This tests the REDEFINES behavior where setting ws-corp-name
        // on a PERSON type still works but getDisplayName uses first+last
        Customer customer = new Customer();
        customer.setCustomerType(CustomerType.PERSON);
        customer.setCorpName("SET CORP VALUE");
        customer.setStreetAddress("890 what st");
        customer.setState("MA");
        customer.setZipCode(9345);

        // Even though corpName is set, since type is PERSON,
        // it should try to use firstName/lastName
        assertTrue(customer.isPerson());

        // When firstName/lastName are null but corpName is set,
        // for PERSON type, getDisplayName returns empty (firstName+lastName)
        assertEquals("", customer.getDisplayName());
    }

    @Test
    void testCustomerTypeEnum() {
        assertEquals(1, CustomerType.PERSON.getCode());
        assertEquals(2, CustomerType.CORPORATION.getCode());

        assertEquals(CustomerType.PERSON, CustomerType.fromCode(1));
        assertEquals(CustomerType.CORPORATION, CustomerType.fromCode(2));
    }

    @Test
    void testCustomerTypeFromCodeInvalid() {
        assertThrows(IllegalArgumentException.class, () -> CustomerType.fromCode(99));
    }

    @Test
    void testGetDisplayNameWithTrimming() {
        // COBOL fields are padded with spaces; verify trimming works
        Customer person = new Customer("John      ", "Doe               ",
                "123 St", "CA", 90210);
        assertEquals("John Doe", person.getDisplayName());
    }

    @Test
    void testGetDisplayNameEmptyFields() {
        Customer person = new Customer("", "", "123 St", "CA", 90210);
        assertEquals("", person.getDisplayName());
    }

    @Test
    void testGetDisplayNameOnlyFirstName() {
        Customer person = new Customer("John", "", "123 St", "CA", 90210);
        assertEquals("John", person.getDisplayName());
    }

    @Test
    void testGetDisplayNameOnlyLastName() {
        Customer person = new Customer("", "Doe", "123 St", "CA", 90210);
        assertEquals("Doe", person.getDisplayName());
    }

    @Test
    void testEquality() {
        Customer c1 = new Customer("John", "Doe", "123 St", "CA", 90210);
        Customer c2 = new Customer("John", "Doe", "123 St", "CA", 90210);
        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void testInequalityDifferentType() {
        Customer person = new Customer("John", "Doe", "123 St", "CA", 90210);
        Customer corp = new Customer("John Doe Corp", "123 St", "CA", 90210);
        assertNotEquals(person, corp);
    }

    @Test
    void testToString() {
        Customer person = new Customer("John", "Doe", "123 St", "CA", 90210);
        String str = person.toString();
        assertTrue(str.contains("PERSON"));
        assertTrue(str.contains("John Doe"));
    }

    @Test
    void testDefaultConstructorAndSetters() {
        Customer customer = new Customer();
        customer.setCustomerType(CustomerType.CORPORATION);
        customer.setCorpName("Acme Corp");
        customer.setStreetAddress("100 Main St");
        customer.setState("TX");
        customer.setZipCode(75001);

        assertEquals(CustomerType.CORPORATION, customer.getCustomerType());
        assertEquals("Acme Corp", customer.getCorpName());
        assertEquals("100 Main St", customer.getStreetAddress());
        assertEquals("TX", customer.getState());
        assertEquals(75001, customer.getZipCode());
        assertEquals("Acme Corp", customer.getDisplayName());
    }
}
