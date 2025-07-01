package com.example.mergesort;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MergeSortExample {
    
    private static final String TEST_FILE_1 = "data/test-file-1.txt";
    private static final String TEST_FILE_2 = "data/test-file-2.txt";
    private static final String MERGED_FILE = "data/merge-output.txt";
    private static final String SORTED_FILE = "data/sorted-contract-id.txt";
    
    public static void main(String[] args) {
        try {
            createTestData();
            
            mergeAndDisplayFiles();
            
            sortAndDisplayFile();
            
            System.out.println("Done.");
            
        } catch (IOException e) {
            System.err.println("Error during file operations: " + e.getMessage());
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
        
        FileBasedMergeSort.createTestFile(TEST_FILE_1, eastRecords);
        FileBasedMergeSort.createTestFile(TEST_FILE_2, westRecords);
    }
    
    private static void mergeAndDisplayFiles() throws IOException {
        FileBasedMergeSort.mergeFiles(TEST_FILE_1, TEST_FILE_2, MERGED_FILE);
        FileBasedMergeSort.displayFileContents(MERGED_FILE, "Merged file contents (sorted by customer ID ascending)");
    }
    
    private static void sortAndDisplayFile() throws IOException {
        FileBasedMergeSort.sortFileByContractId(MERGED_FILE, SORTED_FILE);
        FileBasedMergeSort.displayFileContents(SORTED_FILE, "Final sorted file contents (sorted by contract ID descending)");
    }
}
