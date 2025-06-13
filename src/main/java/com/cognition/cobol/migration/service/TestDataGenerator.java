package com.cognition.cobol.migration.service;

import com.cognition.cobol.migration.model.Customer;
import java.util.Arrays;
import java.util.List;

public class TestDataGenerator {
    
    public List<Customer> createTestFile1Data() {
        return Arrays.asList(
            new Customer(1, "last-1", "first-1", 5423, "comment-1"),
            new Customer(5, "last-5", "first-5", 12323, "comment-5"),
            new Customer(10, "last-10", "first-10", 653, "comment-10"),
            new Customer(50, "last-50", "first-50", 5050, "comment-50"),
            new Customer(25, "last-25", "first-25", 7725, "comment-25"),
            new Customer(75, "last-75", "first-75", 1175, "comment-75")
        );
    }
    
    public List<Customer> createTestFile2Data() {
        return Arrays.asList(
            new Customer(999, "last-999", "first-999", 1610, "comment-99"),
            new Customer(3, "last-03", "first-03", 3331, "comment-03"),
            new Customer(30, "last-30", "first-30", 8765, "comment-30"),
            new Customer(85, "last-85", "first-85", 4567, "comment-85"),
            new Customer(24, "last-24", "first-24", 247, "comment-24")
        );
    }
    
    public void createTestFiles(FileProcessor fileProcessor) {
        System.out.println("Creating test data files...");
        
        List<Customer> file1Data = createTestFile1Data();
        List<Customer> file2Data = createTestFile2Data();
        
        fileProcessor.writeCustomersToFile(file1Data, "test-file-1.txt");
        fileProcessor.writeCustomersToFile(file2Data, "test-file-2.txt");
    }
}
