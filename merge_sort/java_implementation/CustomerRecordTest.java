import org.junit.*;
import static org.junit.Assert.*;

public class CustomerRecordTest {
    
    @Test
    public void testCustomerRecordConstructor() {
        CustomerRecord record = new CustomerRecord(1, "Doe", "John", 12345, "Test comment");
        
        assertEquals(1, record.getCustomerId());
        assertEquals("Doe", record.getCustomerLastName());
        assertEquals("John", record.getCustomerFirstName());
        assertEquals(12345, record.getCustomerContractId());
        assertEquals("Test comment", record.getCustomerComment());
    }
    
    @Test
    public void testCustomerRecordSetters() {
        CustomerRecord record = new CustomerRecord(1, "Doe", "John", 12345, "Test");
        
        record.setCustomerId(2);
        record.setCustomerLastName("Smith");
        record.setCustomerFirstName("Jane");
        record.setCustomerContractId(54321);
        record.setCustomerComment("Updated comment");
        
        assertEquals(2, record.getCustomerId());
        assertEquals("Smith", record.getCustomerLastName());
        assertEquals("Jane", record.getCustomerFirstName());
        assertEquals(54321, record.getCustomerContractId());
        assertEquals("Updated comment", record.getCustomerComment());
    }
    
    @Test
    public void testToString() {
        CustomerRecord record = new CustomerRecord(123, "LastName", "FirstName", 456, "Comment");
        String output = record.toString();
        
        assertNotNull(output);
        assertTrue("Output should contain customer ID", output.contains("00123"));
        assertTrue("Output should contain last name", output.contains("LastName"));
        assertTrue("Output should contain first name", output.contains("FirstName"));
        assertTrue("Output should contain contract ID", output.contains("00456"));
        assertTrue("Output should contain comment", output.contains("Comment"));
    }
    
    @Test
    public void testFromString() {
        String recordLine = String.format("%05d%-50s%-50s%05d%-25s", 123, "LastName", "FirstName", 456, "Comment");
        CustomerRecord record = CustomerRecord.fromString(recordLine);
        
        assertEquals(123, record.getCustomerId());
        assertTrue(record.getCustomerLastName().contains("LastName"));
        assertTrue(record.getCustomerFirstName().contains("FirstName"));
        assertEquals(456, record.getCustomerContractId());
        assertTrue(record.getCustomerComment().contains("Comment"));
    }
    
    @Test
    public void testRoundTripConversion() {
        CustomerRecord original = new CustomerRecord(999, "TestLast", "TestFirst", 88888, "TestComment");
        String serialized = original.toString();
        CustomerRecord deserialized = CustomerRecord.fromString(serialized);
        
        assertEquals(original.getCustomerId(), deserialized.getCustomerId());
        assertEquals(original.getCustomerContractId(), deserialized.getCustomerContractId());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testFromStringWithInvalidInput() {
        CustomerRecord.fromString("too short");
    }
}
