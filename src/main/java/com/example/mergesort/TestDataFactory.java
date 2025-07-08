package com.example.mergesort;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory class to create test data matching the COBOL implementation.
 * Data matches the hardcoded test data from merge_sort_test.cbl lines 185-334.
 */
public class TestDataFactory {
    
    /**
     * Creates East region test data matching COBOL lines 185-259.
     * Contains 6 records with customer IDs: 1, 5, 10, 25, 50, 75
     */
    public static List<CustomerRecord> createEastRegionData() {
        List<CustomerRecord> eastRegion = new ArrayList<>();
        
        eastRegion.add(new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"));
        eastRegion.add(new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"));
        eastRegion.add(new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"));
        eastRegion.add(new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"));
        eastRegion.add(new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"));
        eastRegion.add(new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75"));
        
        return eastRegion;
    }
    
    /**
     * Creates West region test data matching COBOL lines 272-334.
     * Contains 5 records with customer IDs: 999, 3, 30, 85, 24
     */
    public static List<CustomerRecord> createWestRegionData() {
        List<CustomerRecord> westRegion = new ArrayList<>();
        
        westRegion.add(new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"));
        westRegion.add(new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"));
        westRegion.add(new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"));
        westRegion.add(new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"));
        westRegion.add(new CustomerRecord(24, "last-24", "first-24", 247, "comment-24"));
        
        return westRegion;
    }
    
    /**
     * Creates empty test data for edge case testing.
     */
    public static List<CustomerRecord> createEmptyData() {
        return new ArrayList<>();
    }
    
    /**
     * Creates single record test data for edge case testing.
     */
    public static List<CustomerRecord> createSingleRecordData() {
        List<CustomerRecord> singleRecord = new ArrayList<>();
        singleRecord.add(new CustomerRecord(100, "single-last", "single-first", 9999, "single-comment"));
        return singleRecord;
    }
    
    /**
     * Creates test data with duplicate customer IDs for edge case testing.
     */
    public static List<CustomerRecord> createDuplicateIdData() {
        List<CustomerRecord> duplicates = new ArrayList<>();
        duplicates.add(new CustomerRecord(1, "duplicate-1a", "first-1a", 1000, "comment-1a"));
        duplicates.add(new CustomerRecord(1, "duplicate-1b", "first-1b", 2000, "comment-1b"));
        duplicates.add(new CustomerRecord(2, "duplicate-2", "first-2", 3000, "comment-2"));
        return duplicates;
    }
    
    /**
     * Creates large test data set for performance testing.
     */
    public static List<CustomerRecord> createLargeDataSet(int size) {
        List<CustomerRecord> largeData = new ArrayList<>();
        
        for (int i = 1; i <= size; i++) {
            largeData.add(new CustomerRecord(
                i,
                "last-" + i,
                "first-" + i,
                (i * 17) % 10000,
                "comment-" + i
            ));
        }
        
        return largeData;
    }
}
