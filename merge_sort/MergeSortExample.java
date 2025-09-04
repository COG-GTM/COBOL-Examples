import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class MergeSortExample {
    
    public static void main(String[] args) {
        System.out.println("Creating test data files...");
        createTestData();
        
        System.out.println("Merging and sorting files...");
        mergeAndDisplayFiles();
        
        System.out.println("Sorting merged file on descending contract id....");
        sortAndDisplayFile();
        
        System.out.println("Done.");
    }
    
    private static void createTestData() {
        List<CustomerRecord> eastRecords = Arrays.asList(
            new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"),
            new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"),
            new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"),
            new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"),
            new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"),
            new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75")
        );
        
        List<CustomerRecord> westRecords = Arrays.asList(
            new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"),
            new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"),
            new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"),
            new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"),
            new CustomerRecord(24, "last-24", "first-24", 247, "comment-24")
        );
        
        try {
            writeRecordsToFile(eastRecords, "test-file-1.txt");
            writeRecordsToFile(westRecords, "test-file-2.txt");
        } catch (IOException e) {
            System.err.println("Error creating test data files: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private static void writeRecordsToFile(List<CustomerRecord> records, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (CustomerRecord record : records) {
                writer.println(record.toString());
            }
        }
    }
    
    private static void mergeAndDisplayFiles() {
        try {
            List<CustomerRecord> file1Records = readRecordsFromFile("test-file-1.txt");
            List<CustomerRecord> file2Records = readRecordsFromFile("test-file-2.txt");
            
            List<CustomerRecord> mergedRecords = new ArrayList<>();
            mergedRecords.addAll(file1Records);
            mergedRecords.addAll(file2Records);
            
            mergedRecords.sort(Comparator.comparing(CustomerRecord::getCustomerId));
            
            writeRecordsToFile(mergedRecords, "merge-output.txt");
            
            for (CustomerRecord record : mergedRecords) {
                System.out.println(record);
            }
            
        } catch (IOException e) {
            System.err.println("Error merging files: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private static void sortAndDisplayFile() {
        try {
            List<CustomerRecord> records = readRecordsFromFile("merge-output.txt");
            
            records.sort(Comparator.comparing(CustomerRecord::getContractId).reversed());
            
            writeRecordsToFile(records, "sorted-contract-id.txt");
            
            for (CustomerRecord record : records) {
                System.out.println(record);
            }
            
        } catch (IOException e) {
            System.err.println("Error sorting file: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private static List<CustomerRecord> readRecordsFromFile(String filename) throws IOException {
        return Files.lines(Paths.get(filename))
                   .map(CustomerRecord::fromString)
                   .collect(Collectors.toList());
    }
}
