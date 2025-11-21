package com.example.mergesort;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class MergeSortExample {
    
    private static final String TEST_FILE_1 = "test-file-1.txt";
    private static final String TEST_FILE_2 = "test-file-2.txt";
    private static final String MERGED_FILE = "merge-output.txt";
    private static final String SORTED_FILE = "sorted-contract-id.txt";
    
    public static void createTestData() {
        System.out.println("Creating test data files...");
        
        List<CustomerRecord> eastRegionData = TestDataGenerator.generateEastRegionData();
        FileManager.FileStatus status1 = FileManager.writeRecords(eastRegionData, TEST_FILE_1);
        
        if (!FileManager.checkFileStatus(status1)) {
            System.err.println("Failed to create test file 1");
            System.exit(1);
        }
        
        List<CustomerRecord> westRegionData = TestDataGenerator.generateWestRegionData();
        FileManager.FileStatus status2 = FileManager.writeRecords(westRegionData, TEST_FILE_2);
        
        if (!FileManager.checkFileStatus(status2)) {
            System.err.println("Failed to create test file 2");
            System.exit(1);
        }
    }
    
    public static void mergeAndDisplayFiles() {
        System.out.println("Merging and sorting files...");
        
        try {
            List<CustomerRecord> file1Records = FileManager.readRecords(TEST_FILE_1);
            List<CustomerRecord> file2Records = FileManager.readRecords(TEST_FILE_2);
            
            List<CustomerRecord> mergedRecords = FileMerger.merge(
                file1Records, 
                file2Records, 
                Comparator.comparing(CustomerRecord::getCustomerId)
            );
            
            FileManager.FileStatus status = FileManager.writeRecords(mergedRecords, MERGED_FILE);
            
            if (!FileManager.checkFileStatus(status)) {
                System.err.println("Error writing merged output file");
                System.exit(1);
            }
            
            List<CustomerRecord> displayRecords = FileManager.readRecords(MERGED_FILE);
            for (CustomerRecord record : displayRecords) {
                System.out.println(record.toString());
            }
            
        } catch (IOException e) {
            System.err.println("Error reading input files: " + e.getMessage());
            System.exit(1);
        }
    }
    
    public static void sortAndDisplayFile() {
        System.out.println("Sorting merged file on descending contract id....");
        
        try {
            List<CustomerRecord> mergedRecords = FileManager.readRecords(MERGED_FILE);
            
            List<CustomerRecord> sortedRecords = FileSorter.sort(
                mergedRecords,
                Comparator.comparing(CustomerRecord::getContractId).reversed()
            );
            
            FileManager.FileStatus status = FileManager.writeRecords(sortedRecords, SORTED_FILE);
            
            if (!FileManager.checkFileStatus(status)) {
                System.err.println("Error writing sorted output file");
                System.exit(1);
            }
            
            List<CustomerRecord> displayRecords = FileManager.readRecords(SORTED_FILE);
            for (CustomerRecord record : displayRecords) {
                System.out.println(record.toString());
            }
            
        } catch (IOException e) {
            System.err.println("Error reading merged file: " + e.getMessage());
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
