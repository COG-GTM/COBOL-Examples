import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MergeSortExample {
    
    private static final String TEST_FILE_1 = "test-file-1.txt";
    private static final String TEST_FILE_2 = "test-file-2.txt";
    private static final String MERGED_FILE = "merge-output.txt";
    private static final String SORTED_FILE = "sorted-contract-id.txt";

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
        System.out.println("Creating test data files...");
        
        List<CustomerRecord> eastRecords = new ArrayList<>();
        eastRecords.add(new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"));
        eastRecords.add(new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"));
        eastRecords.add(new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"));
        eastRecords.add(new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"));
        eastRecords.add(new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"));
        eastRecords.add(new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75"));
        
        writeRecordsToFile(TEST_FILE_1, eastRecords);
        
        List<CustomerRecord> westRecords = new ArrayList<>();
        westRecords.add(new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"));
        westRecords.add(new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"));
        westRecords.add(new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"));
        westRecords.add(new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"));
        westRecords.add(new CustomerRecord(24, "last-24", "first-24", 247, "comment-24"));
        
        writeRecordsToFile(TEST_FILE_2, westRecords);
    }

    private static void mergeAndDisplayFiles() throws IOException {
        System.out.println("Merging and sorting files...");
        
        List<CustomerRecord> file1Records = readRecordsFromFile(TEST_FILE_1);
        List<CustomerRecord> file2Records = readRecordsFromFile(TEST_FILE_2);
        
        List<CustomerRecord> mergedRecords = Stream.concat(
                file1Records.stream(),
                file2Records.stream()
        )
        .sorted(Comparator.comparingInt(CustomerRecord::getCustomerId))
        .collect(Collectors.toList());
        
        writeRecordsToFile(MERGED_FILE, mergedRecords);
        
        for (CustomerRecord record : mergedRecords) {
            System.out.println(record);
        }
    }

    private static void sortAndDisplayFile() throws IOException {
        System.out.println("Sorting merged file on descending contract id....");
        
        List<CustomerRecord> mergedRecords = readRecordsFromFile(MERGED_FILE);
        
        List<CustomerRecord> sortedRecords = mergedRecords.stream()
                .sorted(Comparator.comparingInt(CustomerRecord::getCustomerContractId).reversed())
                .collect(Collectors.toList());
        
        writeRecordsToFile(SORTED_FILE, sortedRecords);
        
        for (CustomerRecord record : sortedRecords) {
            System.out.println(record);
        }
    }

    private static void writeRecordsToFile(String filename, List<CustomerRecord> records) 
            throws IOException {
        Path path = Paths.get(filename);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (CustomerRecord record : records) {
                writer.write(record.toString());
                writer.newLine();
            }
        }
    }

    private static List<CustomerRecord> readRecordsFromFile(String filename) throws IOException {
        Path path = Paths.get(filename);
        List<CustomerRecord> records = new ArrayList<>();
        
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    records.add(CustomerRecord.fromString(line));
                }
            }
        }
        
        return records;
    }
}
