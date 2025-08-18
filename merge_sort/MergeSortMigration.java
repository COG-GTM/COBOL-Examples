import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class MergeSortMigration {
    
    public static void main(String[] args) {
        MergeSortMigration migration = new MergeSortMigration();
        migration.execute();
    }
    
    public void execute() {
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
    
    private void createTestData() throws IOException {
        System.out.println("Creating test data files...");
        
        List<CustomerRecord> eastRecords = Arrays.asList(
            new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"),
            new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"),
            new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"),
            new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"),
            new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"),
            new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75")
        );
        
        writeRecordsToFile("test-file-1.txt", eastRecords);
        
        List<CustomerRecord> westRecords = Arrays.asList(
            new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"),
            new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"),
            new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"),
            new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"),
            new CustomerRecord(24, "last-24", "first-24", 247, "comment-24")
        );
        
        writeRecordsToFile("test-file-2.txt", westRecords);
    }
    
    private void writeRecordsToFile(String filename, List<CustomerRecord> records) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename))) {
            for (CustomerRecord record : records) {
                writer.write(record.toFixedWidthString());
                writer.newLine();
            }
        }
    }
    
    private void mergeAndDisplayFiles() throws IOException {
        System.out.println("Merging and sorting files...");
        
        List<CustomerRecord> file1Records = readRecordsFromFile("test-file-1.txt");
        List<CustomerRecord> file2Records = readRecordsFromFile("test-file-2.txt");
        
        List<CustomerRecord> mergedRecords = new ArrayList<>();
        mergedRecords.addAll(file1Records);
        mergedRecords.addAll(file2Records);
        
        mergedRecords.sort(Comparator.comparingInt(CustomerRecord::getCustomerId));
        
        writeRecordsToFile("merge-output.txt", mergedRecords);
        
        for (CustomerRecord record : mergedRecords) {
            System.out.println(record.toFixedWidthString());
        }
    }
    
    private void sortAndDisplayFile() throws IOException {
        System.out.println("Sorting merged file on descending contract id....");
        
        List<CustomerRecord> records = readRecordsFromFile("merge-output.txt");
        
        records.sort(Comparator.comparingInt(CustomerRecord::getCustomerContractId).reversed());
        
        writeRecordsToFile("sorted-contract-id.txt", records);
        
        for (CustomerRecord record : records) {
            System.out.println(record.toFixedWidthString());
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
}
