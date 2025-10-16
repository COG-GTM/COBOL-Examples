package com.cobol.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MergeSortProgram {
    
    private static final String TEST_FILE_1 = "test-file-1.txt";
    private static final String TEST_FILE_2 = "test-file-2.txt";
    private static final String MERGE_OUTPUT = "merge-output.txt";
    private static final String SORTED_OUTPUT = "sorted-contract-id.txt";
    
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
        
        FixedWidthFileIO.writeRecords(TEST_FILE_1, eastRecords);
        FixedWidthFileIO.writeRecords(TEST_FILE_2, westRecords);
    }
    
    private static void mergeAndDisplayFiles() throws IOException {
        System.out.println("Merging and sorting files...");
        
        List<CustomerRecord> eastRecords = FixedWidthFileIO.readRecords(TEST_FILE_1);
        List<CustomerRecord> westRecords = FixedWidthFileIO.readRecords(TEST_FILE_2);
        
        List<CustomerRecord> mergedRecords = new ArrayList<>();
        mergedRecords.addAll(eastRecords);
        mergedRecords.addAll(westRecords);
        mergedRecords.sort(Comparator.comparingInt(CustomerRecord::getCustomerId));
        
        FixedWidthFileIO.writeRecords(MERGE_OUTPUT, mergedRecords);
        for (CustomerRecord record : mergedRecords) {
            System.out.println(record.toFixedWidth());
        }
    }
    
    private static void sortAndDisplayFile() throws IOException {
        System.out.println("Sorting merged file on descending contract id....");
        
        List<CustomerRecord> records = FixedWidthFileIO.readRecords(MERGE_OUTPUT);
        
        records.sort(Comparator.comparingInt(CustomerRecord::getContractId).reversed());
        
        FixedWidthFileIO.writeRecords(SORTED_OUTPUT, records);
        for (CustomerRecord record : records) {
            System.out.println(record.toFixedWidth());
        }
    }
}
