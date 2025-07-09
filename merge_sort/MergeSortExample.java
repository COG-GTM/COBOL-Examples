import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class MergeSortExample {
    
    public static class CustomerRecord {
        private int customerId;
        private String customerLastName;
        private String customerFirstName;
        private int customerContractId;
        private String customerComment;
        
        public CustomerRecord() {}
        
        public CustomerRecord(int customerId, String customerLastName, String customerFirstName, 
                            int customerContractId, String customerComment) {
            this.customerId = customerId;
            this.customerLastName = customerLastName;
            this.customerFirstName = customerFirstName;
            this.customerContractId = customerContractId;
            this.customerComment = customerComment;
        }
        
        public int getCustomerId() { return customerId; }
        public void setCustomerId(int customerId) { this.customerId = customerId; }
        
        public String getCustomerLastName() { return customerLastName; }
        public void setCustomerLastName(String customerLastName) { this.customerLastName = customerLastName; }
        
        public String getCustomerFirstName() { return customerFirstName; }
        public void setCustomerFirstName(String customerFirstName) { this.customerFirstName = customerFirstName; }
        
        public int getCustomerContractId() { return customerContractId; }
        public void setCustomerContractId(int customerContractId) { this.customerContractId = customerContractId; }
        
        public String getCustomerComment() { return customerComment; }
        public void setCustomerComment(String customerComment) { this.customerComment = customerComment; }
        
        public String toFileString() {
            return String.format("%05d%-50s%-50s%05d%-25s", 
                customerId, 
                padRight(customerLastName, 50),
                padRight(customerFirstName, 50),
                customerContractId,
                padRight(customerComment, 25));
        }
        
        public static CustomerRecord fromFileString(String line) {
            if (line.length() < 135) {
                throw new IllegalArgumentException("Invalid record format");
            }
            
            CustomerRecord record = new CustomerRecord();
            record.customerId = Integer.parseInt(line.substring(0, 5));
            record.customerLastName = line.substring(5, 55).trim();
            record.customerFirstName = line.substring(55, 105).trim();
            record.customerContractId = Integer.parseInt(line.substring(105, 110));
            record.customerComment = line.substring(110, 135).trim();
            
            return record;
        }
        
        private static String padRight(String str, int length) {
            if (str == null) str = "";
            if (str.length() >= length) return str.substring(0, length);
            return str + " ".repeat(length - str.length());
        }
        
        @Override
        public String toString() {
            return toFileString();
        }
    }
    
    public static void main(String[] args) {
        try {
            MergeSortExample example = new MergeSortExample();
            
            example.createTestData();
            example.mergeAndDisplayFiles();
            example.sortAndDisplayFile();
            
            System.out.println("Done.");
            
        } catch (Exception e) {
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
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (CustomerRecord record : records) {
                writer.println(record.toFileString());
            }
        }
    }
    
    private List<CustomerRecord> readRecordsFromFile(String filename) throws IOException {
        List<CustomerRecord> records = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    records.add(CustomerRecord.fromFileString(line));
                }
            }
        }
        
        return records;
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
            System.out.println(record.toString());
        }
    }
    
    private void sortAndDisplayFile() throws IOException {
        System.out.println("Sorting merged file on descending contract id....");
        
        List<CustomerRecord> mergedRecords = readRecordsFromFile("merge-output.txt");
        
        mergedRecords.sort(Comparator.comparingInt(CustomerRecord::getCustomerContractId).reversed());
        
        writeRecordsToFile("sorted-contract-id.txt", mergedRecords);
        
        for (CustomerRecord record : mergedRecords) {
            System.out.println(record.toString());
        }
    }
}
