import java.io.*;
import java.util.*;

public class MergeSortExample {
    
    public static void main(String[] args) {
        try {
            createTestData();
            mergeAndDisplayFiles();
            sortAndDisplayFile();
            System.out.println("Done.");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createTestData() throws IOException {
        FileUtils.createTestDataFiles();
    }
    
    private static void mergeAndDisplayFiles() throws IOException {
        System.out.println("Merging and sorting files...");
        
        List<Customer> mergedCustomers = FileUtils.mergeFilesByCustomerId("test-file-1.txt", "test-file-2.txt");
        
        FileUtils.writeCustomersToFile(mergedCustomers, "merge-output.txt");
        
        FileUtils.displayCustomers(mergedCustomers);
    }
    
    private static void sortAndDisplayFile() throws IOException {
        System.out.println("Sorting merged file on descending contract id....");
        
        List<Customer> mergedCustomers = FileUtils.readCustomersFromFile("merge-output.txt");
        
        List<Customer> sortedCustomers = FileUtils.sortByContractIdDescending(mergedCustomers);
        
        FileUtils.writeCustomersToFile(sortedCustomers, "sorted-contract-id.txt");
        
        FileUtils.displayCustomers(sortedCustomers);
    }
}
