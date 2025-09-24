import java.io.*;
import java.nio.file.*;
import java.util.*;

public class MergeSortExample {
    
    static class CustomerRecord {
        private int customerId;
        private String lastName;
        private String firstName;
        private int contractId;
        private String comment;
        
        public CustomerRecord(int customerId, String lastName, String firstName, int contractId, String comment) {
            this.customerId = customerId;
            this.lastName = lastName;
            this.firstName = firstName;
            this.contractId = contractId;
            this.comment = comment;
        }
        
        public int getCustomerId() { return customerId; }
        public String getLastName() { return lastName; }
        public String getFirstName() { return firstName; }
        public int getContractId() { return contractId; }
        public String getComment() { return comment; }
        
        @Override
        public String toString() {
            return String.format("%05d%-50s%-50s%05d%-25s", 
                customerId, 
                padRight(lastName, 50),
                padRight(firstName, 50),
                contractId,
                padRight(comment, 25));
        }
        
        public static CustomerRecord fromString(String line) {
            if (line.length() < 135) {
                throw new IllegalArgumentException("Invalid record length");
            }
            int customerId = Integer.parseInt(line.substring(0, 5));
            String lastName = line.substring(5, 55).trim();
            String firstName = line.substring(55, 105).trim();
            int contractId = Integer.parseInt(line.substring(105, 110));
            String comment = line.substring(110, 135).trim();
            return new CustomerRecord(customerId, lastName, firstName, contractId, comment);
        }
    }
    
    private static String padRight(String str, int length) {
        return String.format("%-" + length + "s", str).substring(0, length);
    }
    
    public static void main(String[] args) {
        try {
            createTestData();
            mergeAndDisplayFiles();
            sortAndDisplayFile();
            System.out.println("Done.");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private static void createTestData() throws IOException {
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
    
    private static void mergeAndDisplayFiles() throws IOException {
        System.out.println("Merging and sorting files...");
        
        List<CustomerRecord> allRecords = new ArrayList<>();
        allRecords.addAll(readRecordsFromFile("test-file-1.txt"));
        allRecords.addAll(readRecordsFromFile("test-file-2.txt"));
        
        allRecords.sort(Comparator.comparingInt(CustomerRecord::getCustomerId));
        
        writeRecordsToFile("merge-output.txt", allRecords);
        
        for (CustomerRecord record : allRecords) {
            System.out.println(record);
        }
    }
    
    private static void sortAndDisplayFile() throws IOException {
        System.out.println("Sorting merged file on descending contract id....");
        
        List<CustomerRecord> records = readRecordsFromFile("merge-output.txt");
        
        records.sort(Comparator.comparingInt(CustomerRecord::getContractId).reversed());
        
        writeRecordsToFile("sorted-contract-id.txt", records);
        
        for (CustomerRecord record : records) {
            System.out.println(record);
        }
    }
    
    private static void writeRecordsToFile(String filename, List<CustomerRecord> records) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (CustomerRecord record : records) {
                writer.println(record.toString());
            }
        }
    }
    
    private static List<CustomerRecord> readRecordsFromFile(String filename) throws IOException {
        List<CustomerRecord> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
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
