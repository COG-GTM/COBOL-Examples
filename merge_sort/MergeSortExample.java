import java.io.*;
import java.util.*;

public class MergeSortExample {
    
    public static void main(String[] args) {
        MergeSortExample example = new MergeSortExample();
        
        example.createTestData();
        example.mergeAndDisplayFiles();
        example.sortAndDisplayFile();
        
        System.out.println("Done.");
    }
    
    public void createTestData() {
        System.out.println("Creating test data files...");
        
        List<CustomerRecord> file1Records = Arrays.asList(
            new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"),
            new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"),
            new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"),
            new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"),
            new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"),
            new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75")
        );
        
        List<CustomerRecord> file2Records = Arrays.asList(
            new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"),
            new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"),
            new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"),
            new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"),
            new CustomerRecord(24, "last-24", "first-24", 247, "comment-24")
        );
        
        writeRecordsToFile("test-file-1.txt", file1Records);
        writeRecordsToFile("test-file-2.txt", file2Records);
    }
    
    public void mergeAndDisplayFiles() {
        System.out.println("Merging and sorting files...");
        
        List<CustomerRecord> file1Records = readRecordsFromFile("test-file-1.txt");
        List<CustomerRecord> file2Records = readRecordsFromFile("test-file-2.txt");
        
        List<CustomerRecord> allRecords = new ArrayList<>();
        allRecords.addAll(file1Records);
        allRecords.addAll(file2Records);
        
        allRecords.sort(Comparator.comparingInt(CustomerRecord::getCustomerId));
        
        for (CustomerRecord record : allRecords) {
            System.out.println(record.toDisplayString());
        }
        
        writeRecordsToFile("merge-output.txt", allRecords);
    }
    
    public void sortAndDisplayFile() {
        System.out.println("Sorting merged file on descending contract id....");
        
        List<CustomerRecord> records = readRecordsFromFile("merge-output.txt");
        
        records.sort(Comparator.comparingInt(CustomerRecord::getContractId).reversed());
        
        for (CustomerRecord record : records) {
            System.out.println(record.toDisplayString());
        }
        
        writeRecordsToFile("sorted-contract-id.txt", records);
    }
    
    private void writeRecordsToFile(String filename, List<CustomerRecord> records) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (CustomerRecord record : records) {
                writer.println(record.toFixedWidthString());
            }
        } catch (IOException e) {
            System.err.println("Error writing to file " + filename + ": " + e.getMessage());
        }
    }
    
    private List<CustomerRecord> readRecordsFromFile(String filename) {
        List<CustomerRecord> records = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    CustomerRecord record = CustomerRecord.fromFixedWidthString(line);
                    if (record != null) {
                        records.add(record);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading from file " + filename + ": " + e.getMessage());
        }
        
        return records;
    }
}
