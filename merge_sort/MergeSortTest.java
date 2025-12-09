/**
 * JUnit test cases for MergeSortExample - Java migration of COBOL merge sort program.
 * 
 * Tests verify:
 * - Test data file creation with correct record counts
 * - Merging produces correct number of records sorted by customer ID (ascending)
 * - Sorting produces records ordered by contract ID (descending)
 * - Fixed-width record parsing works correctly
 */

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class MergeSortTest {

    private MergeSortExample mergeSortExample;

    private static final String TEST_FILE_1 = "test-file-1.txt";
    private static final String TEST_FILE_2 = "test-file-2.txt";
    private static final String MERGE_OUTPUT = "merge-output.txt";
    private static final String SORTED_CONTRACT_ID = "sorted-contract-id.txt";

    @BeforeEach
    void setUp() {
        mergeSortExample = new MergeSortExample();
        cleanUpTestFiles();
    }

    @AfterEach
    void tearDown() {
        cleanUpTestFiles();
    }

    private void cleanUpTestFiles() {
        try {
            Files.deleteIfExists(Paths.get(TEST_FILE_1));
            Files.deleteIfExists(Paths.get(TEST_FILE_2));
            Files.deleteIfExists(Paths.get(MERGE_OUTPUT));
            Files.deleteIfExists(Paths.get(SORTED_CONTRACT_ID));
        } catch (IOException e) {
            // Ignore cleanup errors
        }
    }

    @Test
    @DisplayName("Test that createTestData creates files with correct number of records")
    void testCreateTestData() throws IOException {
        mergeSortExample.createTestData();

        assertTrue(Files.exists(Paths.get(TEST_FILE_1)), "Test file 1 should exist");
        assertTrue(Files.exists(Paths.get(TEST_FILE_2)), "Test file 2 should exist");

        List<MergeSortExample.Customer> file1Records = mergeSortExample.readFile(TEST_FILE_1);
        List<MergeSortExample.Customer> file2Records = mergeSortExample.readFile(TEST_FILE_2);

        assertEquals(6, file1Records.size(), "Test file 1 should have 6 records");
        assertEquals(5, file2Records.size(), "Test file 2 should have 5 records");

        assertEquals(1, file1Records.get(0).getCustomerId(), "First record in file 1 should have customer ID 1");
        assertEquals("last-1", file1Records.get(0).getLastName(), "First record should have last name 'last-1'");
        assertEquals("first-1", file1Records.get(0).getFirstName(), "First record should have first name 'first-1'");
        assertEquals(5423, file1Records.get(0).getContractId(), "First record should have contract ID 5423");
        assertEquals("comment-1", file1Records.get(0).getComment(), "First record should have comment 'comment-1'");

        assertEquals(999, file2Records.get(0).getCustomerId(), "First record in file 2 should have customer ID 999");
    }

    @Test
    @DisplayName("Test that mergeFiles produces 11 records sorted by customer ID ascending")
    void testMergeFiles() throws IOException {
        mergeSortExample.createTestData();

        List<MergeSortExample.Customer> mergedCustomers = mergeSortExample.mergeFiles();

        assertEquals(11, mergedCustomers.size(), "Merged file should have 11 records (6 + 5)");

        assertTrue(Files.exists(Paths.get(MERGE_OUTPUT)), "Merge output file should exist");

        for (int i = 0; i < mergedCustomers.size() - 1; i++) {
            assertTrue(
                mergedCustomers.get(i).getCustomerId() <= mergedCustomers.get(i + 1).getCustomerId(),
                "Records should be sorted by customer ID in ascending order"
            );
        }

        assertEquals(1, mergedCustomers.get(0).getCustomerId(), "First record should have lowest customer ID (1)");
        assertEquals(999, mergedCustomers.get(10).getCustomerId(), "Last record should have highest customer ID (999)");
    }

    @Test
    @DisplayName("Test that sortMergedFile produces records sorted by contract ID descending")
    void testSortMergedFile() throws IOException {
        mergeSortExample.createTestData();
        List<MergeSortExample.Customer> mergedCustomers = mergeSortExample.mergeFiles();

        List<MergeSortExample.Customer> sortedCustomers = mergeSortExample.sortMergedFile(mergedCustomers);

        assertEquals(11, sortedCustomers.size(), "Sorted file should have 11 records");

        assertTrue(Files.exists(Paths.get(SORTED_CONTRACT_ID)), "Sorted output file should exist");

        for (int i = 0; i < sortedCustomers.size() - 1; i++) {
            assertTrue(
                sortedCustomers.get(i).getContractId() >= sortedCustomers.get(i + 1).getContractId(),
                "Records should be sorted by contract ID in descending order"
            );
        }

        assertEquals(12323, sortedCustomers.get(0).getContractId(), "First record should have highest contract ID (12323)");
        assertEquals(247, sortedCustomers.get(10).getContractId(), "Last record should have lowest contract ID (247)");
    }

    @Test
    @DisplayName("Test that fixed-width record parsing works correctly")
    void testCustomerRecordParsing() {
        String fixedWidthRecord = String.format("%05d%-50s%-50s%05d%-25s",
            12345, "TestLastName", "TestFirstName", 67890, "TestComment");

        assertEquals(135, fixedWidthRecord.length(), "Fixed-width record should be 135 characters");

        MergeSortExample.Customer customer = mergeSortExample.parseCustomerRecord(fixedWidthRecord);

        assertEquals(12345, customer.getCustomerId(), "Customer ID should be parsed correctly");
        assertEquals("TestLastName", customer.getLastName(), "Last name should be parsed correctly");
        assertEquals("TestFirstName", customer.getFirstName(), "First name should be parsed correctly");
        assertEquals(67890, customer.getContractId(), "Contract ID should be parsed correctly");
        assertEquals("TestComment", customer.getComment(), "Comment should be parsed correctly");
    }

    @Test
    @DisplayName("Test Customer toString produces correct fixed-width format")
    void testCustomerToString() {
        MergeSortExample.Customer customer = new MergeSortExample.Customer(
            123, "Smith", "John", 456, "Test"
        );

        String output = customer.toString();

        assertEquals(135, output.length(), "Output should be exactly 135 characters");
        assertEquals("00123", output.substring(0, 5), "Customer ID should be zero-padded to 5 digits");
        assertTrue(output.substring(5, 55).startsWith("Smith"), "Last name should start at position 5");
        assertTrue(output.substring(55, 105).startsWith("John"), "First name should start at position 55");
        assertEquals("00456", output.substring(105, 110), "Contract ID should be zero-padded to 5 digits");
        assertTrue(output.substring(110, 135).startsWith("Test"), "Comment should start at position 110");
    }

    @Test
    @DisplayName("Test round-trip: write and read customer record")
    void testRoundTrip() throws IOException {
        MergeSortExample.Customer original = new MergeSortExample.Customer(
            42, "Doe", "Jane", 99999, "Important customer"
        );

        String tempFile = "temp-test-file.txt";
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                writer.write(original.toString());
                writer.newLine();
            }

            List<MergeSortExample.Customer> customers = mergeSortExample.readFile(tempFile);

            assertEquals(1, customers.size(), "Should read one customer");
            MergeSortExample.Customer parsed = customers.get(0);

            assertEquals(original.getCustomerId(), parsed.getCustomerId(), "Customer ID should match");
            assertEquals(original.getLastName(), parsed.getLastName(), "Last name should match");
            assertEquals(original.getFirstName(), parsed.getFirstName(), "First name should match");
            assertEquals(original.getContractId(), parsed.getContractId(), "Contract ID should match");
            assertEquals(original.getComment(), parsed.getComment(), "Comment should match");
        } finally {
            Files.deleteIfExists(Paths.get(tempFile));
        }
    }
}
