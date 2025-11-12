package com.cobol.examples;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit and integration tests for MergeSortExample.
 * Tests merge and sort functionality to ensure it matches COBOL behavior.
 */
public class MergeSortExampleTest {

    private String originalDir;

    @BeforeEach
    public void setUp() {
        originalDir = System.getProperty("user.dir");
    }

    @AfterEach
    public void tearDown() {
        System.setProperty("user.dir", originalDir);
        
        deleteFileIfExists("test-file-1.txt");
        deleteFileIfExists("test-file-2.txt");
        deleteFileIfExists("merge-output.txt");
        deleteFileIfExists("sorted-contract-id.txt");
        deleteFileIfExists("work-temp.txt");
    }

    @Test
    public void testCreateTestData() throws IOException {
        MergeSortExample example = new MergeSortExample();
        example.run();

        assertTrue(new File("test-file-1.txt").exists());
        assertTrue(new File("test-file-2.txt").exists());

        List<String> file1Lines = Files.readAllLines(Paths.get("test-file-1.txt"));
        assertEquals(6, file1Lines.size());

        List<String> file2Lines = Files.readAllLines(Paths.get("test-file-2.txt"));
        assertEquals(5, file2Lines.size());
    }

    @Test
    public void testMergeOutput() throws IOException {
        MergeSortExample example = new MergeSortExample();
        example.run();

        assertTrue(new File("merge-output.txt").exists());

        List<String> mergedLines = Files.readAllLines(Paths.get("merge-output.txt"));
        assertEquals(11, mergedLines.size());

        List<CustomerRecord> records = new ArrayList<>();
        for (String line : mergedLines) {
            records.add(CustomerRecord.fromString(line));
        }

        for (int i = 0; i < records.size() - 1; i++) {
            assertTrue(records.get(i).getCustomerId() <= records.get(i + 1).getCustomerId(),
                "Records should be sorted by customer ID in ascending order");
        }
    }

    @Test
    public void testSortOutput() throws IOException {
        MergeSortExample example = new MergeSortExample();
        example.run();

        assertTrue(new File("sorted-contract-id.txt").exists());

        List<String> sortedLines = Files.readAllLines(Paths.get("sorted-contract-id.txt"));
        assertEquals(11, sortedLines.size());

        List<CustomerRecord> records = new ArrayList<>();
        for (String line : sortedLines) {
            records.add(CustomerRecord.fromString(line));
        }

        for (int i = 0; i < records.size() - 1; i++) {
            assertTrue(records.get(i).getContractId() >= records.get(i + 1).getContractId(),
                "Records should be sorted by contract ID in descending order");
        }
    }

    @Test
    public void testMergeOutputContainsAllRecords() throws IOException {
        MergeSortExample example = new MergeSortExample();
        example.run();

        List<String> mergedLines = Files.readAllLines(Paths.get("merge-output.txt"));
        List<CustomerRecord> records = new ArrayList<>();
        for (String line : mergedLines) {
            records.add(CustomerRecord.fromString(line));
        }

        Set<Integer> customerIds = new HashSet<>();
        for (CustomerRecord record : records) {
            customerIds.add(record.getCustomerId());
        }

        assertTrue(customerIds.contains(1));
        assertTrue(customerIds.contains(3));
        assertTrue(customerIds.contains(5));
        assertTrue(customerIds.contains(10));
        assertTrue(customerIds.contains(24));
        assertTrue(customerIds.contains(25));
        assertTrue(customerIds.contains(30));
        assertTrue(customerIds.contains(50));
        assertTrue(customerIds.contains(75));
        assertTrue(customerIds.contains(85));
        assertTrue(customerIds.contains(999));
    }

    @Test
    public void testExpectedCustomerData() throws IOException {
        MergeSortExample example = new MergeSortExample();
        example.run();

        List<String> mergedLines = Files.readAllLines(Paths.get("merge-output.txt"));
        List<CustomerRecord> records = new ArrayList<>();
        for (String line : mergedLines) {
            records.add(CustomerRecord.fromString(line));
        }

        CustomerRecord firstRecord = records.get(0);
        assertEquals(1, firstRecord.getCustomerId());
        assertEquals("last-1", firstRecord.getLastName());
        assertEquals("first-1", firstRecord.getFirstName());
        assertEquals(5423, firstRecord.getContractId());
        assertEquals("comment-1", firstRecord.getComment());
    }

    @Test
    public void testHighestContractIdFirst() throws IOException {
        MergeSortExample example = new MergeSortExample();
        example.run();

        List<String> sortedLines = Files.readAllLines(Paths.get("sorted-contract-id.txt"));
        List<CustomerRecord> records = new ArrayList<>();
        for (String line : sortedLines) {
            records.add(CustomerRecord.fromString(line));
        }

        CustomerRecord firstRecord = records.get(0);
        assertEquals(12323, firstRecord.getContractId());
        assertEquals(5, firstRecord.getCustomerId());
    }

    private void deleteFileIfExists(String filename) {
        try {
            Files.deleteIfExists(Paths.get(filename));
        } catch (IOException e) {
        }
    }
}
