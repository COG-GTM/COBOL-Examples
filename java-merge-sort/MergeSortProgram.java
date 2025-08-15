import java.io.*;
import java.util.*;

public class MergeSortProgram {
    
    public static void main(String[] args) {
        try {
            System.out.println("Creating test data files...");
            createTestData();
            
            System.out.println("Merging and sorting files...");
            mergeAndDisplayFiles();
            
            System.out.println("Sorting merged file on descending contract id....");
            sortAndDisplayFile();
            
            System.out.println("Done.");
            
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createTestData() throws IOException {
        createTestFile1();
        createTestFile2();
    }
    
    private static void createTestFile1() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("test-file-1.txt"))) {
            CustomerRecord[] records = {
                new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"),
                new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"),
                new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"),
                new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"),
                new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"),
                new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75")
            };
            
            for (CustomerRecord record : records) {
                writer.write(record.toFixedWidthString());
                writer.newLine();
            }
        }
    }
    
    private static void createTestFile2() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("test-file-2.txt"))) {
            CustomerRecord[] records = {
                new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"),
                new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"),
                new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"),
                new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"),
                new CustomerRecord(24, "last-24", "first-24", 247, "comment-24")
            };
            
            for (CustomerRecord record : records) {
                writer.write(record.toFixedWidthString());
                writer.newLine();
            }
        }
    }
    
    private static void mergeAndDisplayFiles() throws IOException {
        List<CustomerRecord> allRecords = new ArrayList<>();
        
        try (BufferedReader reader1 = new BufferedReader(new FileReader("test-file-1.txt"))) {
            String line;
            while ((line = reader1.readLine()) != null) {
                CustomerRecord record = CustomerRecord.fromFixedWidthString(line);
                if (record != null) {
                    allRecords.add(record);
                }
            }
        }
        
        try (BufferedReader reader2 = new BufferedReader(new FileReader("test-file-2.txt"))) {
            String line;
            while ((line = reader2.readLine()) != null) {
                CustomerRecord record = CustomerRecord.fromFixedWidthString(line);
                if (record != null) {
                    allRecords.add(record);
                }
            }
        }
        
        allRecords.sort(Comparator.comparingInt(CustomerRecord::getCustomerId));
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("merge-output.txt"))) {
            for (CustomerRecord record : allRecords) {
                System.out.println(record.toFixedWidthString());
                writer.write(record.toFixedWidthString());
                writer.newLine();
            }
        }
    }
    
    private static void sortAndDisplayFile() throws IOException {
        List<CustomerRecord> records = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader("merge-output.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                CustomerRecord record = CustomerRecord.fromFixedWidthString(line);
                if (record != null) {
                    records.add(record);
                }
            }
        }
        
        records.sort(Comparator.comparingInt(CustomerRecord::getCustomerContractId).reversed());
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("sorted-contract-id.txt"))) {
            for (CustomerRecord record : records) {
                System.out.println(record.toFixedWidthString());
                writer.write(record.toFixedWidthString());
                writer.newLine();
            }
        }
    }
}
