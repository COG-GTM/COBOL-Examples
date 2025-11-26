import java.io.*;
import java.util.*;

/**
 * Java implementation of the COBOL merge_sort_test.cbl program.
 * 
 * This program:
 * 1. Creates two test data files (test-file-1.txt and test-file-2.txt)
 * 2. Merges them by customerID (ascending) into merge-output.txt
 * 3. Sorts the merged output by contractID (descending) into sorted-contract-id.txt
 * 
 * Author: Converted from COBOL by Devin
 * Original COBOL author: Erik Eriksen
 * Original date: 2021-09-19
 */
public class MergeSortTest {
    
    private static final String TEST_FILE_1 = "test-file-1.txt";
    private static final String TEST_FILE_2 = "test-file-2.txt";
    private static final String MERGED_FILE = "merge-output.txt";
    private static final String SORTED_FILE = "sorted-contract-id.txt";
    
    public static void main(String[] args) {
        MergeSortTest program = new MergeSortTest();
        
        try {
            program.createTestData();
            program.mergeAndDisplayFiles();
            program.sortAndDisplayFile();
            System.out.println("Done.");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private void createTestData() throws IOException {
        System.out.println("Creating test data files...");
        
        List<CustomerRecord> eastRecords = new ArrayList<>();
        eastRecords.add(new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"));
        eastRecords.add(new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"));
        eastRecords.add(new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"));
        eastRecords.add(new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"));
        eastRecords.add(new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"));
        eastRecords.add(new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75"));
        
        FixedWidthFileIO.writeRecords(TEST_FILE_1, eastRecords);
        
        List<CustomerRecord> westRecords = new ArrayList<>();
        westRecords.add(new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"));
        westRecords.add(new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"));
        westRecords.add(new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"));
        westRecords.add(new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"));
        westRecords.add(new CustomerRecord(24, "last-24", "first-24", 247, "comment-24"));
        
        FixedWidthFileIO.writeRecords(TEST_FILE_2, westRecords);
    }
    
    private void mergeAndDisplayFiles() throws IOException {
        System.out.println("Merging and sorting files...");
        
        List<CustomerRecord> file1Records = FixedWidthFileIO.readRecords(TEST_FILE_1);
        List<CustomerRecord> file2Records = FixedWidthFileIO.readRecords(TEST_FILE_2);
        
        List<CustomerRecord> allRecords = new ArrayList<>();
        allRecords.addAll(file1Records);
        allRecords.addAll(file2Records);
        
        Collections.sort(allRecords, Comparator.comparingInt(CustomerRecord::getCustomerId));
        
        FixedWidthFileIO.writeRecords(MERGED_FILE, allRecords);
        
        System.out.println("Merged output (sorted by customerID ascending):");
        FixedWidthFileIO.displayRecords(MERGED_FILE);
    }
    
    private void sortAndDisplayFile() throws IOException {
        System.out.println("Sorting merged file on descending contract id....");
        
        List<CustomerRecord> records = FixedWidthFileIO.readRecords(MERGED_FILE);
        
        Collections.sort(records, Comparator.comparingInt(CustomerRecord::getContractId).reversed());
        
        FixedWidthFileIO.writeRecords(SORTED_FILE, records);
        
        System.out.println("Sorted output (sorted by contractID descending):");
        FixedWidthFileIO.displayRecords(SORTED_FILE);
    }
}
