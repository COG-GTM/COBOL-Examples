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
                throw new IllegalArgumentException("Invalid record format");
            }
            
            int customerId = Integer.parseInt(line.substring(0, 5));
            String lastName = line.substring(5, 55).trim();
            String firstName = line.substring(55, 105).trim();
            int contractId = Integer.parseInt(line.substring(105, 110));
            String comment = line.substring(110, 135).trim();
            
            return new CustomerRecord(customerId, lastName, firstName, contractId, comment);
        }
        
        private static String padRight(String str, int length) {
            if (str.length() >= length) {
                return str.substring(0, length);
            }
            return str + " ".repeat(length - str.length());
        }
    }
    
    private static void createTestData() {
        System.out.println("Creating test data files...");
        
        try {
            List<CustomerRecord> file1Records = Arrays.asList(
                new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"),
                new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"),
                new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"),
                new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"),
                new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"),
                new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75")
            );
            
            try (PrintWriter writer = new PrintWriter(new FileWriter("test-file-1.txt"))) {
                for (CustomerRecord record : file1Records) {
                    writer.println(record.toString());
                }
            }
            
            List<CustomerRecord> file2Records = Arrays.asList(
                new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"),
                new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"),
                new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"),
                new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"),
                new CustomerRecord(24, "last-24", "first-24", 247, "comment-24")
            );
            
            try (PrintWriter writer = new PrintWriter(new FileWriter("test-file-2.txt"))) {
                for (CustomerRecord record : file2Records) {
                    writer.println(record.toString());
                }
            }
            
        } catch (IOException e) {
            System.err.println("Failed to open file for output: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private static void mergeAndDisplayFiles() {
        System.out.println("Merging and sorting files...");
        
        try {
            List<CustomerRecord> allRecords = new ArrayList<>();
            
            try (BufferedReader reader = new BufferedReader(new FileReader("test-file-1.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    allRecords.add(CustomerRecord.fromString(line));
                }
            }
            
            try (BufferedReader reader = new BufferedReader(new FileReader("test-file-2.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    allRecords.add(CustomerRecord.fromString(line));
                }
            }
            
            allRecords.sort(Comparator.comparingInt(CustomerRecord::getCustomerId));
            
            try (PrintWriter writer = new PrintWriter(new FileWriter("merge-output.txt"))) {
                for (CustomerRecord record : allRecords) {
                    writer.println(record.toString());
                }
            }
            
            try (BufferedReader reader = new BufferedReader(new FileReader("merge-output.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error opening merged output file: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private static void sortAndDisplayFile() {
        System.out.println("Sorting merged file on descending contract id....");
        
        try {
            List<CustomerRecord> records = new ArrayList<>();
            
            try (BufferedReader reader = new BufferedReader(new FileReader("merge-output.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    records.add(CustomerRecord.fromString(line));
                }
            }
            
            records.sort(Comparator.comparingInt(CustomerRecord::getContractId).reversed());
            
            try (PrintWriter writer = new PrintWriter(new FileWriter("sorted-contract-id.txt"))) {
                for (CustomerRecord record : records) {
                    writer.println(record.toString());
                }
            }
            
            try (BufferedReader reader = new BufferedReader(new FileReader("sorted-contract-id.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error opening sorted output file: " + e.getMessage());
            System.exit(1);
        }
    }
    
    public static void main(String[] args) {
        createTestData();
        mergeAndDisplayFiles();
        sortAndDisplayFile();
        System.out.println("Done.");
    }
}
