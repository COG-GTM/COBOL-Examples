package com.cognition.cobol.migration.service;

import com.cognition.cobol.migration.model.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class FileProcessorTest {
    
    private FileProcessor fileProcessor;
    private final String testFileName = "test-customer-file.txt";
    
    @BeforeEach
    public void setUp() {
        fileProcessor = new FileProcessor();
    }
    
    @AfterEach
    public void tearDown() {
        File testFile = new File(testFileName);
        if (testFile.exists()) {
            testFile.delete();
        }
    }
    
    @Test
    public void testWriteAndReadCustomers() {
        List<Customer> originalCustomers = Arrays.asList(
            new Customer(1, "Smith", "John", 100, "Test1"),
            new Customer(2, "Doe", "Jane", 200, "Test2"),
            new Customer(3, "Johnson", "Bob", 300, "Test3")
        );
        
        fileProcessor.writeCustomersToFile(originalCustomers, testFileName);
        
        List<Customer> readCustomers = fileProcessor.readCustomersFromFile(testFileName);
        
        assertEquals(3, readCustomers.size());
        
        for (int i = 0; i < originalCustomers.size(); i++) {
            Customer original = originalCustomers.get(i);
            Customer read = readCustomers.get(i);
            
            assertEquals(original.getCustomerId(), read.getCustomerId());
            assertEquals(original.getLastName(), read.getLastName());
            assertEquals(original.getFirstName(), read.getFirstName());
            assertEquals(original.getContractId(), read.getContractId());
            assertEquals(original.getComment(), read.getComment());
        }
    }
    
    @Test
    public void testReadEmptyFile() {
        fileProcessor.writeCustomersToFile(Arrays.asList(), testFileName);
        List<Customer> customers = fileProcessor.readCustomersFromFile(testFileName);
        assertTrue(customers.isEmpty());
    }
    
    @Test
    public void testReadNonExistentFile() {
        assertThrows(RuntimeException.class, () -> {
            fileProcessor.readCustomersFromFile("non-existent-file.txt");
        });
    }
    
    @Test
    public void testCustomerRecordFormatting() {
        Customer customer = new Customer(1, "last-1", "first-1", 5423, "comment-1");
        List<Customer> customers = Arrays.asList(customer);
        
        fileProcessor.writeCustomersToFile(customers, testFileName);
        List<Customer> readCustomers = fileProcessor.readCustomersFromFile(testFileName);
        
        assertEquals(1, readCustomers.size());
        Customer readCustomer = readCustomers.get(0);
        
        assertEquals(1, readCustomer.getCustomerId());
        assertEquals("last-1", readCustomer.getLastName());
        assertEquals("first-1", readCustomer.getFirstName());
        assertEquals(5423, readCustomer.getContractId());
        assertEquals("comment-1", readCustomer.getComment());
    }
}
