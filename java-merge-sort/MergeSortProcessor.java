import java.io.*;
import java.util.*;

public class MergeSortProcessor {
    
    public static void main(String[] args) {
        try {
            MergeSortProcessor processor = new MergeSortProcessor();
            processor.run();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void run() throws IOException {
        createTestData();
        mergeAndDisplayFiles();
        sortAndDisplayFile();
        System.out.println("Done.");
    }
    
    private void createTestData() throws IOException {
        System.out.println("Creating test data files...");
        
        List<CustomerRecord> testFile1Records = Arrays.asList(
            new CustomerRecord("1", "last-1", "first-1", "5423", "comment-1"),
            new CustomerRecord("5", "last-5", "first-5", "12323", "comment-5"),
            new CustomerRecord("10", "last-10", "first-10", "653", "comment-10"),
            new CustomerRecord("50", "last-50", "first-50", "5050", "comment-50"),
            new CustomerRecord("25", "last-25", "first-25", "7725", "comment-25"),
            new CustomerRecord("75", "last-75", "first-75", "1175", "comment-75")
        );
        
        List<CustomerRecord> testFile2Records = Arrays.asList(
            new CustomerRecord("999", "last-999", "first-999", "1610", "comment-99"),
            new CustomerRecord("3", "last-03", "first-03", "3331", "comment-03"),
            new CustomerRecord("30", "last-30", "first-30", "8765", "comment-30"),
            new CustomerRecord("85", "last-85", "first-85", "4567", "comment-85"),
            new CustomerRecord("24", "last-24", "first-24", "247", "comment-24")
        );
        
        FileWriter.writeCustomerRecords(testFile1Records, "test-file-1.txt");
        FileWriter.writeCustomerRecords(testFile2Records, "test-file-2.txt");
    }
    
    private void mergeAndDisplayFiles() throws IOException {
        System.out.println("Merging and sorting files...");
        
        List<CustomerRecord> file1Records = FileReader.readCustomerRecords("test-file-1.txt");
        List<CustomerRecord> file2Records = FileReader.readCustomerRecords("test-file-2.txt");
        
        List<CustomerRecord> mergedRecords = new ArrayList<>();
        mergedRecords.addAll(file1Records);
        mergedRecords.addAll(file2Records);
        
        mergedRecords.sort(Comparator.comparingInt(CustomerRecord::getCustomerIDAsInt));
        
        FileWriter.writeCustomerRecords(mergedRecords, "merge-output.txt");
        
        for (CustomerRecord record : mergedRecords) {
            System.out.println(record.toFixedWidthString());
        }
    }
    
    private void sortAndDisplayFile() throws IOException {
        System.out.println("Sorting merged file on descending contract id....");
        
        List<CustomerRecord> mergedRecords = FileReader.readCustomerRecords("merge-output.txt");
        
        mergedRecords.sort((r1, r2) -> Integer.compare(r2.getContractIDAsInt(), r1.getContractIDAsInt()));
        
        FileWriter.writeCustomerRecords(mergedRecords, "sorted-contract-id.txt");
        
        for (CustomerRecord record : mergedRecords) {
            System.out.println(record.toFixedWidthString());
        }
    }
}
