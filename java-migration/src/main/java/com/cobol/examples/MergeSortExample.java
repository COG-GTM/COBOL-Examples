package com.cobol.examples;

import java.io.*;
import java.util.*;

/**
 * Java migration of merge_sort_test.cbl
 * 
 * This program demonstrates:
 * - File I/O operations with multiple input/output files
 * - MERGE functionality for combining sorted files
 * - SORT functionality for sorting data
 * 
 * COBOL-to-Java mappings:
 * - COBOL MERGE statement -> Java manual merge algorithm using sorted lists
 * - COBOL SORT statement -> Java Collections.sort() with Comparator
 * - COBOL file I/O -> Java BufferedReader/BufferedWriter
 * - COBOL PERFORM -> Java methods and loops
 */
public class MergeSortExample {

    public static void main(String[] args) {
        try {
            MergeSortExample example = new MergeSortExample();
            example.run();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void run() throws IOException {
        createTestData();
        mergeAndDisplayFiles();
        sortAndDisplayFile();
        System.out.println("Done.");
    }

    /**
     * Creates test data files matching COBOL create-test-data paragraph.
     * Corresponds to lines 173-338 in merge_sort_test.cbl
     */
    private void createTestData() throws IOException {
        System.out.println("Creating test data files...");

        List<CustomerRecord> eastRecords = new ArrayList<>();
        eastRecords.add(new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"));
        eastRecords.add(new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"));
        eastRecords.add(new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"));
        eastRecords.add(new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"));
        eastRecords.add(new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"));
        eastRecords.add(new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75"));

        writeRecordsToFile("test-file-1.txt", eastRecords);

        List<CustomerRecord> westRecords = new ArrayList<>();
        westRecords.add(new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"));
        westRecords.add(new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"));
        westRecords.add(new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"));
        westRecords.add(new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"));
        westRecords.add(new CustomerRecord(24, "last-24", "first-24", 247, "comment-24"));

        writeRecordsToFile("test-file-2.txt", westRecords);
    }

    /**
     * Merges and displays files matching COBOL merge-and-display-files paragraph.
     * Corresponds to lines 103-134 in merge_sort_test.cbl
     * 
     * COBOL MERGE statement is replaced with Java merge algorithm:
     * 1. Read both files into lists
     * 2. Sort each list by customer ID (ascending)
     * 3. Merge the two sorted lists
     * 4. Write merged result to output file
     */
    private void mergeAndDisplayFiles() throws IOException {
        System.out.println("Merging and sorting files...");

        List<CustomerRecord> file1Records = readRecordsFromFile("test-file-1.txt");
        List<CustomerRecord> file2Records = readRecordsFromFile("test-file-2.txt");

        file1Records.sort(Comparator.comparingInt(CustomerRecord::getCustomerId));
        file2Records.sort(Comparator.comparingInt(CustomerRecord::getCustomerId));

        List<CustomerRecord> mergedRecords = mergeSortedLists(file1Records, file2Records);

        writeRecordsToFile("merge-output.txt", mergedRecords);

        for (CustomerRecord record : mergedRecords) {
            System.out.println(record);
        }
    }

    /**
     * Sorts and displays file matching COBOL sort-and-display-file paragraph.
     * Corresponds to lines 138-169 in merge_sort_test.cbl
     * 
     * COBOL SORT statement is replaced with Java Collections.sort() using Comparator.
     */
    private void sortAndDisplayFile() throws IOException {
        System.out.println("Sorting merged file on descending contract id....");

        List<CustomerRecord> records = readRecordsFromFile("merge-output.txt");

        records.sort(Comparator.comparingInt(CustomerRecord::getContractId).reversed());

        writeRecordsToFile("sorted-contract-id.txt", records);

        for (CustomerRecord record : records) {
            System.out.println(record);
        }
    }

    /**
     * Merges two sorted lists into one sorted list.
     * This implements the COBOL MERGE functionality manually.
     */
    private List<CustomerRecord> mergeSortedLists(List<CustomerRecord> list1, List<CustomerRecord> list2) {
        List<CustomerRecord> merged = new ArrayList<>();
        int i = 0, j = 0;

        while (i < list1.size() && j < list2.size()) {
            if (list1.get(i).getCustomerId() <= list2.get(j).getCustomerId()) {
                merged.add(list1.get(i));
                i++;
            } else {
                merged.add(list2.get(j));
                j++;
            }
        }

        while (i < list1.size()) {
            merged.add(list1.get(i));
            i++;
        }

        while (j < list2.size()) {
            merged.add(list2.get(j));
            j++;
        }

        return merged;
    }

    /**
     * Reads customer records from a file.
     * Replaces COBOL file I/O operations.
     */
    private List<CustomerRecord> readRecordsFromFile(String filename) throws IOException {
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

    /**
     * Writes customer records to a file.
     * Replaces COBOL file I/O operations.
     */
    private void writeRecordsToFile(String filename, List<CustomerRecord> records) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (CustomerRecord record : records) {
                writer.write(record.toString());
                writer.newLine();
            }
        }
    }
}
