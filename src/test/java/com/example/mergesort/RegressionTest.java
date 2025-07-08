package com.example.mergesort;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test suite to verify the Java implementation produces the same results
 * as the COBOL implementation for the exact same test data.
 */
class RegressionTest {
    
    private final MergeSortProcessor processor = new MergeSortProcessor();
    
    @Test
    void testRegression_ExactCOBOLTestData() {
        List<CustomerRecord> east = TestDataFactory.createEastRegionData();
        List<CustomerRecord> west = TestDataFactory.createWestRegionData();
        
        MergeSortProcessor.ProcessingResult result = processor.processRecords(east, west);
        
        List<CustomerRecord> merged = result.getMergedRecords();
        List<CustomerRecord> sorted = result.getSortedRecords();
        
        assertEquals(11, merged.size());
        assertEquals(11, sorted.size());
        
        int[] expectedMergedIds = {1, 3, 5, 10, 24, 25, 30, 50, 75, 85, 999};
        for (int i = 0; i < expectedMergedIds.length; i++) {
            assertEquals(expectedMergedIds[i], merged.get(i).getCustomerId(), 
                "Merged record " + i + " has wrong customer ID");
        }
        
        int[] expectedSortedContractIds = {12323, 8765, 7725, 5423, 5050, 4567, 3331, 1610, 1175, 653, 247};
        for (int i = 0; i < expectedSortedContractIds.length; i++) {
            assertEquals(expectedSortedContractIds[i], sorted.get(i).getCustomerContractId(),
                "Sorted record " + i + " has wrong contract ID");
        }
    }
    
    @Test
    void testRegression_MergedRecordOrder() {
        List<CustomerRecord> east = TestDataFactory.createEastRegionData();
        List<CustomerRecord> west = TestDataFactory.createWestRegionData();
        
        List<CustomerRecord> merged = processor.mergeByCustomerId(east, west);
        
        String[] expectedLastNames = {
            "last-1", "last-03", "last-5", "last-10", "last-24", 
            "last-25", "last-30", "last-50", "last-75", "last-85", "last-999"
        };
        
        for (int i = 0; i < expectedLastNames.length; i++) {
            assertEquals(expectedLastNames[i], merged.get(i).getCustomerLastName(),
                "Merged record " + i + " has wrong last name");
        }
    }
    
    @Test
    void testRegression_SortedRecordOrder() {
        List<CustomerRecord> east = TestDataFactory.createEastRegionData();
        List<CustomerRecord> west = TestDataFactory.createWestRegionData();
        
        List<CustomerRecord> merged = processor.mergeByCustomerId(east, west);
        List<CustomerRecord> sorted = processor.sortByContractIdDescending(merged);
        
        String[] expectedLastNames = {
            "last-5", "last-30", "last-25", "last-1", "last-50", 
            "last-85", "last-03", "last-999", "last-75", "last-10", "last-24"
        };
        
        for (int i = 0; i < expectedLastNames.length; i++) {
            assertEquals(expectedLastNames[i], sorted.get(i).getCustomerLastName(),
                "Sorted record " + i + " has wrong last name");
        }
    }
    
    @Test
    void testRegression_RecordFormatting() {
        CustomerRecord record = new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1");
        String formatted = record.toString();
        
        assertEquals(135, formatted.length());
        assertTrue(formatted.startsWith("00001"));
        assertTrue(formatted.contains("last-1"));
        assertTrue(formatted.contains("first-1"));
        assertTrue(formatted.contains("05423"));
        assertTrue(formatted.contains("comment-1"));
    }
    
    @Test
    void testRegression_StabilityOfSorting() {
        List<CustomerRecord> duplicates = TestDataFactory.createDuplicateIdData();
        
        List<CustomerRecord> merged1 = processor.mergeByCustomerId(duplicates, TestDataFactory.createEmptyData());
        List<CustomerRecord> merged2 = processor.mergeByCustomerId(duplicates, TestDataFactory.createEmptyData());
        
        assertEquals(merged1.size(), merged2.size());
        for (int i = 0; i < merged1.size(); i++) {
            assertEquals(merged1.get(i), merged2.get(i));
        }
    }
    
    @Test
    void testRegression_EmptyInputHandling() {
        List<CustomerRecord> empty = TestDataFactory.createEmptyData();
        List<CustomerRecord> east = TestDataFactory.createEastRegionData();
        
        List<CustomerRecord> result1 = processor.mergeByCustomerId(empty, empty);
        List<CustomerRecord> result2 = processor.mergeByCustomerId(east, empty);
        List<CustomerRecord> result3 = processor.mergeByCustomerId(empty, east);
        
        assertTrue(result1.isEmpty());
        assertEquals(6, result2.size());
        assertEquals(6, result3.size());
        
        assertEquals(result2, result3);
    }
}
