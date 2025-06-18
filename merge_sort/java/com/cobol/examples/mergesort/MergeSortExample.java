package com.cobol.examples.mergesort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MergeSortExample {
    
    private static final String TEST_FILE_1 = "test-file-1.txt";
    private static final String TEST_FILE_2 = "test-file-2.txt";
    private static final String MERGE_OUTPUT = "merge-output.txt";
    private static final String SORTED_CONTRACT_ID = "sorted-contract-id.txt";
    
    public static void main(String[] args) {
        try {
            createTestData();
            
            mergeAndDisplayFiles();
            
            sortAndDisplayFile();
            
            System.out.println("Done.");
            
        } catch (IOException e) {
            System.err.println("Error during file operations: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
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
        
        FileIOUtils.writeCustomerRecords(TEST_FILE_1, eastRecords);
        
        List<CustomerRecord> westRecords = new ArrayList<>();
        westRecords.add(new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"));
        westRecords.add(new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"));
        westRecords.add(new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"));
        westRecords.add(new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"));
        westRecords.add(new CustomerRecord(24, "last-24", "first-24", 247, "comment-24"));
        
        FileIOUtils.writeCustomerRecords(TEST_FILE_2, westRecords);
    }
    
    private static void mergeAndDisplayFiles() throws IOException {
        System.out.println("Merging and sorting files...");
        
        List<CustomerRecord> file1Records = FileIOUtils.readCustomerRecords(TEST_FILE_1);
        List<CustomerRecord> file2Records = FileIOUtils.readCustomerRecords(TEST_FILE_2);
        
        List<CustomerRecord> mergedRecords = new ArrayList<>();
        mergedRecords.addAll(file1Records);
        mergedRecords.addAll(file2Records);
        
        Collections.sort(mergedRecords, CustomerRecord.BY_CUSTOMER_ID);
        
        FileIOUtils.writeCustomerRecords(MERGE_OUTPUT, mergedRecords);
        
        for (CustomerRecord record : mergedRecords) {
            System.out.println(record.toString());
        }
    }
    
    private static void sortAndDisplayFile() throws IOException {
        System.out.println("Sorting merged file on descending contract id....");
        
        List<CustomerRecord> mergedRecords = FileIOUtils.readCustomerRecords(MERGE_OUTPUT);
        
        Collections.sort(mergedRecords, CustomerRecord.BY_CONTRACT_ID_DESC);
        
        FileIOUtils.writeCustomerRecords(SORTED_CONTRACT_ID, mergedRecords);
        
        for (CustomerRecord record : mergedRecords) {
            System.out.println(record.toString());
        }
    }
}
