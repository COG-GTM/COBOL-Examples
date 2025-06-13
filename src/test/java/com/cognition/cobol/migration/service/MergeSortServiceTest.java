package com.cognition.cobol.migration.service;

import com.cognition.cobol.migration.model.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class MergeSortServiceTest {
    
    private MergeSortService mergeSortService;
    private FileProcessor fileProcessor;
    private final String testFile1 = "test-merge-file-1.txt";
    private final String testFile2 = "test-merge-file-2.txt";
    
    @BeforeEach
    public void setUp() {
        fileProcessor = new FileProcessor();
        mergeSortService = new MergeSortService(fileProcessor);
    }
    
    @AfterEach
    public void tearDown() {
        deleteFileIfExists(testFile1);
        deleteFileIfExists(testFile2);
    }
    
    private void deleteFileIfExists(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
    }
    
    @Test
    public void testMergeFilesSortedByCustomerId() {
        List<Customer> file1Data = Arrays.asList(
            new Customer(10, "last-10", "first-10", 100, "comment-10"),
            new Customer(5, "last-5", "first-5", 200, "comment-5"),
            new Customer(20, "last-20", "first-20", 300, "comment-20")
        );
        
        List<Customer> file2Data = Arrays.asList(
            new Customer(15, "last-15", "first-15", 150, "comment-15"),
            new Customer(1, "last-1", "first-1", 250, "comment-1")
        );
        
        fileProcessor.writeCustomersToFile(file1Data, testFile1);
        fileProcessor.writeCustomersToFile(file2Data, testFile2);
        
        List<Customer> mergedCustomers = mergeSortService.mergeFilesSortedByCustomerId(testFile1, testFile2);
        
        assertEquals(5, mergedCustomers.size());
        
        int[] expectedIds = {1, 5, 10, 15, 20};
        for (int i = 0; i < expectedIds.length; i++) {
            assertEquals(expectedIds[i], mergedCustomers.get(i).getCustomerId());
        }
    }
    
    @Test
    public void testSortByContractIdDescending() {
        List<Customer> customers = Arrays.asList(
            new Customer(1, "last-1", "first-1", 100, "comment-1"),
            new Customer(2, "last-2", "first-2", 300, "comment-2"),
            new Customer(3, "last-3", "first-3", 200, "comment-3"),
            new Customer(4, "last-4", "first-4", 400, "comment-4")
        );
        
        List<Customer> sortedCustomers = mergeSortService.sortByContractIdDescending(customers);
        
        assertEquals(4, sortedCustomers.size());
        
        int[] expectedContractIds = {400, 300, 200, 100};
        for (int i = 0; i < expectedContractIds.length; i++) {
            assertEquals(expectedContractIds[i], sortedCustomers.get(i).getContractId());
        }
    }
    
    @Test
    public void testCOBOLTestDataMergeAndSort() {
        TestDataGenerator generator = new TestDataGenerator();
        List<Customer> file1Data = generator.createTestFile1Data();
        List<Customer> file2Data = generator.createTestFile2Data();
        
        fileProcessor.writeCustomersToFile(file1Data, testFile1);
        fileProcessor.writeCustomersToFile(file2Data, testFile2);
        
        List<Customer> mergedCustomers = mergeSortService.mergeFilesSortedByCustomerId(testFile1, testFile2);
        
        assertEquals(11, mergedCustomers.size());
        
        int[] expectedMergedIds = {1, 3, 5, 10, 24, 25, 30, 50, 75, 85, 999};
        for (int i = 0; i < expectedMergedIds.length; i++) {
            assertEquals(expectedMergedIds[i], mergedCustomers.get(i).getCustomerId());
        }
        
        List<Customer> sortedByContract = mergeSortService.sortByContractIdDescending(mergedCustomers);
        
        assertTrue(sortedByContract.get(0).getContractId() >= sortedByContract.get(1).getContractId());
        assertTrue(sortedByContract.get(1).getContractId() >= sortedByContract.get(2).getContractId());
    }
}
