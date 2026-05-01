/**
 * Java conversion of merge_sort_test.cbl
 *
 * author:  Erik Eriksen (original COBOL), converted to Java
 * date:    2021-09-19 (original)
 * purpose: Testing sort and merge on test data using file-based I/O.
 *
 * Record layout (135 characters, fixed-width, line sequential):
 *   customer-id         PIC 9(5)   positions  1-5
 *   customer-last-name  PIC X(50)  positions  6-55
 *   customer-first-name PIC X(50)  positions 56-105
 *   customer-contract-id PIC 9(5)  positions 106-110
 *   customer-comment    PIC X(25)  positions 111-135
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MergeSortTest {

    private static final int CUSTOMER_ID_LEN = 5;
    private static final int LAST_NAME_LEN = 50;
    private static final int FIRST_NAME_LEN = 50;
    private static final int CONTRACT_ID_LEN = 5;
    private static final int COMMENT_LEN = 25;

    private static final String TEST_FILE_1 = "test-file-1.txt";
    private static final String TEST_FILE_2 = "test-file-2.txt";
    private static final String MERGE_OUTPUT = "merge-output.txt";
    private static final String SORTED_CONTRACT_ID = "sorted-contract-id.txt";

    public static void main(String[] args) {
        createTestData();
        mergeAndDisplayFiles();
        sortAndDisplayFile();
        System.out.println("Done.");
    }

    private static void mergeAndDisplayFiles() {
        System.out.println("Merging and sorting files...");

        List<String> records = new ArrayList<>();
        readRecordsFromFile(TEST_FILE_1, records);
        readRecordsFromFile(TEST_FILE_2, records);

        records.sort(Comparator.comparingInt(MergeSortTest::extractCustomerId));

        writeRecordsToFile(MERGE_OUTPUT, records);

        displayFile(MERGE_OUTPUT);
    }

    private static void sortAndDisplayFile() {
        System.out.println("Sorting merged file on descending contract id....");

        List<String> records = new ArrayList<>();
        readRecordsFromFile(MERGE_OUTPUT, records);

        records.sort(Comparator.comparingInt(MergeSortTest::extractContractId).reversed());

        writeRecordsToFile(SORTED_CONTRACT_ID, records);

        displayFile(SORTED_CONTRACT_ID);
    }

    private static void createTestData() {
        System.out.println("Creating test data files...");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEST_FILE_1))) {
            writer.write(formatRecord(1, "last-1", "first-1", 5423, "comment-1"));
            writer.newLine();
            writer.write(formatRecord(5, "last-5", "first-5", 12323, "comment-5"));
            writer.newLine();
            writer.write(formatRecord(10, "last-10", "first-10", 653, "comment-10"));
            writer.newLine();
            writer.write(formatRecord(50, "last-50", "first-50", 5050, "comment-50"));
            writer.newLine();
            writer.write(formatRecord(25, "last-25", "first-25", 7725, "comment-25"));
            writer.newLine();
            writer.write(formatRecord(75, "last-75", "first-75", 1175, "comment-75"));
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Failed to open file for output: " + TEST_FILE_1);
            System.exit(1);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEST_FILE_2))) {
            writer.write(formatRecord(999, "last-999", "first-999", 1610, "comment-99"));
            writer.newLine();
            writer.write(formatRecord(3, "last-03", "first-03", 3331, "comment-03"));
            writer.newLine();
            writer.write(formatRecord(30, "last-30", "first-30", 8765, "comment-30"));
            writer.newLine();
            writer.write(formatRecord(85, "last-85", "first-85", 4567, "comment-85"));
            writer.newLine();
            writer.write(formatRecord(24, "last-24", "first-24", 247, "comment-24"));
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Failed to open file for output: " + TEST_FILE_2);
            System.exit(1);
        }
    }

    private static String formatRecord(int customerId, String lastName,
                                        String firstName, int contractId,
                                        String comment) {
        return padNumeric(customerId, CUSTOMER_ID_LEN)
                + padAlpha(lastName, LAST_NAME_LEN)
                + padAlpha(firstName, FIRST_NAME_LEN)
                + padNumeric(contractId, CONTRACT_ID_LEN)
                + padAlpha(comment, COMMENT_LEN);
    }

    private static String padNumeric(int value, int length) {
        return String.format("%0" + length + "d", value);
    }

    private static String padAlpha(String value, int length) {
        if (value.length() >= length) {
            return value.substring(0, length);
        }
        return value + " ".repeat(length - value.length());
    }

    private static int extractCustomerId(String record) {
        return Integer.parseInt(record.substring(0, CUSTOMER_ID_LEN).trim());
    }

    private static int extractContractId(String record) {
        int start = CUSTOMER_ID_LEN + LAST_NAME_LEN + FIRST_NAME_LEN;
        return Integer.parseInt(record.substring(start, start + CONTRACT_ID_LEN).trim());
    }

    private static void readRecordsFromFile(String filename, List<String> records) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error opening file: " + filename);
            System.exit(1);
        }
    }

    private static void writeRecordsToFile(String filename, List<String> records) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String record : records) {
                writer.write(record);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to file: " + filename);
            System.exit(1);
        }
    }

    private static void displayFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error opening file: " + filename);
            System.exit(1);
        }
    }
}
