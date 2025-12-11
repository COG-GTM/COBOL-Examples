/**
 * MergeSortExample.java
 * 
 * Author: Migrated from COBOL by Devin
 * Original COBOL Author: Erik Eriksen
 * Original Date: 2021-09-19
 * 
 * Purpose: Testing sort and merge functionality on test data.
 *          This is a Java migration of the COBOL merge_sort_test.cbl program.
 * 
 * The program demonstrates:
 * - Creating test data files with customer records
 * - Merging two files while sorting by customer ID (ascending)
 * - Sorting the merged file by contract ID (descending)
 */

import java.io.*;
import java.util.*;

public class MergeSortExample {

    private static final String TEST_FILE_1 = "test-file-1.txt";
    private static final String TEST_FILE_2 = "test-file-2.txt";
    private static final String MERGED_FILE = "merge-output.txt";
    private static final String SORTED_CONTRACT_ID_FILE = "sorted-contract-id.txt";

    static class CustomerRecord {
        private int customerId;
        private String customerLastName;
        private String customerFirstName;
        private int customerContractId;
        private String customerComment;

        public CustomerRecord(int customerId, String customerLastName, String customerFirstName,
                              int customerContractId, String customerComment) {
            this.customerId = customerId;
            this.customerLastName = customerLastName;
            this.customerFirstName = customerFirstName;
            this.customerContractId = customerContractId;
            this.customerComment = customerComment;
        }

        public int getCustomerId() {
            return customerId;
        }

        public int getCustomerContractId() {
            return customerContractId;
        }

        public String toFileFormat() {
            return String.format("%05d%-50s%-50s%05d%-25s",
                    customerId,
                    padRight(customerLastName, 50),
                    padRight(customerFirstName, 50),
                    customerContractId,
                    padRight(customerComment, 25));
        }

        @Override
        public String toString() {
            return String.format("%05d%-50s%-50s%05d%-25s",
                    customerId,
                    padRight(customerLastName, 50),
                    padRight(customerFirstName, 50),
                    customerContractId,
                    padRight(customerComment, 25));
        }

        public static CustomerRecord fromFileFormat(String line) {
            if (line.length() < 135) {
                line = padRight(line, 135);
            }
            int customerId = Integer.parseInt(line.substring(0, 5).trim());
            String lastName = line.substring(5, 55).trim();
            String firstName = line.substring(55, 105).trim();
            int contractId = Integer.parseInt(line.substring(105, 110).trim());
            String comment = line.substring(110, 135).trim();
            return new CustomerRecord(customerId, lastName, firstName, contractId, comment);
        }

        private static String padRight(String s, int length) {
            if (s == null) {
                s = "";
            }
            if (s.length() >= length) {
                return s.substring(0, length);
            }
            StringBuilder sb = new StringBuilder(s);
            while (sb.length() < length) {
                sb.append(' ');
            }
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        MergeSortExample program = new MergeSortExample();

        program.createTestData();
        program.mergeAndDisplayFiles();
        program.sortAndDisplayFile();

        System.out.println("Done.");
    }

    private void createTestData() {
        System.out.println("Creating test data files...");

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

        writeRecordsToFile(TEST_FILE_1, eastRecords);
        writeRecordsToFile(TEST_FILE_2, westRecords);
    }

    private void writeRecordsToFile(String filename, List<CustomerRecord> records) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (CustomerRecord record : records) {
                writer.println(record.toFileFormat());
            }
        } catch (IOException e) {
            System.err.println("Failed to open file for output: " + filename);
            System.exit(1);
        }
    }

    private List<CustomerRecord> readRecordsFromFile(String filename) {
        List<CustomerRecord> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(CustomerRecord.fromFileFormat(line));
            }
        } catch (IOException e) {
            System.err.println("Error opening file: " + filename);
            System.exit(1);
        }
        return records;
    }

    private void mergeAndDisplayFiles() {
        System.out.println("Merging and sorting files...");

        List<CustomerRecord> file1Records = readRecordsFromFile(TEST_FILE_1);
        List<CustomerRecord> file2Records = readRecordsFromFile(TEST_FILE_2);

        List<CustomerRecord> allRecords = new ArrayList<>();
        allRecords.addAll(file1Records);
        allRecords.addAll(file2Records);

        Collections.sort(allRecords, Comparator.comparingInt(CustomerRecord::getCustomerId));

        writeRecordsToFile(MERGED_FILE, allRecords);

        List<CustomerRecord> mergedRecords = readRecordsFromFile(MERGED_FILE);
        for (CustomerRecord record : mergedRecords) {
            System.out.println(record);
        }
    }

    private void sortAndDisplayFile() {
        System.out.println("Sorting merged file on descending contract id....");

        List<CustomerRecord> records = readRecordsFromFile(MERGED_FILE);

        Collections.sort(records, Comparator.comparingInt(CustomerRecord::getCustomerContractId).reversed());

        writeRecordsToFile(SORTED_CONTRACT_ID_FILE, records);

        List<CustomerRecord> sortedRecords = readRecordsFromFile(SORTED_CONTRACT_ID_FILE);
        for (CustomerRecord record : sortedRecords) {
            System.out.println(record);
        }
    }
}
