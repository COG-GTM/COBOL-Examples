package com.example.mergesort;

import java.io.IOException;

public class MergeSortExample {
    
    public static void main(String[] args) {
        FileMergeSorter sorter = new FileMergeSorter();
        
        try {
            sorter.createTestDataEast("test-file-1.txt");
            sorter.createTestDataWest("test-file-2.txt");
            
            sorter.mergeFiles("test-file-1.txt", "test-file-2.txt", "merge-output.txt");
            
            sorter.sortFile("merge-output.txt", "sorted-contract-id.txt");
            
            System.out.println("Done.");
            
        } catch (IOException e) {
            System.err.println("Error processing files: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
