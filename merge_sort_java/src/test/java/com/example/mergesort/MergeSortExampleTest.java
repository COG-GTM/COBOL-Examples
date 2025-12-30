package com.example.mergesort;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for MergeSortExample - Java implementation of COBOL merge_sort_test.cbl
 */
public class MergeSortExampleTest {

    private MergeSortExample mergeSortExample;
    private static final String TEST_FILE_1 = "test-file-1.txt";
    private static final String TEST_FILE_2 = "test-file-2.txt";
    private static final String MERGED_FILE = "merge-output.txt";
    private static final String SORTED_CONTRACT_ID_FILE = "sorted-contract-id.txt";

    @Before
    public void setUp() {
        mergeSortExample = new MergeSortExample();
    }

    @After
    public void tearDown() {
        new File(TEST_FILE_1).delete();
        new File(TEST_FILE_2).delete();
        new File(MERGED_FILE).delete();
        new File(SORTED_CONTRACT_ID_FILE).delete();
    }

    @Test
    public void testCustomerRecordCreation() {
        CustomerRecord record = new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1");
        assertEquals(1, record.getCustomerId());
        assertEquals("last-1", record.getCustomerLastName());
        assertEquals("first-1", record.getCustomerFirstName());
        assertEquals(5423, record.getCustomerContractId());
        assertEquals("comment-1", record.getCustomerComment());
    }

    @Test
    public void testCustomerRecordToString() {
        CustomerRecord record = new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1");
        String output = record.toString();
        assertTrue(output.startsWith("00001"));
        assertTrue(output.contains("last-1"));
        assertTrue(output.contains("first-1"));
        assertTrue(output.contains("05423"));
        assertTrue(output.contains("comment-1"));
        assertEquals(135, output.length());
    }

    @Test
    public void testCreateTestData() throws IOException {
        mergeSortExample.createTestData();

        File file1 = new File(TEST_FILE_1);
        File file2 = new File(TEST_FILE_2);

        assertTrue("Test file 1 should exist", file1.exists());
        assertTrue("Test file 2 should exist", file2.exists());

        List<CustomerRecord> eastRecords = mergeSortExample.readRecordsFromFile(TEST_FILE_1);
        List<CustomerRecord> westRecords = mergeSortExample.readRecordsFromFile(TEST_FILE_2);

        assertEquals("East file should have 6 records", 6, eastRecords.size());
        assertEquals("West file should have 5 records", 5, westRecords.size());
    }

    @Test
    public void testEastRecordsData() {
        List<CustomerRecord> eastRecords = mergeSortExample.getEastRecords();

        assertEquals(6, eastRecords.size());

        assertEquals(1, eastRecords.get(0).getCustomerId());
        assertEquals(5423, eastRecords.get(0).getCustomerContractId());

        assertEquals(5, eastRecords.get(1).getCustomerId());
        assertEquals(12323, eastRecords.get(1).getCustomerContractId());

        assertEquals(10, eastRecords.get(2).getCustomerId());
        assertEquals(653, eastRecords.get(2).getCustomerContractId());

        assertEquals(50, eastRecords.get(3).getCustomerId());
        assertEquals(5050, eastRecords.get(3).getCustomerContractId());

        assertEquals(25, eastRecords.get(4).getCustomerId());
        assertEquals(7725, eastRecords.get(4).getCustomerContractId());

        assertEquals(75, eastRecords.get(5).getCustomerId());
        assertEquals(1175, eastRecords.get(5).getCustomerContractId());
    }

    @Test
    public void testWestRecordsData() {
        List<CustomerRecord> westRecords = mergeSortExample.getWestRecords();

        assertEquals(5, westRecords.size());

        assertEquals(999, westRecords.get(0).getCustomerId());
        assertEquals(1610, westRecords.get(0).getCustomerContractId());

        assertEquals(3, westRecords.get(1).getCustomerId());
        assertEquals(3331, westRecords.get(1).getCustomerContractId());

        assertEquals(30, westRecords.get(2).getCustomerId());
        assertEquals(8765, westRecords.get(2).getCustomerContractId());

        assertEquals(85, westRecords.get(3).getCustomerId());
        assertEquals(4567, westRecords.get(3).getCustomerContractId());

        assertEquals(24, westRecords.get(4).getCustomerId());
        assertEquals(247, westRecords.get(4).getCustomerContractId());
    }

