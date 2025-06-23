import java.io.*;
import java.util.*;

public class MergeSortExample {
    
    static class CustomerRecord {
        int customerId;
        String lastName;
        String firstName;
        int contractId;
        String comment;
        
        public CustomerRecord(int customerId, String lastName, String firstName, int contractId, String comment) {
            this.customerId = customerId;
            this.lastName = lastName;
            this.firstName = firstName;
            this.contractId = contractId;
            this.comment = comment;
        }
        
        public String toFileString() {
            return String.format("%05d%-50s%-50s%05d%-25s",
                customerId,
                padRight(lastName, 50),
                padRight(firstName, 50),
                contractId,
                padRight(comment, 25));
        }
        
        public String toDisplayString() {
            return String.format("%05d%-50s%-50s%05d%-25s",
                customerId,
                padRight(lastName, 50),
                padRight(firstName, 50),
                contractId,
                padRight(comment, 25));
        }
        
        private static String padRight(String str, int length) {
            if (str.length() >= length) {
                return str.substring(0, length);
            }
            StringBuilder sb = new StringBuilder(str);
            while (sb.length() < length) {
                sb.append(' ');
            }
            return sb.toString();
        }
        
        public static CustomerRecord fromFileString(String line) {
            if (line.length() < 135) {
                line = padRight(line, 135);
            }
            
            int customerId = Integer.parseInt(line.substring(0, 5));
            String lastName = line.substring(5, 55).trim();
            String firstName = line.substring(55, 105).trim();
            int contractId = Integer.parseInt(line.substring(105, 110));
            String comment = line.substring(110, 135).trim();
            
            return new CustomerRecord(customerId, lastName, firstName, contractId, comment);
        }
    }
    
    static class CustomerIdComparator implements Comparator<CustomerRecord> {
        @Override
        public int compare(CustomerRecord a, CustomerRecord b) {
            return Integer.compare(a.customerId, b.customerId);
        }
    }
    
    static class ContractIdComparator implements Comparator<CustomerRecord> {
        @Override
        public int compare(CustomerRecord a, CustomerRecord b) {
            return Integer.compare(b.contractId, a.contractId);
        }
    }
    
    public static void main(String[] args) {
        createTestData();
        mergeAndDisplayFiles();
        sortAndDisplayFile();
        System.out.println("Done.");
    }
    
    private static void createTestData() {
        System.out.println("Creating test data files...");
        
        try (PrintWriter writer1 = new PrintWriter(new FileWriter("test-file-1.txt"))) {
            writer1.println(new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1").toFileString());
            writer1.println(new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5").toFileString());
            writer1.println(new CustomerRecord(10, "last-10", "first-10", 653, "comment-10").toFileString());
            writer1.println(new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50").toFileString());
            writer1.println(new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25").toFileString());
            writer1.println(new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75").toFileString());
        } catch (IOException e) {
            System.out.println("Failed to open file for output: " + e.getMessage());
            System.exit(1);
        }
        
        try (PrintWriter writer2 = new PrintWriter(new FileWriter("test-file-2.txt"))) {
            writer2.println(new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99").toFileString());
            writer2.println(new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03").toFileString());
            writer2.println(new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30").toFileString());
            writer2.println(new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85").toFileString());
            writer2.println(new CustomerRecord(24, "last-24", "first-24", 247, "comment-24").toFileString());
        } catch (IOException e) {
            System.out.println("Failed to open file for output: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private static void mergeAndDisplayFiles() {
        System.out.println("Merging and sorting files...");
        
        List<CustomerRecord> allRecords = new ArrayList<>();
        
        try (BufferedReader reader1 = new BufferedReader(new FileReader("test-file-1.txt"))) {
            String line;
            while ((line = reader1.readLine()) != null) {
                allRecords.add(CustomerRecord.fromFileString(line));
            }
        } catch (IOException e) {
            System.out.println("Error opening test file 1: " + e.getMessage());
            System.exit(1);
        }
        
        try (BufferedReader reader2 = new BufferedReader(new FileReader("test-file-2.txt"))) {
            String line;
            while ((line = reader2.readLine()) != null) {
                allRecords.add(CustomerRecord.fromFileString(line));
            }
        } catch (IOException e) {
            System.out.println("Error opening test file 2: " + e.getMessage());
            System.exit(1);
        }
        
        Collections.sort(allRecords, new CustomerIdComparator());
        
        try (PrintWriter writer = new PrintWriter(new FileWriter("merge-output.txt"))) {
            for (CustomerRecord record : allRecords) {
                writer.println(record.toFileString());
                System.out.println(record.toDisplayString());
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
            System.out.println("Error opening merged output file: " + e.getMessage());
            System.exit(1);
        }
        
        Collections.sort(records, new ContractIdComparator());
        
        try (PrintWriter writer = new PrintWriter(new FileWriter("sorted-contract-id.txt"))) {
            for (CustomerRecord record : records) {
                writer.println(record.toFileString());
                System.out.println(record.toDisplayString());
            }
        } catch (IOException e) {
            System.out.println("Error opening sorted output file: " + e.getMessage());
            System.exit(1);
        }
        
        try (PrintWriter workTemp = new PrintWriter(new FileWriter("work-temp.txt"))) {
            for (CustomerRecord record : records) {
                workTemp.println(record.toFileString());
            }
        } catch (IOException e) {
            System.out.println("Error creating work temp file: " + e.getMessage());
        }
    }
}
