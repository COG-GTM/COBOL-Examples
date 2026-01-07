/**
 * Author: Rebuilt from COBOL by Devin
 * Original COBOL Author: Erik Eriksen
 * Date: 2021-09-19 (original), 2026-01-07 (Java rebuild)
 * Purpose: Testing sort and merge syntax on test data - Java implementation
 *          of the COBOL merge_sort_test.cbl program
 */

import java.io.*;
import java.util.*;

public class MergeSortExample {

    /**
     * CustomerRecord represents a customer record with fixed-width fields
     * matching the COBOL data structure:
     * - customer-id: 5 digits (pic 9(5))
     * - customer-last-name: 50 characters (pic x(50))
     * - customer-first-name: 50 characters (pic x(50))
     * - customer-contract-id: 5 digits (pic 9(5))
     * - customer-comment: 25 characters (pic x(25))
     */
    static class CustomerRecord {
        private int customerId;           // 5 digits
        private String customerLastName;  // 50 chars
        private String customerFirstName; // 50 chars
        private int customerContractId;   // 5 digits
        private String customerComment;   // 25 chars

        public CustomerRecord(int customerId, String customerLastName, 
                              String customerFirstName, int customerContractId, 
                              String customerComment) {
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

        /**
         * Format the record as a fixed-width string matching COBOL output format
         */
        public String toFixedWidthString() {
            return String.format("%05d%-50s%-50s%05d%-25s",
                    customerId,
                    padRight(customerLastName, 50),
                    padRight(customerFirstName, 50),
                    customerContractId,
                    padRight(customerComment, 25));
        }

        /**
         * Parse a fixed-width string into a CustomerRecord
         */
        public static CustomerRecord fromFixedWidthString(String line) {
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

        @Override
        public String toString() {
            return toFixedWidthString();
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

    private static final String TEST_FILE_1 = "test-file-1.txt";
    private static final String TEST_FILE_2 = "test-file-2.txt";
    private static final String MERGED_FILE = "merge-output.txt";
    private static final String SORTED_CONTRACT_ID_FILE = "sorted-contract-id.txt";

    public static void main(String[] args) {
        MergeSortExample example = new MergeSortExample();
        
        example.createTestData();
        example.mergeAndDisplayFiles();
        example.sortAndDisplayFile();
        
        System.out.println("Done.");
    }

    /**
     * Creates test data files matching the COBOL program's test data
     */
    private void createTestData() {
        System.out.println("Creating test data files...");

        // Create test-file-1.txt (East region customers)
        List<CustomerRecord> eastRecords = Arrays.asList(
            new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"),
            new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"),
            new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"),
            new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"),
            new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"),
            new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75")
        );

        try (PrintWriter writer = new PrintWriter(new FileWriter(TEST_FILE_1))) {
            for (CustomerRecord record : eastRecords) {
                writer.println(record.toFixedWidthString());
            }
        } catch (IOException e) {
            System.err.println("Failed to open file for output: " + TEST_FILE_1);
            System.exit(1);
        }

        // Create test-file-2.txt (West region customers)
        List<CustomerRecord> westRecords = Arrays.asList(
            new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"),
            new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"),
            new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"),
            new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"),
            new CustomerRecord(24, "last-24", "first-24", 247, "comment-24")
        );

        try (PrintWriter writer = new PrintWriter(new FileWriter(TEST_FILE_2))) {
            for (CustomerRecord record : westRecords) {
                writer.println(record.toFixedWidthString());
            }
        } catch (IOException e) {
            System.err.println("Failed to open file for output: " + TEST_FILE_2);
            System.exit(1);
        }
    }

    /**
     * Merges both test files, sorts by customer ID ascending,
     * writes to merge-output.txt, and displays all records
     */
    private void mergeAndDisplayFiles() {
        System.out.println("Merging and sorting files...");

        List<CustomerRecord> allRecords = new ArrayList<>();

        // Read from test-file-1.txt
        try (BufferedReader reader = new BufferedReader(new FileReader(TEST_FILE_1))) {
            String line;
            while ((line = reader.readLine()) != null) {
                allRecords.add(CustomerRecord.fromFixedWidthString(line));
            }
        } catch (IOException e) {
            System.err.println("Error opening test file 1: " + e.getMessage());
            System.exit(1);
        }

        // Read from test-file-2.txt
        try (BufferedReader reader = new BufferedReader(new FileReader(TEST_FILE_2))) {
            String line;
            while ((line = reader.readLine()) != null) {
                allRecords.add(CustomerRecord.fromFixedWidthString(line));
            }
        } catch (IOException e) {
            System.err.println("Error opening test file 2: " + e.getMessage());
            System.exit(1);
        }

        // Sort by customer ID ascending (merge sort behavior)
        Collections.sort(allRecords, Comparator.comparingInt(CustomerRecord::getCustomerId));

        // Write to merge-output.txt
        try (PrintWriter writer = new PrintWriter(new FileWriter(MERGED_FILE))) {
            for (CustomerRecord record : allRecords) {
                writer.println(record.toFixedWidthString());
            }
        } catch (IOException e) {
            System.err.println("Error writing merged output file: " + e.getMessage());
            System.exit(1);
        }

        // Display merged records
        try (BufferedReader reader = new BufferedReader(new FileReader(MERGED_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error opening merged output file: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Sorts the merged file by contract ID descending,
     * writes to sorted-contract-id.txt, and displays all records
     */
    private void sortAndDisplayFile() {
        System.out.println("Sorting merged file on descending contract id....");

        List<CustomerRecord> records = new ArrayList<>();

        // Read from merge-output.txt
        try (BufferedReader reader = new BufferedReader(new FileReader(MERGED_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(CustomerRecord.fromFixedWidthString(line));
            }
        } catch (IOException e) {
            System.err.println("Error opening merged file: " + e.getMessage());
            System.exit(1);
        }

        // Sort by contract ID descending
        Collections.sort(records, (a, b) -> Integer.compare(b.getCustomerContractId(), a.getCustomerContractId()));

        // Write to sorted-contract-id.txt
        try (PrintWriter writer = new PrintWriter(new FileWriter(SORTED_CONTRACT_ID_FILE))) {
            for (CustomerRecord record : records) {
                writer.println(record.toFixedWidthString());
            }
        } catch (IOException e) {
            System.err.println("Error writing sorted output file: " + e.getMessage());
            System.exit(1);
        }

        // Display sorted records
        try (BufferedReader reader = new BufferedReader(new FileReader(SORTED_CONTRACT_ID_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error opening sorted output file: " + e.getMessage());
            System.exit(1);
        }
    }
}