    @Test
    public void testMergeAndSortByCustomerId() {
        List<CustomerRecord> allRecords = new ArrayList<>();
        allRecords.addAll(mergeSortExample.getEastRecords());
        allRecords.addAll(mergeSortExample.getWestRecords());

        allRecords.sort(Comparator.comparingInt(CustomerRecord::getCustomerId));

        assertEquals(11, allRecords.size());

        assertEquals(1, allRecords.get(0).getCustomerId());
        assertEquals(3, allRecords.get(1).getCustomerId());
        assertEquals(5, allRecords.get(2).getCustomerId());
        assertEquals(10, allRecords.get(3).getCustomerId());
        assertEquals(24, allRecords.get(4).getCustomerId());
        assertEquals(25, allRecords.get(5).getCustomerId());
        assertEquals(30, allRecords.get(6).getCustomerId());
        assertEquals(50, allRecords.get(7).getCustomerId());
        assertEquals(75, allRecords.get(8).getCustomerId());
        assertEquals(85, allRecords.get(9).getCustomerId());
        assertEquals(999, allRecords.get(10).getCustomerId());
    }

    @Test
    public void testSortByContractIdDescending() {
        List<CustomerRecord> allRecords = new ArrayList<>();
        allRecords.addAll(mergeSortExample.getEastRecords());
        allRecords.addAll(mergeSortExample.getWestRecords());

        allRecords.sort(Comparator.comparingInt(CustomerRecord::getCustomerContractId).reversed());

        assertEquals(11, allRecords.size());

        assertEquals(12323, allRecords.get(0).getCustomerContractId());
        assertEquals(8765, allRecords.get(1).getCustomerContractId());
        assertEquals(7725, allRecords.get(2).getCustomerContractId());
        assertEquals(5423, allRecords.get(3).getCustomerContractId());
        assertEquals(5050, allRecords.get(4).getCustomerContractId());
        assertEquals(4567, allRecords.get(5).getCustomerContractId());
        assertEquals(3331, allRecords.get(6).getCustomerContractId());
        assertEquals(1610, allRecords.get(7).getCustomerContractId());
        assertEquals(1175, allRecords.get(8).getCustomerContractId());
        assertEquals(653, allRecords.get(9).getCustomerContractId());
        assertEquals(247, allRecords.get(10).getCustomerContractId());
    }

    @Test
    public void testFileWriteAndRead() throws IOException {
        List<CustomerRecord> records = new ArrayList<>();
        records.add(new CustomerRecord(1, "test-last", "test-first", 1234, "test-comment"));
        records.add(new CustomerRecord(2, "test-last-2", "test-first-2", 5678, "test-comment-2"));

        String testFile = "test-write-read.txt";
        try {
            mergeSortExample.writeRecordsToFile(testFile, records);
            List<CustomerRecord> readRecords = mergeSortExample.readRecordsFromFile(testFile);

            assertEquals(2, readRecords.size());
            assertEquals(1, readRecords.get(0).getCustomerId());
            assertEquals("test-last", readRecords.get(0).getCustomerLastName());
            assertEquals(2, readRecords.get(1).getCustomerId());
            assertEquals("test-last-2", readRecords.get(1).getCustomerLastName());
        } finally {
            new File(testFile).delete();
        }
    }

    @Test
    public void testFullWorkflow() throws IOException {
        mergeSortExample.run();

        File mergedFile = new File(MERGED_FILE);
        File sortedFile = new File(SORTED_CONTRACT_ID_FILE);

        assertTrue("Merged file should exist", mergedFile.exists());
        assertTrue("Sorted file should exist", sortedFile.exists());

        List<CustomerRecord> mergedRecords = mergeSortExample.readRecordsFromFile(MERGED_FILE);
        assertEquals("Merged file should have 11 records", 11, mergedRecords.size());

        for (int i = 0; i < mergedRecords.size() - 1; i++) {
            assertTrue("Records should be sorted by customer ID ascending",
                    mergedRecords.get(i).getCustomerId() <= mergedRecords.get(i + 1).getCustomerId());
        }

        List<CustomerRecord> sortedRecords = mergeSortExample.readRecordsFromFile(SORTED_CONTRACT_ID_FILE);
        assertEquals("Sorted file should have 11 records", 11, sortedRecords.size());

        for (int i = 0; i < sortedRecords.size() - 1; i++) {
            assertTrue("Records should be sorted by contract ID descending",
                    sortedRecords.get(i).getCustomerContractId() >= sortedRecords.get(i + 1).getCustomerContractId());
        }
    }

    @Test
    public void testCustomerRecordEquality() {
        CustomerRecord record1 = new CustomerRecord(1, "last", "first", 100, "comment");
        CustomerRecord record2 = new CustomerRecord(1, "last", "first", 100, "comment");
        CustomerRecord record3 = new CustomerRecord(2, "last", "first", 100, "comment");

        assertEquals(record1, record2);
        assertNotEquals(record1, record3);
        assertEquals(record1.hashCode(), record2.hashCode());
    }
}
