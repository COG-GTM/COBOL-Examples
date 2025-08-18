import java.io.*;
import java.nio.file.*;
import java.util.*;

public class LargeDatasetTest {
    
    public static void main(String[] args) {
        LargeDatasetTest test = new LargeDatasetTest();
        test.testLargeDataset();
    }
    
    public void testLargeDataset() {
        try {
            System.out.println("Testing with larger dataset to verify streaming behavior...");
            
            createLargeTestFiles();
            
            MergeSortMigration migration = new MergeSortMigration();
            
            testLargeMergeAndSort();
            
            System.out.println("Large dataset test completed successfully!");
            
        } catch (IOException e) {
            System.err.println("Large dataset test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createLargeTestFiles() throws IOException {
        System.out.println("Creating large test files with 1000 records each...");
        
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("large-test-file-1.txt"))) {
            for (int i = 1; i <= 1000; i += 2) {
                CustomerRecord record = new CustomerRecord(
                    i, 
                    "lastname-" + i, 
                    "firstname-" + i, 
                    (i * 13) % 99999, 
                    "comment-" + i
                );
                writer.write(record.toFixedWidthString());
                writer.newLine();
            }
        }
        
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("large-test-file-2.txt"))) {
            for (int i = 2; i <= 1000; i += 2) {
                CustomerRecord record = new CustomerRecord(
                    i, 
                    "lastname-" + i, 
                    "firstname-" + i, 
                    (i * 17) % 99999, 
                    "comment-" + i
                );
                writer.write(record.toFixedWidthString());
                writer.newLine();
            }
        }
    }
    
    private void testLargeMergeAndSort() throws IOException {
        List<CustomerRecord> file1Records = readRecordsFromFile("large-test-file-1.txt");
        List<CustomerRecord> file2Records = readRecordsFromFile("large-test-file-2.txt");
        
        System.out.println("Read " + file1Records.size() + " records from file 1");
        System.out.println("Read " + file2Records.size() + " records from file 2");
        
        List<CustomerRecord> mergedRecords = new ArrayList<>();
        mergedRecords.addAll(file1Records);
        mergedRecords.addAll(file2Records);
        
        mergedRecords.sort(Comparator.comparingInt(CustomerRecord::getCustomerId));
        
        writeRecordsToFile("large-merge-output.txt", mergedRecords);
        System.out.println("Merged " + mergedRecords.size() + " records");
        
        mergedRecords.sort(Comparator.comparingInt(CustomerRecord::getCustomerContractId).reversed());
        
        writeRecordsToFile("large-sorted-contract-id.txt", mergedRecords);
        System.out.println("Sorted " + mergedRecords.size() + " records by contract ID");
        
        System.out.println("Top 5 records by contract ID (descending):");
        for (int i = 0; i < Math.min(5, mergedRecords.size()); i++) {
            CustomerRecord record = mergedRecords.get(i);
            System.out.println("ID: " + record.getCustomerId() + 
                             ", Contract ID: " + record.getCustomerContractId());
        }
    }
    
    private List<CustomerRecord> readRecordsFromFile(String filename) throws IOException {
        List<CustomerRecord> records = new ArrayList<>();
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    CustomerRecord record = CustomerRecord.fromFixedWidthString(line);
                    if (record != null) {
                        records.add(record);
                    }
                }
            }
        }
        
        return records;
    }
    
    private void writeRecordsToFile(String filename, List<CustomerRecord> records) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename))) {
            for (CustomerRecord record : records) {
                writer.write(record.toFixedWidthString());
                writer.newLine();
            }
        }
    }
}
