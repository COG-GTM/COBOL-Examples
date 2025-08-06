package com.example.mergesort;

import java.util.*;
import java.io.*;

public class MergeSortExample {
    
    public static void main(String[] args) {
        MergeSortExample example = new MergeSortExample();
        
        List<CustomerRecord> testFile1 = example.createTestDataFile1();
        List<CustomerRecord> testFile2 = example.createTestDataFile2();
        
        example.mergeAndDisplayFiles(testFile1, testFile2);
        
        List<CustomerRecord> mergedRecords = example.mergeFiles(testFile1, testFile2);
        example.sortAndDisplayFile(mergedRecords);
        
        System.out.println("Done.");
    }
    
    public List<CustomerRecord> mergeFiles(List<CustomerRecord> file1, List<CustomerRecord> file2) {
        List<CustomerRecord> merged = new ArrayList<>();
        
        List<CustomerRecord> sortedFile1 = new ArrayList<>(file1);
        List<CustomerRecord> sortedFile2 = new ArrayList<>(file2);
        
        sortedFile1.sort(Comparator.comparing(CustomerRecord::getCustomerId));
        sortedFile2.sort(Comparator.comparing(CustomerRecord::getCustomerId));
        
        int i = 0, j = 0;
        
        while (i < sortedFile1.size() && j < sortedFile2.size()) {
            CustomerRecord record1 = sortedFile1.get(i);
            CustomerRecord record2 = sortedFile2.get(j);
            
            if (record1.getCustomerId() <= record2.getCustomerId()) {
                merged.add(record1);
                i++;
            } else {
                merged.add(record2);
                j++;
            }
        }
        
        while (i < sortedFile1.size()) {
            merged.add(sortedFile1.get(i));
            i++;
        }
        
        while (j < sortedFile2.size()) {
            merged.add(sortedFile2.get(j));
            j++;
        }
        
        return merged;
    }
    
    public void mergeAndDisplayFiles(List<CustomerRecord> file1, List<CustomerRecord> file2) {
        System.out.println("Merging and sorting files...");
        
        List<CustomerRecord> merged = mergeFiles(file1, file2);
        
        for (CustomerRecord record : merged) {
            System.out.println(record);
        }
    }
    
    public void sortAndDisplayFile(List<CustomerRecord> records) {
        System.out.println("Sorting merged file on descending contract id....");
        
        List<CustomerRecord> sorted = new ArrayList<>(records);
        sorted.sort(Comparator.comparing(CustomerRecord::getContractId).reversed());
        
        for (CustomerRecord record : sorted) {
            System.out.println(record);
        }
    }
    
    public List<CustomerRecord> createTestDataFile1() {
        System.out.println("Creating test data files...");
        
        List<CustomerRecord> testFile1 = new ArrayList<>();
        
        testFile1.add(new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"));
        testFile1.add(new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"));
        testFile1.add(new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"));
        testFile1.add(new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"));
        testFile1.add(new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"));
        testFile1.add(new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75"));
        
        return testFile1;
    }
    
    public List<CustomerRecord> createTestDataFile2() {
        List<CustomerRecord> testFile2 = new ArrayList<>();
        
        testFile2.add(new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"));
        testFile2.add(new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"));
        testFile2.add(new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"));
        testFile2.add(new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"));
        testFile2.add(new CustomerRecord(24, "last-24", "first-24", 247, "comment-24"));
        
        return testFile2;
    }
}
