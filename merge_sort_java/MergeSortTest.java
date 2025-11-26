import java.io.*;
import java.util.*;

/**
 * MergeSortTest - Java port of merge_sort_test.cbl
 * 
 * Purpose: Testing sort and merge syntax on test data.
 * Original author: Erik Eriksen
 * Original date: 2021-09-19
 * 
 * This program demonstrates:
 * 1. Creating test data files with customer records
 * 2. Merging two files and sorting by customer ID (ascending)
 * 3. Sorting the merged file by contract ID (descending)
 */
public class MergeSortTest {

    private static final String TEST_FILE_1 = "test-file-1.txt";
    private static final String TEST_FILE_2 = "test-file-2.txt";
    private static final String MERGED_FILE = "merge-output.txt";
    private static final String SORTED_CONTRACT_ID_FILE = "sorted-contract-id.txt";

    public static void main(String[] args) {
        MergeSortTest program = new MergeSortTest();
        
        program.createTestData();
        program.mergeAndDisplayFiles();
        program.sortAndDisplayFile();
        
        System.out.println("Done.");
    }

    /**
     * Merges two input files and sorts by customer ID in ascending order.
     * Equivalent to COBOL merge-and-display-files paragraph.
     */
    private void mergeAndDisplayFiles() {
        System.out.println("Merging and sorting files...");

        List<CustomerRecord> allRecords = new ArrayList<>();

        try (BufferedReader reader1 = new BufferedReader(new FileReader(TEST_FILE_1))) {
            String line;
            while ((line = reader1.readLine()) != null) {
                allRecords.add(CustomerRecord.fromString(line));
            }
        } catch (IOException e) {
            System.out.println("Error opening test file 1: " + e.getMessage());
            System.exit(1);
        }

        try (BufferedReader reader2 = new BufferedReader(new FileReader(TEST_FILE_2))) {
            String line;
            while ((line = reader2.readLine()) != null) {
                allRecords.add(CustomerRecord.fromString(line));
            }
        } catch (IOException e) {
            System.out.println("Error opening test file 2: " + e.getMessage());
            System.exit(1);
        }

        allRecords.sort(Comparator.comparingInt(CustomerRecord::getCustomerId));

        try (PrintWriter writer = new PrintWriter(new FileWriter(MERGED_FILE))) {
            for (CustomerRecord record : allRecords) {
                writer.println(record.toString());
            }
        } catch (IOException e) {
            System.out.println("Error writing merged output file: " + e.getMessage());
            System.exit(1);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(MERGED_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error opening merged output file: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Sorts the merged file by contract ID in descending order.
     * Equivalent to COBOL sort-and-display-file paragraph.
     */
    private void sortAndDisplayFile() {
        System.out.println("Sorting merged file on descending contract id....");

        List<CustomerRecord> records = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(MERGED_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(CustomerRecord.fromString(line));
            }
        } catch (IOException e) {
            System.out.println("Error opening merged file: " + e.getMessage());
            System.exit(1);
        }

        records.sort(Comparator.comparingInt(CustomerRecord::getCustomerContractId).reversed());

        try (PrintWriter writer = new PrintWriter(new FileWriter(SORTED_CONTRACT_ID_FILE))) {
            for (CustomerRecord record : records) {
                writer.println(record.toString());
            }
        } catch (IOException e) {
            System.out.println("Error writing sorted output file: " + e.getMessage());
            System.exit(1);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(SORTED_CONTRACT_ID_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error opening sorted output file: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Creates test data files with customer records.
     * Equivalent to COBOL create-test-data paragraph.
     */
    private void createTestData() {
        System.out.println("Creating test data files...");

        try (PrintWriter writer = new PrintWriter(new FileWriter(TEST_FILE_1))) {
            writer.println(new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"));
            writer.println(new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"));
            writer.println(new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"));
            writer.println(new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"));
            writer.println(new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"));
            writer.println(new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75"));
        } catch (IOException e) {
            System.out.println("Failed to open file for output: " + e.getMessage());
            System.exit(1);
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(TEST_FILE_2))) {
            writer.println(new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"));
            writer.println(new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"));
            writer.println(new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"));
            writer.println(new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"));
            writer.println(new CustomerRecord(24, "last-24", "first-24", 247, "comment-24"));
        } catch (IOException e) {
            System.out.println("Failed to open file for output: " + e.getMessage());
            System.exit(1);
        }
    }
}
