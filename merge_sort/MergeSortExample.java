import java.io.*;
import java.util.*;

public class MergeSortExample {
    
    static class CustomerRecord {
        private int customerId;
        private String lastName;
        private String firstName;
        private int contractId;
        private String comment;
        
        public CustomerRecord(int customerId, String lastName, String firstName, 
                            int contractId, String comment) {
            this.customerId = customerId;
            this.lastName = lastName;
            this.firstName = firstName;
            this.contractId = contractId;
            this.comment = comment;
        }
        
        public int getCustomerId() {
            return customerId;
        }
        
        public int getContractId() {
            return contractId;
        }
        
        public String toFileString() {
            return String.format("%05d%-50s%-50s%05d%-25s",
                customerId, lastName, firstName, contractId, comment);
        }
        
        @Override
        public String toString() {
            return toFileString();
        }
        
        public static CustomerRecord fromFileString(String line) {
            if (line.length() < 135) {
                line = String.format("%-135s", line);
            }
            int customerId = Integer.parseInt(line.substring(0, 5).trim());
            String lastName = line.substring(5, 55);
            String firstName = line.substring(55, 105);
            int contractId = Integer.parseInt(line.substring(105, 110).trim());
            String comment = line.substring(110, 135);
            return new CustomerRecord(customerId, lastName, firstName, contractId, comment);
        }
    }
    
    private static void createTestData() {
        System.out.println("Creating test data files...");
        
        try (BufferedWriter writer1 = new BufferedWriter(new FileWriter("test-file-1.txt"))) {
            writer1.write(new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1").toFileString());
            writer1.newLine();
            writer1.write(new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5").toFileString());
            writer1.newLine();
            writer1.write(new CustomerRecord(10, "last-10", "first-10", 653, "comment-10").toFileString());
            writer1.newLine();
            writer1.write(new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50").toFileString());
            writer1.newLine();
            writer1.write(new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25").toFileString());
            writer1.newLine();
            writer1.write(new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75").toFileString());
            writer1.newLine();
        } catch (IOException e) {
            System.out.println("Failed to open file for output: " + e.getMessage());
            System.exit(1);
        }
        
        try (BufferedWriter writer2 = new BufferedWriter(new FileWriter("test-file-2.txt"))) {
            writer2.write(new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99").toFileString());
            writer2.newLine();
            writer2.write(new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03").toFileString());
            writer2.newLine();
            writer2.write(new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30").toFileString());
            writer2.newLine();
            writer2.write(new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85").toFileString());
            writer2.newLine();
            writer2.write(new CustomerRecord(24, "last-24", "first-24", 247, "comment-24").toFileString());
            writer2.newLine();
        } catch (IOException e) {
            System.out.println("Failed to open file for output: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private static void mergeAndDisplayFiles() {
        System.out.println("Merging and sorting files...");
        
        List<CustomerRecord> records = new ArrayList<>();
        
        try (BufferedReader reader1 = new BufferedReader(new FileReader("test-file-1.txt"))) {
            String line;
            while ((line = reader1.readLine()) != null) {
                records.add(CustomerRecord.fromFileString(line));
            }
        } catch (IOException e) {
            System.out.println("Error reading test file 1: " + e.getMessage());
            System.exit(1);
        }
        
        try (BufferedReader reader2 = new BufferedReader(new FileReader("test-file-2.txt"))) {
            String line;
            while ((line = reader2.readLine()) != null) {
                records.add(CustomerRecord.fromFileString(line));
            }
        } catch (IOException e) {
            System.out.println("Error reading test file 2: " + e.getMessage());
            System.exit(1);
        }
        
        Collections.sort(records, Comparator.comparingInt(CustomerRecord::getCustomerId));
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("merge-output.txt"))) {
            for (CustomerRecord record : records) {
                writer.write(record.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing merged output file: " + e.getMessage());
            System.exit(1);
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader("merge-output.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error opening merged output file: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private static void sortAndDisplayFile() {
        System.out.println("Sorting merged file on descending contract id....");
        
        List<CustomerRecord> records = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader("merge-output.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(CustomerRecord.fromFileString(line));
            }
        } catch (IOException e) {
            System.out.println("Error reading merged output file: " + e.getMessage());
            System.exit(1);
        }
        
        Collections.sort(records, Comparator.comparingInt(CustomerRecord::getContractId).reversed());
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("sorted-contract-id.txt"))) {
            for (CustomerRecord record : records) {
                writer.write(record.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing sorted output file: " + e.getMessage());
            System.exit(1);
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader("sorted-contract-id.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error opening sorted output file: " + e.getMessage());
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
