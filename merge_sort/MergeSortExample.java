import java.io.*;
import java.nio.file.*;
import java.util.*;

public class MergeSortExample {

    static class CustomerRecord {
        int customerId;
        String lastName;
        String firstName;
        int contractId;
        String comment;

        public CustomerRecord(int customerId, String lastName, String firstName, 
                            int contractId, String comment) {
            this.customerId = customerId;
            this.lastName = lastName;
            this.firstName = firstName;
            this.contractId = contractId;
            this.comment = comment;
        }

        public String toFixedWidthString() {
            return String.format("%05d%-50s%-50s%05d%-25s",
                customerId, lastName, firstName, contractId, comment);
        }

        public static CustomerRecord fromFixedWidthString(String line) {
            if (line.length() < 135) {
                line = String.format("%-135s", line);
            }
            
            int customerId = Integer.parseInt(line.substring(0, 5));
            String lastName = line.substring(5, 55);
            String firstName = line.substring(55, 105);
            int contractId = Integer.parseInt(line.substring(105, 110));
            String comment = line.substring(110, 135);
            
            return new CustomerRecord(customerId, lastName, firstName, contractId, comment);
        }

        @Override
        public String toString() {
            return toFixedWidthString();
        }
    }

    private static void createTestData() {
        System.out.println("Creating test data files...");

        try (BufferedWriter writer1 = new BufferedWriter(new FileWriter("test-file-1.txt"))) {
            writer1.write(new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1").toFixedWidthString());
            writer1.newLine();
            writer1.write(new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5").toFixedWidthString());
            writer1.newLine();
            writer1.write(new CustomerRecord(10, "last-10", "first-10", 653, "comment-10").toFixedWidthString());
            writer1.newLine();
            writer1.write(new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50").toFixedWidthString());
            writer1.newLine();
            writer1.write(new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25").toFixedWidthString());
            writer1.newLine();
            writer1.write(new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75").toFixedWidthString());
            writer1.newLine();
        } catch (IOException e) {
            System.out.println("Failed to open file for output: " + e.getMessage());
            System.exit(1);
        }

        try (BufferedWriter writer2 = new BufferedWriter(new FileWriter("test-file-2.txt"))) {
            writer2.write(new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99").toFixedWidthString());
            writer2.newLine();
            writer2.write(new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03").toFixedWidthString());
            writer2.newLine();
            writer2.write(new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30").toFixedWidthString());
            writer2.newLine();
            writer2.write(new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85").toFixedWidthString());
            writer2.newLine();
            writer2.write(new CustomerRecord(24, "last-24", "first-24", 247, "comment-24").toFixedWidthString());
            writer2.newLine();
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
                allRecords.add(CustomerRecord.fromFixedWidthString(line));
            }
        } catch (IOException e) {
            System.out.println("Error opening test file 1: " + e.getMessage());
            System.exit(1);
        }

        try (BufferedReader reader2 = new BufferedReader(new FileReader("test-file-2.txt"))) {
            String line;
            while ((line = reader2.readLine()) != null) {
                allRecords.add(CustomerRecord.fromFixedWidthString(line));
            }
        } catch (IOException e) {
            System.out.println("Error opening test file 2: " + e.getMessage());
            System.exit(1);
        }

        allRecords.sort(Comparator.comparingInt(r -> r.customerId));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("merge-output.txt"))) {
            for (CustomerRecord record : allRecords) {
                writer.write(record.toFixedWidthString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing merged file: " + e.getMessage());
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
                records.add(CustomerRecord.fromFixedWidthString(line));
            }
        } catch (IOException e) {
            System.out.println("Error opening merged file: " + e.getMessage());
            System.exit(1);
        }

        records.sort((r1, r2) -> Integer.compare(r2.contractId, r1.contractId));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("sorted-contract-id.txt"))) {
            for (CustomerRecord record : records) {
                writer.write(record.toFixedWidthString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing sorted file: " + e.getMessage());
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
