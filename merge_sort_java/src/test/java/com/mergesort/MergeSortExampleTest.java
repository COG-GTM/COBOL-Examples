package com.mergesort;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MergeSortExampleTest {

    private Path tempDir;
    private MergeSortExample app;

    @Before
    public void setUp() throws IOException {
        tempDir = Files.createTempDirectory("merge-sort-test");
        app = new MergeSortExample(tempDir);
    }

    @After
    public void tearDown() throws IOException {
        Files.walk(tempDir)
                .sorted(java.util.Comparator.reverseOrder())
                .forEach(p -> {
                    try { Files.deleteIfExists(p); } catch (IOException ignored) {}
                });
    }

    @Test
    public void testCreateTestData() throws IOException {
        app.createTestData();

        Path file1 = tempDir.resolve("test-file-1.txt");
        Path file2 = tempDir.resolve("test-file-2.txt");
        assertTrue("test-file-1.txt should exist", Files.exists(file1));
        assertTrue("test-file-2.txt should exist", Files.exists(file2));

        List<String> lines1 = Files.readAllLines(file1);
        assertEquals("East file should have 6 records", 6, lines1.size());

        List<String> lines2 = Files.readAllLines(file2);
        assertEquals("West file should have 5 records", 5, lines2.size());
    }

    @Test
    public void testMergeAndDisplayFiles() throws IOException {
        app.createTestData();
        app.mergeAndDisplayFiles();

        Path mergedFile = tempDir.resolve("merge-output.txt");
        assertTrue("merge-output.txt should exist", Files.exists(mergedFile));

        List<CustomerRecord> records = app.readRecords(mergedFile);
        assertEquals("Merged file should have 11 records", 11, records.size());

        for (int i = 1; i < records.size(); i++) {
            assertTrue("Records should be sorted by customer ID ascending",
                    records.get(i).getCustomerId() >= records.get(i - 1).getCustomerId());
        }

        assertEquals(1, records.get(0).getCustomerId());
        assertEquals(3, records.get(1).getCustomerId());
        assertEquals(5, records.get(2).getCustomerId());
        assertEquals(999, records.get(10).getCustomerId());
    }

    @Test
    public void testSortAndDisplayFile() throws IOException {
        app.createTestData();
        app.mergeAndDisplayFiles();
        app.sortAndDisplayFile();

        Path sortedFile = tempDir.resolve("sorted-contract-id.txt");
        assertTrue("sorted-contract-id.txt should exist", Files.exists(sortedFile));

        List<CustomerRecord> records = app.readRecords(sortedFile);
        assertEquals("Sorted file should have 11 records", 11, records.size());

        for (int i = 1; i < records.size(); i++) {
            assertTrue("Records should be sorted by contract ID descending",
                    records.get(i).getContractId() <= records.get(i - 1).getContractId());
        }

        assertEquals(12323, records.get(0).getContractId());
        assertEquals(247, records.get(10).getContractId());
    }

    @Test
    public void testCustomerRecordRoundTrip() {
        CustomerRecord original = new CustomerRecord(42, "Smith", "John", 12345, "test-comment");
        String fileString = original.toFileString();
        CustomerRecord parsed = CustomerRecord.fromFileString(fileString);

        assertEquals(original.getCustomerId(), parsed.getCustomerId());
        assertEquals(original.getLastName(), parsed.getLastName());
        assertEquals(original.getFirstName(), parsed.getFirstName());
        assertEquals(original.getContractId(), parsed.getContractId());
        assertEquals(original.getComment(), parsed.getComment());
    }

    @Test
    public void testFullRun() throws IOException {
        app.run();

        assertTrue(Files.exists(tempDir.resolve("test-file-1.txt")));
        assertTrue(Files.exists(tempDir.resolve("test-file-2.txt")));
        assertTrue(Files.exists(tempDir.resolve("merge-output.txt")));
        assertTrue(Files.exists(tempDir.resolve("sorted-contract-id.txt")));
    }
}
