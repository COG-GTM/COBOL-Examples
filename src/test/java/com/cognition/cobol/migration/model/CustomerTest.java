package com.cognition.cobol.migration.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CustomerTest {
    
    @Test
    public void testCustomerCreation() {
        Customer customer = new Customer(12345, "Smith", "John", 67890, "Test comment");
        
        assertEquals(12345, customer.getCustomerId());
        assertEquals("Smith", customer.getLastName());
        assertEquals("John", customer.getFirstName());
        assertEquals(67890, customer.getContractId());
        assertEquals("Test comment", customer.getComment());
    }
    
    @Test
    public void testCustomerIdValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Customer(-1, "Smith", "John", 67890, "Test");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new Customer(100000, "Smith", "John", 67890, "Test");
        });
    }
    
    @Test
    public void testContractIdValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Customer(12345, "Smith", "John", -1, "Test");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new Customer(12345, "Smith", "John", 100000, "Test");
        });
    }
    
    @Test
    public void testStringFieldTruncation() {
        String longLastName = "A".repeat(60);
        String longFirstName = "B".repeat(60);
        String longComment = "C".repeat(30);
        
        Customer customer = new Customer(12345, longLastName, longFirstName, 67890, longComment);
        
        assertEquals(50, customer.getLastName().length());
        assertEquals(50, customer.getFirstName().length());
        assertEquals(25, customer.getComment().length());
    }
    
    @Test
    public void testToStringFormat() {
        Customer customer = new Customer(1, "last-1", "first-1", 5423, "comment-1");
        String result = customer.toString();
        
        assertTrue(result.startsWith("00001"));
        assertTrue(result.contains("last-1"));
        assertTrue(result.contains("first-1"));
        assertTrue(result.contains("05423"));
        assertTrue(result.contains("comment-1"));
        assertEquals(135, result.length());
    }
    
    @Test
    public void testEqualsAndHashCode() {
        Customer customer1 = new Customer(1, "Smith", "John", 100, "Test");
        Customer customer2 = new Customer(1, "Smith", "John", 100, "Test");
        Customer customer3 = new Customer(2, "Smith", "John", 100, "Test");
        
        assertEquals(customer1, customer2);
        assertNotEquals(customer1, customer3);
        assertEquals(customer1.hashCode(), customer2.hashCode());
    }
    
    @Test
    public void testNullStringHandling() {
        Customer customer = new Customer(1, null, null, 100, null);
        
        assertEquals("", customer.getLastName());
        assertEquals("", customer.getFirstName());
        assertEquals("", customer.getComment());
    }
}
