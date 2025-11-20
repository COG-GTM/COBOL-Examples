package com.cobolexamples.sorting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MergeSortExample {
    
    private static final String TEST_FILE_1 = "test-file-1.txt";
    private static final String TEST_FILE_2 = "test-file-2.txt";
    private static final String MERGED_FILE = "merge-output.txt";
    private static final String SORTED_FILE = "sorted-contract-id.txt";
    
    public static void main(String[] args) {
        try {
            createTestData();
            mergeAndDisplayFiles();
            sortAndDisplayFile();
            System.out.println("Done.");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createTestData() throws IOException {
        System.out.println("Creating test data files...");
        
        List<CustomerRecord> eastRecords = new ArrayList<>();
        eastRecords.add(new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"));
        eastRecords.add(new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"));
        eastRecords.add(new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"));
        eastRecords.add(new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"));
        eastRecords.add(new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"));
        eastRecords.add(new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75"));
        
        writeRecordsToFile(TEST_FILE_1, eastRecords);
        
        List<CustomerRecord> westRecords = new ArrayList<>();
        westRecords.add(new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"));
        westRecords.add(new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"));
        westRecords.add(new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"));
        westRecords.add(new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"));
        westRecords.add(new CustomerRecord(24, "last-24", "first-24", 247, "comment-24"));
        
        writeRecordsToFile(TEST_FILE_2, westRecords);
    }
    
    private static void mergeAndDisplayFiles() throws IOException {
        System.out.println("Merging and sorting files...");
        
        List<CustomerRecord> allRecords = new ArrayList<>();
        
        allRecords.addAll(readRecordsFromFile(TEST_FILE_1));
        allRecords.addAll(readRecordsFromFile(TEST_FILE_2));
        
        Collections.sort(allRecords, Comparator.comparingInt(CustomerRecord::getCustomerId));
        
        writeRecordsToFile(MERGED_FILE, allRecords);
        
        List<CustomerRecord> mergedRecords = readRecordsFromFile(MERGED_FILE);
        for (CustomerRecord record : mergedRecords) {
            System.out.println(record.toString());
        }
    }
    
    private static void sortAndDisplayFile() throws IOException {
        System.out.println("Sorting merged file on descending contract id....");
        
        List<CustomerRecord> records = readRecordsFromFile(MERGED_FILE);
        
        Collections.sort(records, Comparator.comparingInt(CustomerRecord::getCustomerContractId).reversed());
        
        writeRecordsToFile(SORTED_FILE, records);
        
        List<CustomerRecord> sortedRecords = readRecordsFromFile(SORTED_FILE);
        for (CustomerRecord record : sortedRecords) {
            System.out.println(record.toString());
        }
    }
    
    private static void writeRecordsToFile(String filename, List<CustomerRecord> records) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (CustomerRecord record : records) {
                writer.write(record.toFileFormat());
                writer.newLine();
            }
        }
    }
    
    private static List<CustomerRecord> readRecordsFromFile(String filename) throws IOException {
        List<CustomerRecord> records = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    records.add(CustomerRecord.fromFileFormat(line));
                }
            }
        }
        
        return records;
    }
}
