package com.example.mergesort;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Java implementation of COBOL merge_sort_test.cbl
 * 
 * Author: Erik Eriksen (original COBOL)
 * Date: 2021-09-19 (original COBOL)
 * Purpose: Testing sort and merge syntax on test data.
 * 
 * This Java version replicates the functionality of the COBOL program:
 * 1. Creates test data files with customer records
 * 2. Merges and sorts files by customer ID (ascending)
 * 3. Sorts the merged file by contract ID (descending)
 */
public class MergeSortExample {

    private static final String TEST_FILE_1 = "test-file-1.txt";
    private static final String TEST_FILE_2 = "test-file-2.txt";
    private static final String MERGED_FILE = "merge-output.txt";
    private static final String SORTED_CONTRACT_ID_FILE = "sorted-contract-id.txt";

    public static void main(String[] args) {
        MergeSortExample example = new MergeSortExample();
        example.run();
    }

    public void run() {
        createTestData();
        mergeAndDisplayFiles();
        sortAndDisplayFile();
        System.out.println("Done.");
    }

    /**
     * Merges both test files and sorts by customer ID (ascending).
     * Equivalent to COBOL merge-and-display-files paragraph.
     */
    public void mergeAndDisplayFiles() {
        System.out.println("Merging and sorting files...");

        List<CustomerRecord> allRecords = new ArrayList<>();

        try {
            allRecords.addAll(readRecordsFromFile(TEST_FILE_1));
            allRecords.addAll(readRecordsFromFile(TEST_FILE_2));
        } catch (IOException e) {
            System.err.println("Error reading input files: " + e.getMessage());
            System.exit(1);
        }

        allRecords.sort(Comparator.comparingInt(CustomerRecord::getCustomerId));

        try {
            writeRecordsToFile(MERGED_FILE, allRecords);
        } catch (IOException e) {
            System.err.println("Error writing merged output file: " + e.getMessage());
            System.exit(1);
        }

        for (CustomerRecord record : allRecords) {
            System.out.println(record);
        }
    }

    /**
     * Sorts the merged file by contract ID (descending).
     * Equivalent to COBOL sort-and-display-file paragraph.
     */
    public void sortAndDisplayFile() {
        System.out.println("Sorting merged file on descending contract id....");

        List<CustomerRecord> records;
        try {
            records = readRecordsFromFile(MERGED_FILE);
        } catch (IOException e) {
            System.err.println("Error reading merged file: " + e.getMessage());
            System.exit(1);
            return;
        }

        records.sort(Comparator.comparingInt(CustomerRecord::getCustomerContractId).reversed());

        try {
            writeRecordsToFile(SORTED_CONTRACT_ID_FILE, records);
        } catch (IOException e) {
            System.err.println("Error writing sorted output file: " + e.getMessage());
            System.exit(1);
        }

        for (CustomerRecord record : records) {
            System.out.println(record);
        }
    }

    /**
     * Creates test data files with customer records.
     * Equivalent to COBOL create-test-data paragraph.
     */
    public void createTestData() {
        System.out.println("Creating test data files...");

        List<CustomerRecord> eastRecords = new ArrayList<>();
        eastRecords.add(new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"));
        eastRecords.add(new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"));
        eastRecords.add(new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"));
        eastRecords.add(new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"));
        eastRecords.add(new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"));
        eastRecords.add(new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75"));

        try {
            writeRecordsToFile(TEST_FILE_1, eastRecords);
        } catch (IOException e) {
            System.err.println("Failed to open file for output: " + e.getMessage());
            System.exit(1);
        }

        List<CustomerRecord> westRecords = new ArrayList<>();
        westRecords.add(new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"));
        westRecords.add(new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"));
        westRecords.add(new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"));
        westRecords.add(new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"));
        westRecords.add(new CustomerRecord(24, "last-24", "first-24", 247, "comment-24"));

        try {
            writeRecordsToFile(TEST_FILE_2, westRecords);
        } catch (IOException e) {
            System.err.println("Failed to open file for output: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Reads customer records from a file.
     */
    public List<CustomerRecord> readRecordsFromFile(String filename) throws IOException {
        List<CustomerRecord> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() >= 135) {
                    int customerId = Integer.parseInt(line.substring(0, 5).trim());
                    String lastName = line.substring(5, 55).trim();
                    String firstName = line.substring(55, 105).trim();
                    int contractId = Integer.parseInt(line.substring(105, 110).trim());
                    String comment = line.substring(110, 135).trim();
                    records.add(new CustomerRecord(customerId, lastName, firstName, contractId, comment));
                }
            }
        }
        return records;
    }

    /**
     * Writes customer records to a file.
     */
    public void writeRecordsToFile(String filename, List<CustomerRecord> records) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (CustomerRecord record : records) {
                writer.println(record);
            }
        }
    }

    /**
     * Returns the list of east region test records.
     */
    public List<CustomerRecord> getEastRecords() {
        List<CustomerRecord> records = new ArrayList<>();
        records.add(new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"));
        records.add(new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"));
        records.add(new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"));
        records.add(new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"));
        records.add(new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"));
        records.add(new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75"));
        return records;
    }

    /**
     * Returns the list of west region test records.
     */
    public List<CustomerRecord> getWestRecords() {
        List<CustomerRecord> records = new ArrayList<>();
        records.add(new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"));
        records.add(new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"));
        records.add(new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"));
        records.add(new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"));
        records.add(new CustomerRecord(24, "last-24", "first-24", 247, "comment-24"));
        return records;
    }
}
