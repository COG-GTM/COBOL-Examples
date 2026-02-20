package com.mergesort;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MergeSortExampleTest {

    private Path tempDir;
    private MergeSortExample program;

    @Before
    public void setUp() throws IOException {
        tempDir = Files.createTempDirectory("merge-sort-test");
        program = new MergeSortExample(tempDir);
    }

    @After
    public void tearDown() throws IOException {
        Files.walk(tempDir)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Test
    public void testCreateTestData() throws IOException {
        program.createTestData();

        Path file1 = tempDir.resolve("test-file-1.txt");
        Path file2 = tempDir.resolve("test-file-2.txt");

        assertTrue("test-file-1.txt should exist", Files.exists(file1));
        assertTrue("test-file-2.txt should exist", Files.exists(file2));

        List<String> lines1 = Files.readAllLines(file1);
        assertEquals("test-file-1.txt should have 6 records", 6, lines1.size());

        List<String> lines2 = Files.readAllLines(file2);
        assertEquals("test-file-2.txt should have 5 records", 5, lines2.size());
    }

    @Test
    public void testMergeAndDisplayFiles() throws IOException {
        program.createTestData();
        program.mergeAndDisplayFiles();

        Path mergedFile = tempDir.resolve("merge-output.txt");
        assertTrue("merge-output.txt should exist", Files.exists(mergedFile));

        List<CustomerRecord> records = program.readRecordsFromFile(mergedFile);
        assertEquals("Merged file should have 11 records", 11, records.size());

        for (int i = 1; i < records.size(); i++) {
            assertTrue("Records should be sorted by customer ID ascending",
                    records.get(i).getCustomerId() >= records.get(i - 1).getCustomerId());
        }
    }

    @Test
    public void testMergedFileContainsAllRecords() throws IOException {
        program.createTestData();
        program.mergeAndDisplayFiles();

        List<CustomerRecord> merged = program.readRecordsFromFile(tempDir.resolve("merge-output.txt"));
        int[] expectedIds = {1, 3, 5, 10, 24, 25, 30, 50, 75, 85, 999};

        assertEquals(expectedIds.length, merged.size());
        for (int i = 0; i < expectedIds.length; i++) {
            assertEquals("Customer ID at position " + i, expectedIds[i], merged.get(i).getCustomerId());
        }
    }

    @Test
    public void testSortAndDisplayFile() throws IOException {
        program.createTestData();
        program.mergeAndDisplayFiles();
        program.sortAndDisplayFile();

        Path sortedFile = tempDir.resolve("sorted-contract-id.txt");
        assertTrue("sorted-contract-id.txt should exist", Files.exists(sortedFile));

        List<CustomerRecord> records = program.readRecordsFromFile(sortedFile);
        assertEquals("Sorted file should have 11 records", 11, records.size());

        for (int i = 1; i < records.size(); i++) {
            assertTrue("Records should be sorted by contract ID descending",
                    records.get(i).getCustomerContractId()
                            <= records.get(i - 1).getCustomerContractId());
        }
    }

    @Test
    public void testSortedFileContractIdOrder() throws IOException {
        program.createTestData();
        program.mergeAndDisplayFiles();
        program.sortAndDisplayFile();

        List<CustomerRecord> sorted = program.readRecordsFromFile(
                tempDir.resolve("sorted-contract-id.txt"));
        int[] expectedContractIds = {12323, 8765, 7725, 5423, 5050, 4567, 3331, 1610, 1175, 653, 247};

        assertEquals(expectedContractIds.length, sorted.size());
        for (int i = 0; i < expectedContractIds.length; i++) {
            assertEquals("Contract ID at position " + i,
                    expectedContractIds[i], sorted.get(i).getCustomerContractId());
        }
    }

    @Test
    public void testFullRun() throws IOException {
        program.run();

        assertTrue(Files.exists(tempDir.resolve("test-file-1.txt")));
        assertTrue(Files.exists(tempDir.resolve("test-file-2.txt")));
        assertTrue(Files.exists(tempDir.resolve("merge-output.txt")));
        assertTrue(Files.exists(tempDir.resolve("sorted-contract-id.txt")));
    }

    @Test
    public void testCustomerRecordFixedWidth() {
        CustomerRecord record = new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1");
        String fixed = record.toFixedWidthString();

        assertEquals(135, fixed.length());

        CustomerRecord parsed = CustomerRecord.fromFixedWidthString(fixed);
        assertEquals(record, parsed);
    }

    @Test
    public void testEastFileRecordValues() throws IOException {
        program.createTestData();
        List<CustomerRecord> eastRecords = program.readRecordsFromFile(
                tempDir.resolve("test-file-1.txt"));

        assertEquals(1, eastRecords.get(0).getCustomerId());
        assertEquals("last-1", eastRecords.get(0).getCustomerLastName());
        assertEquals("first-1", eastRecords.get(0).getCustomerFirstName());
        assertEquals(5423, eastRecords.get(0).getCustomerContractId());
        assertEquals("comment-1", eastRecords.get(0).getCustomerComment());

        assertEquals(75, eastRecords.get(5).getCustomerId());
        assertEquals("last-75", eastRecords.get(5).getCustomerLastName());
        assertEquals(1175, eastRecords.get(5).getCustomerContractId());
    }

    @Test
    public void testWestFileRecordValues() throws IOException {
        program.createTestData();
        List<CustomerRecord> westRecords = program.readRecordsFromFile(
                tempDir.resolve("test-file-2.txt"));

        assertEquals(999, westRecords.get(0).getCustomerId());
        assertEquals("last-999", westRecords.get(0).getCustomerLastName());
        assertEquals("first-999", westRecords.get(0).getCustomerFirstName());
        assertEquals(1610, westRecords.get(0).getCustomerContractId());
        assertEquals("comment-99", westRecords.get(0).getCustomerComment());

        assertEquals(24, westRecords.get(4).getCustomerId());
        assertEquals(247, westRecords.get(4).getCustomerContractId());
    }
}
