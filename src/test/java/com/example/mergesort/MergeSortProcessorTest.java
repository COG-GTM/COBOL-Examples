package com.example.mergesort;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for MergeSortProcessor covering normal operation,
 * error handling, edge cases, and performance testing.
 */
class MergeSortProcessorTest {
    
    private MergeSortProcessor processor;
    
    @BeforeEach
    void setUp() {
        processor = new MergeSortProcessor();
    }
    
    @Test
    void testMergeByCustomerId_NormalOperation() {
        List<CustomerRecord> east = TestDataFactory.createEastRegionData();
        List<CustomerRecord> west = TestDataFactory.createWestRegionData();
        
        List<CustomerRecord> merged = processor.mergeByCustomerId(east, west);
        
        assertEquals(11, merged.size());
        
        int[] expectedIds = {1, 3, 5, 10, 24, 25, 30, 50, 75, 85, 999};
        for (int i = 0; i < expectedIds.length; i++) {
            assertEquals(expectedIds[i], merged.get(i).getCustomerId());
        }
    }
    
    @Test
    void testSortByContractIdDescending_NormalOperation() {
        List<CustomerRecord> testData = TestDataFactory.createEastRegionData();
        
        List<CustomerRecord> sorted = processor.sortByContractIdDescending(testData);
        
        assertEquals(6, sorted.size());
        
        int[] expectedContractIds = {12323, 7725, 5423, 5050, 1175, 653};
        for (int i = 0; i < expectedContractIds.length; i++) {
            assertEquals(expectedContractIds[i], sorted.get(i).getCustomerContractId());
        }
    }
    
    @Test
    void testProcessRecords_CompleteWorkflow() {
        List<CustomerRecord> east = TestDataFactory.createEastRegionData();
        List<CustomerRecord> west = TestDataFactory.createWestRegionData();
        
        MergeSortProcessor.ProcessingResult result = processor.processRecords(east, west);
        
        assertNotNull(result);
        assertEquals(11, result.getMergedRecords().size());
        assertEquals(11, result.getSortedRecords().size());
        assertTrue(result.getProcessingTimeNanos() > 0);
        
        int[] expectedMergedIds = {1, 3, 5, 10, 24, 25, 30, 50, 75, 85, 999};
        for (int i = 0; i < expectedMergedIds.length; i++) {
            assertEquals(expectedMergedIds[i], result.getMergedRecords().get(i).getCustomerId());
        }
        
        int[] expectedSortedContractIds = {12323, 8765, 7725, 5423, 5050, 4567, 3331, 1610, 1175, 653, 247};
        for (int i = 0; i < expectedSortedContractIds.length; i++) {
            assertEquals(expectedSortedContractIds[i], result.getSortedRecords().get(i).getCustomerContractId());
        }
    }
    
    @Test
    void testMergeByCustomerId_NullInputs() {
        List<CustomerRecord> result1 = processor.mergeByCustomerId(null, null);
        assertTrue(result1.isEmpty());
        
        List<CustomerRecord> east = TestDataFactory.createEastRegionData();
        List<CustomerRecord> result2 = processor.mergeByCustomerId(east, null);
        assertEquals(6, result2.size());
        
        List<CustomerRecord> west = TestDataFactory.createWestRegionData();
        List<CustomerRecord> result3 = processor.mergeByCustomerId(null, west);
        assertEquals(5, result3.size());
    }
    
    @Test
    void testSortByContractIdDescending_NullInput() {
        List<CustomerRecord> result = processor.sortByContractIdDescending(null);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testMergeByCustomerId_EmptyLists() {
        List<CustomerRecord> empty1 = new ArrayList<>();
        List<CustomerRecord> empty2 = new ArrayList<>();
        
        List<CustomerRecord> result = processor.mergeByCustomerId(empty1, empty2);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testMergeByCustomerId_SingleRecord() {
        List<CustomerRecord> single = TestDataFactory.createSingleRecordData();
        List<CustomerRecord> empty = new ArrayList<>();
        
        List<CustomerRecord> result = processor.mergeByCustomerId(single, empty);
        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getCustomerId());
    }
    
    @Test
    void testMergeByCustomerId_DuplicateIds() {
        List<CustomerRecord> duplicates = TestDataFactory.createDuplicateIdData();
        List<CustomerRecord> empty = new ArrayList<>();
        
        List<CustomerRecord> result = processor.mergeByCustomerId(duplicates, empty);
        assertEquals(3, result.size());
        
        assertEquals(1, result.get(0).getCustomerId());
        assertEquals(1, result.get(1).getCustomerId());
        assertEquals(2, result.get(2).getCustomerId());
    }
    
    @Test
    void testFileIO_WriteAndRead(@TempDir Path tempDir) throws IOException {
        List<CustomerRecord> testData = TestDataFactory.createEastRegionData();
        String filename = tempDir.resolve("test-output.txt").toString();
        
        processor.writeToFile(testData, filename);
        List<CustomerRecord> readData = processor.readFromFile(filename);
        
        assertEquals(testData.size(), readData.size());
        for (int i = 0; i < testData.size(); i++) {
            assertEquals(testData.get(i), readData.get(i));
        }
    }
    
    @Test
    void testFileIO_ReadNonexistentFile() throws IOException {
        List<CustomerRecord> result = processor.readFromFile("nonexistent-file.txt");
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testFileIO_WriteToInvalidPath() {
        List<CustomerRecord> testData = TestDataFactory.createEastRegionData();
        
        assertThrows(IOException.class, () -> {
            processor.writeToFile(testData, "/invalid/path/file.txt");
        });
    }
    
    @Test
    void testProcessingResult_TimingMethods() {
        List<CustomerRecord> east = TestDataFactory.createEastRegionData();
        List<CustomerRecord> west = TestDataFactory.createWestRegionData();
        
        MergeSortProcessor.ProcessingResult result = processor.processRecords(east, west);
        
        assertTrue(result.getProcessingTimeNanos() > 0);
        assertTrue(result.getProcessingTimeMillis() > 0);
        assertTrue(result.getProcessingTimeSeconds() > 0);
        
        assertEquals(result.getProcessingTimeNanos() / 1_000_000.0, result.getProcessingTimeMillis(), 0.001);
        assertEquals(result.getProcessingTimeNanos() / 1_000_000_000.0, result.getProcessingTimeSeconds(), 0.000001);
    }
}
