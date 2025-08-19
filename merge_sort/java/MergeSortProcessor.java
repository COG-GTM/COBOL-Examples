import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MergeSortProcessor {
    
    public static void main(String[] args) {
        createTestData();
        mergeAndDisplayFiles();
        sortAndDisplayFile();
        System.out.println("Done.");
    }
    
    private static void createTestData() {
        System.out.println("Creating test data files...");
        
        List<CustomerRecord> testFile1Records = new ArrayList<>();
        testFile1Records.add(new CustomerRecord("1", "last-1", "first-1", "5423", "comment-1"));
        testFile1Records.add(new CustomerRecord("5", "last-5", "first-5", "12323", "comment-5"));
        testFile1Records.add(new CustomerRecord("10", "last-10", "first-10", "653", "comment-10"));
        testFile1Records.add(new CustomerRecord("50", "last-50", "first-50", "5050", "comment-50"));
        testFile1Records.add(new CustomerRecord("25", "last-25", "first-25", "7725", "comment-25"));
        testFile1Records.add(new CustomerRecord("75", "last-75", "first-75", "1175", "comment-75"));
        
        List<CustomerRecord> testFile2Records = new ArrayList<>();
        testFile2Records.add(new CustomerRecord("999", "last-999", "first-999", "1610", "comment-99"));
        testFile2Records.add(new CustomerRecord("3", "last-03", "first-03", "3331", "comment-03"));
        testFile2Records.add(new CustomerRecord("30", "last-30", "first-30", "8765", "comment-30"));
        testFile2Records.add(new CustomerRecord("85", "last-85", "first-85", "4567", "comment-85"));
        testFile2Records.add(new CustomerRecord("24", "last-24", "first-24", "247", "comment-24"));
        
        FileWriter.writeCustomerRecords(testFile1Records, "test-file-1.txt");
        FileWriter.writeCustomerRecords(testFile2Records, "test-file-2.txt");
    }
    
    private static void mergeAndDisplayFiles() {
        System.out.println("Merging and sorting files...");
        
        List<CustomerRecord> file1Records = FileReader.readCustomerRecords("test-file-1.txt");
        List<CustomerRecord> file2Records = FileReader.readCustomerRecords("test-file-2.txt");
        
        if (file1Records.isEmpty()) {
            System.err.println("Error opening test-file-1.txt");
            return;
        }
        
        if (file2Records.isEmpty()) {
            System.err.println("Error opening test-file-2.txt");
            return;
        }
        
        List<CustomerRecord> mergedRecords = new ArrayList<>();
        mergedRecords.addAll(file1Records);
        mergedRecords.addAll(file2Records);
        
        Collections.sort(mergedRecords, Comparator.comparing(CustomerRecord::getCustomerIDAsInt));
        
        FileWriter.writeCustomerRecords(mergedRecords, "merge-output.txt");
        
        for (CustomerRecord record : mergedRecords) {
            System.out.println(record.toFixedWidthString());
        }
    }
    
    private static void sortAndDisplayFile() {
        System.out.println("Sorting merged file on descending contract id....");
        
        List<CustomerRecord> mergedRecords = FileReader.readCustomerRecords("merge-output.txt");
        
        if (mergedRecords.isEmpty()) {
            System.err.println("Error opening merge-output.txt");
            return;
        }
        
        Collections.sort(mergedRecords, Comparator.comparing(CustomerRecord::getContractIDAsInt).reversed());
        
        FileWriter.writeCustomerRecords(mergedRecords, "sorted-contract-id.txt");
        
        for (CustomerRecord record : mergedRecords) {
            System.out.println(record.toFixedWidthString());
        }
    }
}
