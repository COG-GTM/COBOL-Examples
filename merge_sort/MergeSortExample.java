/**
 * Java migration of COBOL merge sort program (merge_sort_test.cbl)
 * 
 * This program demonstrates file sorting and merging, equivalent to COBOL's
 * SORT and MERGE statements. It creates test data files, merges them sorted
 * by customer ID (ascending), then sorts the merged file by contract ID (descending).
 * 
 * Original COBOL author: Erik Eriksen
 * Original date: 2021-09-19
 * Java migration: 2024
 */

import java.io.*;
import java.util.*;

public class MergeSortExample {

    /**
     * Customer record class - maps to COBOL record structure:
     * - f-customer-id: PIC 9(5) - 5 digits
     * - f-customer-last-name: PIC X(50) - 50 characters
     * - f-customer-first-name: PIC X(50) - 50 characters
     * - f-customer-contract-id: PIC 9(5) - 5 digits
     * - f-customer-comment: PIC X(25) - 25 characters
     * Total record length: 135 characters
     */
    public static class Customer {
        private int customerId;
        private String lastName;
        private String firstName;
        private int contractId;
        private String comment;

        public Customer() {
        }

        public Customer(int customerId, String lastName, String firstName, 
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

        public void setCustomerId(int customerId) {
            this.customerId = customerId;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public int getContractId() {
            return contractId;
        }

        public void setContractId(int contractId) {
            this.contractId = contractId;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        /**
         * Formats the customer record as a fixed-width string (135 characters total)
         * matching the COBOL record layout.
         */
        @Override
        public String toString() {
            return String.format("%05d%-50s%-50s%05d%-25s",
                customerId,
                padRight(lastName, 50),
                padRight(firstName, 50),
                contractId,
                padRight(comment, 25));
        }

        private String padRight(String s, int length) {
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
    private static final String MERGE_OUTPUT = "merge-output.txt";
    private static final String SORTED_CONTRACT_ID = "sorted-contract-id.txt";

    /**
     * Creates test data files matching the COBOL create-test-data paragraph.
     * File 1 contains 6 customer records (East region).
     * File 2 contains 5 customer records (West region).
     */
    public void createTestData() throws IOException {
        System.out.println("Creating test data files...");

        try (BufferedWriter writer1 = new BufferedWriter(new FileWriter(TEST_FILE_1))) {
            writer1.write(new Customer(1, "last-1", "first-1", 5423, "comment-1").toString());
            writer1.newLine();
            writer1.write(new Customer(5, "last-5", "first-5", 12323, "comment-5").toString());
            writer1.newLine();
            writer1.write(new Customer(10, "last-10", "first-10", 653, "comment-10").toString());
            writer1.newLine();
            writer1.write(new Customer(50, "last-50", "first-50", 5050, "comment-50").toString());
            writer1.newLine();
            writer1.write(new Customer(25, "last-25", "first-25", 7725, "comment-25").toString());
            writer1.newLine();
            writer1.write(new Customer(75, "last-75", "first-75", 1175, "comment-75").toString());
            writer1.newLine();
        }

        try (BufferedWriter writer2 = new BufferedWriter(new FileWriter(TEST_FILE_2))) {
            writer2.write(new Customer(999, "last-999", "first-999", 1610, "comment-99").toString());
            writer2.newLine();
            writer2.write(new Customer(3, "last-03", "first-03", 3331, "comment-03").toString());
            writer2.newLine();
            writer2.write(new Customer(30, "last-30", "first-30", 8765, "comment-30").toString());
            writer2.newLine();
            writer2.write(new Customer(85, "last-85", "first-85", 4567, "comment-85").toString());
            writer2.newLine();
            writer2.write(new Customer(24, "last-24", "first-24", 247, "comment-24").toString());
            writer2.newLine();
        }
    }

    /**
     * Parses a fixed-width customer record string.
     * Field positions:
     * - Customer ID: 0-5 (5 chars)
     * - Last Name: 5-55 (50 chars)
     * - First Name: 55-105 (50 chars)
     * - Contract ID: 105-110 (5 chars)
     * - Comment: 110-135 (25 chars)
     */
    public Customer parseCustomerRecord(String line) {
        if (line == null || line.length() < 135) {
            if (line != null) {
                line = String.format("%-135s", line);
            } else {
                return null;
            }
        }

        Customer customer = new Customer();
        customer.setCustomerId(Integer.parseInt(line.substring(0, 5).trim()));
        customer.setLastName(line.substring(5, 55).trim());
        customer.setFirstName(line.substring(55, 105).trim());
        customer.setContractId(Integer.parseInt(line.substring(105, 110).trim()));
        customer.setComment(line.substring(110, 135).trim());

        return customer;
    }

    /**
     * Reads a file and parses all customer records.
     */
    public List<Customer> readFile(String filename) throws IOException {
        List<Customer> customers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    customers.add(parseCustomerRecord(line));
                }
            }
        }
        return customers;
    }

    /**
     * Merges two test files and sorts by customer ID in ascending order.
     * Equivalent to COBOL's MERGE ON ASCENDING KEY f-customer-id.
     */
    public List<Customer> mergeFiles() throws IOException {
        System.out.println("Merging and sorting files...");

        List<Customer> allCustomers = new ArrayList<>();
        allCustomers.addAll(readFile(TEST_FILE_1));
        allCustomers.addAll(readFile(TEST_FILE_2));

        allCustomers.sort(Comparator.comparingInt(Customer::getCustomerId));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MERGE_OUTPUT))) {
            for (Customer customer : allCustomers) {
                writer.write(customer.toString());
                writer.newLine();
                System.out.println(customer.toString());
            }
        }

        return allCustomers;
    }

    /**
     * Sorts merged customers by contract ID in descending order.
     * Equivalent to COBOL's SORT ON DESCENDING KEY f-customer-contract-id.
     */
    public List<Customer> sortMergedFile(List<Customer> mergedCustomers) throws IOException {
        System.out.println("Sorting merged file on descending contract id....");

        List<Customer> sortedCustomers = new ArrayList<>(mergedCustomers);
        sortedCustomers.sort(Comparator.comparingInt(Customer::getContractId).reversed());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SORTED_CONTRACT_ID))) {
            for (Customer customer : sortedCustomers) {
                writer.write(customer.toString());
                writer.newLine();
                System.out.println(customer.toString());
            }
        }

        return sortedCustomers;
    }

    /**
     * Main method - orchestrates the entire merge sort process.
     */
    public static void main(String[] args) {
        MergeSortExample example = new MergeSortExample();

        try {
            example.createTestData();
            List<Customer> mergedCustomers = example.mergeFiles();
            example.sortMergedFile(mergedCustomers);
            System.out.println("Done.");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
