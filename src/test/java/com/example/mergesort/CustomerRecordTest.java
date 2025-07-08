package com.example.mergesort;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for CustomerRecord class covering construction, comparison, and formatting.
 */
class CustomerRecordTest {
    
    @Test
    void testConstructor_AllFields() {
        CustomerRecord record = new CustomerRecord(12345, "Smith", "John", 67890, "Test comment");
        
        assertEquals(12345, record.getCustomerId());
        assertEquals("Smith", record.getCustomerLastName());
        assertEquals("John", record.getCustomerFirstName());
        assertEquals(67890, record.getCustomerContractId());
        assertEquals("Test comment", record.getCustomerComment());
    }
    
    @Test
    void testConstructor_NullStrings() {
        CustomerRecord record = new CustomerRecord(1, null, null, 2, null);
        
        assertEquals("", record.getCustomerLastName());
        assertEquals("", record.getCustomerFirstName());
        assertEquals("", record.getCustomerComment());
    }
    
    @Test
    void testSetters_NullStrings() {
        CustomerRecord record = new CustomerRecord();
        
        record.setCustomerLastName(null);
        record.setCustomerFirstName(null);
        record.setCustomerComment(null);
        
        assertEquals("", record.getCustomerLastName());
        assertEquals("", record.getCustomerFirstName());
        assertEquals("", record.getCustomerComment());
    }
    
    @Test
    void testCompareTo() {
        CustomerRecord record1 = new CustomerRecord(1, "Last1", "First1", 100, "Comment1");
        CustomerRecord record2 = new CustomerRecord(2, "Last2", "First2", 200, "Comment2");
        CustomerRecord record3 = new CustomerRecord(1, "Last3", "First3", 300, "Comment3");
        
        assertTrue(record1.compareTo(record2) < 0);
        assertTrue(record2.compareTo(record1) > 0);
        assertEquals(0, record1.compareTo(record3));
    }
    
    @Test
    void testEquals() {
        CustomerRecord record1 = new CustomerRecord(1, "Smith", "John", 100, "Comment");
        CustomerRecord record2 = new CustomerRecord(1, "Smith", "John", 100, "Comment");
        CustomerRecord record3 = new CustomerRecord(2, "Smith", "John", 100, "Comment");
        
        assertEquals(record1, record2);
        assertNotEquals(record1, record3);
        assertNotEquals(record1, null);
        assertNotEquals(record1, "not a record");
    }
    
    @Test
    void testHashCode() {
        CustomerRecord record1 = new CustomerRecord(1, "Smith", "John", 100, "Comment");
        CustomerRecord record2 = new CustomerRecord(1, "Smith", "John", 100, "Comment");
        
        assertEquals(record1.hashCode(), record2.hashCode());
    }
    
    @Test
    void testToString_Formatting() {
        CustomerRecord record = new CustomerRecord(123, "Smith", "John", 456, "Test");
        String result = record.toString();
        
        assertEquals(135, result.length());
        assertTrue(result.startsWith("00123"));
        assertTrue(result.contains("Smith"));
        assertTrue(result.contains("John"));
        assertTrue(result.contains("00456"));
        assertTrue(result.contains("Test"));
    }
    
    @Test
    void testToString_Padding() {
        CustomerRecord record = new CustomerRecord(1, "A", "B", 2, "C");
        String result = record.toString();
        
        assertEquals(135, result.length());
        assertTrue(result.startsWith("00001"));
        assertTrue(result.contains("A" + " ".repeat(49)));
        assertTrue(result.contains("B" + " ".repeat(49)));
        assertTrue(result.contains("00002"));
        assertTrue(result.contains("C" + " ".repeat(24)));
    }
    
    @Test
    void testToString_Truncation() {
        String longString = "A".repeat(100);
        CustomerRecord record = new CustomerRecord(1, longString, longString, 2, longString);
        String result = record.toString();
        
        assertEquals(135, result.length());
        assertTrue(result.contains("A".repeat(50)));
        assertTrue(result.contains("A".repeat(25)));
    }
}
