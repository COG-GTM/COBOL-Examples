package com.example.mergesort;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class MergeSortMain {
    
    private static final String TEST_FILE_1 = "test-file-1.txt";
    private static final String TEST_FILE_2 = "test-file-2.txt";
    private static final String MERGED_FILE = "merge-output.txt";
    private static final String SORTED_FILE = "sorted-contract-id.txt";
    
    private MergeSortProcessor processor;
    
    public MergeSortMain() {
        this.processor = new MergeSortProcessor();
    }
    
    public static void main(String[] args) {
        MergeSortMain app = new MergeSortMain();
        
        try {
            app.createTestData();
            app.mergeAndDisplayFiles();
            app.sortAndDisplayFile();
            
            System.out.println("Done.");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void createTestData() {
        System.out.println("Creating test data files...");
        
        try {
            createTestFile1();
            createTestFile2();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create test data: " + e.getMessage(), e);
        }
    }
    
    private void createTestFile1() throws IOException {
        List<CustomerRecord> records = Arrays.asList(
            new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"),
            new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"),
            new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"),
            new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"),
            new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"),
            new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75")
        );
        
        writeRecordsToFile(records, TEST_FILE_1);
    }
    
    private void createTestFile2() throws IOException {
        List<CustomerRecord> records = Arrays.asList(
            new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"),
            new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"),
            new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"),
            new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"),
            new CustomerRecord(24, "last-24", "first-24", 247, "comment-24")
        );
        
        writeRecordsToFile(records, TEST_FILE_2);
    }
    
    private void writeRecordsToFile(List<CustomerRecord> records, String fileName) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName))) {
            for (CustomerRecord record : records) {
                writer.write(record.toString());
                writer.newLine();
            }
        }
    }
    
    public void mergeAndDisplayFiles() {
        System.out.println("Merging and sorting files...");
        
        processor.mergeFiles(TEST_FILE_1, TEST_FILE_2, MERGED_FILE);
        processor.displayFile(MERGED_FILE, "Merged file contents:");
    }
    
    public void sortAndDisplayFile() {
        System.out.println("Sorting merged file on descending contract id....");
        
        processor.sortFileByContractId(MERGED_FILE, SORTED_FILE);
        processor.displayFile(SORTED_FILE, "Sorted file contents:");
    }
}
