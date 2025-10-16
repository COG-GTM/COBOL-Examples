package com.cobol.examples;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MergeSortProgramTest {
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    public void setup() {
        System.setOut(new PrintStream(outContent));
    }
    
    @AfterEach
    public void cleanup() throws IOException {
        System.setOut(originalOut);
        
        deleteFileIfExists("test-file-1.txt");
        deleteFileIfExists("test-file-2.txt");
        deleteFileIfExists("merge-output.txt");
        deleteFileIfExists("sorted-contract-id.txt");
    }
    
    private void deleteFileIfExists(String filename) throws IOException {
        Path path = Paths.get(filename);
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }
    
    @Test
    public void testCreateTestData() throws IOException {
        MergeSortProgram.main(new String[]{});
        
        assertTrue(Files.exists(Paths.get("test-file-1.txt")));
        assertTrue(Files.exists(Paths.get("test-file-2.txt")));
        
        List<CustomerRecord> eastRecords = FixedWidthFileIO.readRecords("test-file-1.txt");
        List<CustomerRecord> westRecords = FixedWidthFileIO.readRecords("test-file-2.txt");
        assertEquals(6, eastRecords.size());
        assertEquals(5, westRecords.size());
    }
    
    @Test
    public void testMergeOutput() throws IOException {
        MergeSortProgram.main(new String[]{});
        
        List<CustomerRecord> mergedRecords = FixedWidthFileIO.readRecords("merge-output.txt");
        
        assertEquals(11, mergedRecords.size());
        
        for (int i = 0; i < mergedRecords.size() - 1; i++) {
            assertTrue(mergedRecords.get(i).getCustomerId() <= mergedRecords.get(i + 1).getCustomerId(),
                "Records should be sorted by customer ID ascending");
        }
        
        assertEquals(1, mergedRecords.get(0).getCustomerId());
        assertEquals(999, mergedRecords.get(10).getCustomerId());
    }
    
    @Test
    public void testSortOutput() throws IOException {
        MergeSortProgram.main(new String[]{});
        
        List<CustomerRecord> sortedRecords = FixedWidthFileIO.readRecords("sorted-contract-id.txt");
        
        assertEquals(11, sortedRecords.size());
        
        for (int i = 0; i < sortedRecords.size() - 1; i++) {
            assertTrue(sortedRecords.get(i).getContractId() >= sortedRecords.get(i + 1).getContractId(),
                "Records should be sorted by contract ID descending");
        }
        
        assertEquals(12323, sortedRecords.get(0).getContractId());
        assertEquals(247, sortedRecords.get(10).getContractId());
    }
    
    @Test
    public void testFixedWidthFormatting() throws IOException {
        MergeSortProgram.main(new String[]{});
        
        List<String> lines = Files.readAllLines(Paths.get("merge-output.txt"));
        
        for (String line : lines) {
            assertEquals(line, line.stripTrailing(), "Lines should not have trailing spaces (matches COBOL behavior)");
            assertTrue(line.length() >= 110, "Each line should be at least 110 characters (5+50+50+5)");
        }
    }
    
    @Test
    public void testCustomerRecordParsing() {
        String line = "00001last-1                                            first-1                                           05423comment-1                ";
        CustomerRecord record = CustomerRecord.parseFromFixedWidth(line);
        
        assertEquals(1, record.getCustomerId());
        assertEquals("last-1                                            ", record.getLastName());
        assertEquals("first-1                                           ", record.getFirstName());
        assertEquals(5423, record.getContractId());
        assertEquals("comment-1                ", record.getComment());
    }
    
    @Test
    public void testCustomerRecordFormatting() {
        CustomerRecord record = new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1");
        String formatted = record.toFixedWidth();
        
        assertEquals(135, formatted.length());
        assertEquals("00001", formatted.substring(0, 5));
        assertEquals("last-1", formatted.substring(5, 11).trim());
        assertEquals("first-1", formatted.substring(55, 62).trim());
        assertEquals("05423", formatted.substring(105, 110));
        assertEquals("comment-1", formatted.substring(110, 119).trim());
    }
    
    @Test
    public void testMergeOrderCorrectness() throws IOException {
        MergeSortProgram.main(new String[]{});
        
        List<CustomerRecord> mergedRecords = FixedWidthFileIO.readRecords("merge-output.txt");
        
        int[] expectedCustomerIds = {1, 3, 5, 10, 24, 25, 30, 50, 75, 85, 999};
        
        for (int i = 0; i < expectedCustomerIds.length; i++) {
            assertEquals(expectedCustomerIds[i], mergedRecords.get(i).getCustomerId(),
                "Customer ID at position " + i + " should match expected order");
        }
    }
    
    @Test
    public void testSortOrderCorrectness() throws IOException {
        MergeSortProgram.main(new String[]{});
        
        List<CustomerRecord> sortedRecords = FixedWidthFileIO.readRecords("sorted-contract-id.txt");
        
        int[] expectedContractIds = {12323, 8765, 7725, 5423, 5050, 4567, 3331, 1610, 1175, 653, 247};
        
        for (int i = 0; i < expectedContractIds.length; i++) {
            assertEquals(expectedContractIds[i], sortedRecords.get(i).getContractId(),
                "Contract ID at position " + i + " should match expected order");
        }
    }
}
