package com.cognition.cobol.migration.service;

import com.cognition.cobol.migration.model.Customer;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class TestDataGeneratorTest {
    
    private final TestDataGenerator generator = new TestDataGenerator();
    
    @Test
    public void testCreateTestFile1Data() {
        List<Customer> customers = generator.createTestFile1Data();
        
        assertEquals(6, customers.size());
        
        assertEquals(1, customers.get(0).getCustomerId());
        assertEquals("last-1", customers.get(0).getLastName());
        assertEquals("first-1", customers.get(0).getFirstName());
        assertEquals(5423, customers.get(0).getContractId());
        assertEquals("comment-1", customers.get(0).getComment());
        
        assertEquals(75, customers.get(5).getCustomerId());
        assertEquals("last-75", customers.get(5).getLastName());
        assertEquals("first-75", customers.get(5).getFirstName());
        assertEquals(1175, customers.get(5).getContractId());
        assertEquals("comment-75", customers.get(5).getComment());
    }
    
    @Test
    public void testCreateTestFile2Data() {
        List<Customer> customers = generator.createTestFile2Data();
        
        assertEquals(5, customers.size());
        
        assertEquals(999, customers.get(0).getCustomerId());
        assertEquals("last-999", customers.get(0).getLastName());
        assertEquals("first-999", customers.get(0).getFirstName());
        assertEquals(1610, customers.get(0).getContractId());
        assertEquals("comment-99", customers.get(0).getComment());
        
        assertEquals(24, customers.get(4).getCustomerId());
        assertEquals("last-24", customers.get(4).getLastName());
        assertEquals("first-24", customers.get(4).getFirstName());
        assertEquals(247, customers.get(4).getContractId());
        assertEquals("comment-24", customers.get(4).getComment());
    }
    
    @Test
    public void testFile1DataMatchesCOBOLOrder() {
        List<Customer> customers = generator.createTestFile1Data();
        
        int[] expectedIds = {1, 5, 10, 50, 25, 75};
        for (int i = 0; i < expectedIds.length; i++) {
            assertEquals(expectedIds[i], customers.get(i).getCustomerId());
        }
    }
    
    @Test
    public void testFile2DataMatchesCOBOLOrder() {
        List<Customer> customers = generator.createTestFile2Data();
        
        int[] expectedIds = {999, 3, 30, 85, 24};
        for (int i = 0; i < expectedIds.length; i++) {
            assertEquals(expectedIds[i], customers.get(i).getCustomerId());
        }
    }
}
