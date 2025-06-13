package com.cognition.cobol.migration;

import com.cognition.cobol.migration.model.Customer;
import com.cognition.cobol.migration.service.FileProcessor;
import com.cognition.cobol.migration.service.MergeSortService;
import com.cognition.cobol.migration.service.TestDataGenerator;
import java.util.List;

public class MergeSortApplication {
    
    public static void main(String[] args) {
        FileProcessor fileProcessor = new FileProcessor();
        MergeSortService mergeSortService = new MergeSortService(fileProcessor);
        TestDataGenerator testDataGenerator = new TestDataGenerator();
        
        testDataGenerator.createTestFiles(fileProcessor);
        
        System.out.println("Merging and sorting files...");
        List<Customer> mergedCustomers = mergeSortService.mergeFilesSortedByCustomerId(
            "test-file-1.txt", "test-file-2.txt");
        
        fileProcessor.writeCustomersToFile(mergedCustomers, "merge-output.txt");
        fileProcessor.displayCustomers(mergedCustomers, "Merged file contents (sorted by customer ID ascending):");
        
        System.out.println("Sorting merged file on descending contract id....");
        List<Customer> sortedByContract = mergeSortService.sortByContractIdDescending(mergedCustomers);
        
        fileProcessor.writeCustomersToFile(sortedByContract, "sorted-contract-id.txt");
        fileProcessor.displayCustomers(sortedByContract, "Sorted file contents (sorted by contract ID descending):");
        
        System.out.println("Done.");
    }
}
