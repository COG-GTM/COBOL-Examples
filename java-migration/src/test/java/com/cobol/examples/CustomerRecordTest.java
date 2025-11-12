package com.cobol.examples;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CustomerRecord class.
 * Tests data structure conversions from COBOL to Java.
 */
public class CustomerRecordTest {

    @Test
    public void testConstructorAndGetters() {
        CustomerRecord record = new CustomerRecord(1, "Smith", "John", 5423, "Test comment");
        
        assertEquals(1, record.getCustomerId());
        assertEquals("Smith", record.getLastName());
        assertEquals("John", record.getFirstName());
        assertEquals(5423, record.getContractId());
        assertEquals("Test comment", record.getComment());
    }

    @Test
    public void testSetters() {
        CustomerRecord record = new CustomerRecord();
        
        record.setCustomerId(999);
        record.setLastName("Doe");
        record.setFirstName("Jane");
        record.setContractId(1234);
        record.setComment("Another comment");
        
        assertEquals(999, record.getCustomerId());
        assertEquals("Doe", record.getLastName());
        assertEquals("Jane", record.getFirstName());
        assertEquals(1234, record.getContractId());
        assertEquals("Another comment", record.getComment());
    }

    @Test
    public void testToString() {
        CustomerRecord record = new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1");
        String output = record.toString();
        
        assertNotNull(output);
        assertEquals(135, output.length());
        assertTrue(output.startsWith("00001"));
        assertTrue(output.contains("last-1"));
        assertTrue(output.contains("first-1"));
        assertTrue(output.contains("05423"));
        assertTrue(output.contains("comment-1"));
    }

    @Test
    public void testFromString() {
        String line = "00001last-1                                            first-1                                           05423comment-1                ";
        CustomerRecord record = CustomerRecord.fromString(line);
        
        assertEquals(1, record.getCustomerId());
        assertEquals("last-1", record.getLastName());
        assertEquals("first-1", record.getFirstName());
        assertEquals(5423, record.getContractId());
        assertEquals("comment-1", record.getComment());
    }

    @Test
    public void testFromStringWithLongNames() {
        CustomerRecord original = new CustomerRecord(99999, 
            "VeryLongLastNameThatExceedsFiftyCharactersAndShouldBeTruncated", 
            "VeryLongFirstNameThatExceedsFiftyCharactersAndShouldBeTruncated",
            12345, 
            "VeryLongCommentThatExceedsTwentyFiveCharacters");
        
        String formatted = original.toString();
        CustomerRecord parsed = CustomerRecord.fromString(formatted);
        
        assertEquals(99999, parsed.getCustomerId());
        assertEquals(12345, parsed.getContractId());
    }

    @Test
    public void testFromStringInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> {
            CustomerRecord.fromString("short");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            CustomerRecord.fromString(null);
        });
    }

    @Test
    public void testRoundTrip() {
        CustomerRecord original = new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50");
        String formatted = original.toString();
        CustomerRecord parsed = CustomerRecord.fromString(formatted);
        
        assertEquals(original.getCustomerId(), parsed.getCustomerId());
        assertEquals(original.getLastName(), parsed.getLastName());
        assertEquals(original.getFirstName(), parsed.getFirstName());
        assertEquals(original.getContractId(), parsed.getContractId());
        assertEquals(original.getComment(), parsed.getComment());
    }
}
