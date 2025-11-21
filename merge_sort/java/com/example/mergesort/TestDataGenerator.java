package com.example.mergesort;

import java.util.ArrayList;
import java.util.List;

public class TestDataGenerator {
    
    public static List<CustomerRecord> generateEastRegionData() {
        List<CustomerRecord> records = new ArrayList<>();
        
        records.add(new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"));
        records.add(new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"));
        records.add(new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"));
        records.add(new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"));
        records.add(new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"));
        records.add(new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75"));
        
        return records;
    }
    
    public static List<CustomerRecord> generateWestRegionData() {
        List<CustomerRecord> records = new ArrayList<>();
        
        records.add(new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"));
        records.add(new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"));
        records.add(new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"));
        records.add(new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"));
        records.add(new CustomerRecord(24, "last-24", "first-24", 247, "comment-24"));
        
        return records;
    }
}
