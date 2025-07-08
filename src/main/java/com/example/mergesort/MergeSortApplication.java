package com.example.mergesort;

import java.io.IOException;
import java.util.List;

/**
 * Main application class that demonstrates the merge sort functionality.
 * Matches the console output format from the COBOL implementation.
 */
public class MergeSortApplication {
    
    public static void main(String[] args) {
        MergeSortApplication app = new MergeSortApplication();
        
        if (args.length > 0 && "file".equals(args[0])) {
            app.runWithFileIO();
        } else {
            app.runInMemory();
        }
    }
    
    /**
     * Runs the application using in-memory data processing.
     */
    public void runInMemory() {
        System.out.println("Creating test data...");
        
        List<CustomerRecord> eastRegion = TestDataFactory.createEastRegionData();
        List<CustomerRecord> westRegion = TestDataFactory.createWestRegionData();
        
        MergeSortProcessor processor = new MergeSortProcessor();
        
        System.out.println("Merging and sorting files...");
        MergeSortProcessor.ProcessingResult result = processor.processRecords(eastRegion, westRegion);
        
        System.out.println("Merged records (sorted by ascending customer ID):");
        for (CustomerRecord record : result.getMergedRecords()) {
            System.out.println(record);
        }
        
        System.out.println("\nSorting merged file on descending contract id....");
        System.out.println("Final sorted records (by descending contract ID):");
        for (CustomerRecord record : result.getSortedRecords()) {
            System.out.println(record);
        }
        
        System.out.printf("\nProcessing completed in %.3f milliseconds%n", result.getProcessingTimeMillis());
        System.out.println("Done.");
    }
    
    /**
     * Runs the application using file I/O for compatibility with COBOL approach.
     */
    public void runWithFileIO() {
        try {
            System.out.println("Creating test data files...");
            
            List<CustomerRecord> eastRegion = TestDataFactory.createEastRegionData();
            List<CustomerRecord> westRegion = TestDataFactory.createWestRegionData();
            
            MergeSortProcessor processor = new MergeSortProcessor();
            
            processor.writeToFile(eastRegion, "test-file-1.txt");
            processor.writeToFile(westRegion, "test-file-2.txt");
            
            List<CustomerRecord> eastFromFile = processor.readFromFile("test-file-1.txt");
            List<CustomerRecord> westFromFile = processor.readFromFile("test-file-2.txt");
            
            System.out.println("Merging and sorting files...");
            MergeSortProcessor.ProcessingResult result = processor.processRecords(eastFromFile, westFromFile);
            
            processor.writeToFile(result.getMergedRecords(), "merge-output.txt");
            processor.writeToFile(result.getSortedRecords(), "sorted-contract-id.txt");
            
            System.out.println("Merged records (sorted by ascending customer ID):");
            for (CustomerRecord record : result.getMergedRecords()) {
                System.out.println(record);
            }
            
            System.out.println("\nSorting merged file on descending contract id....");
            System.out.println("Final sorted records (by descending contract ID):");
            for (CustomerRecord record : result.getSortedRecords()) {
                System.out.println(record);
            }
            
            System.out.printf("\nProcessing completed in %.3f milliseconds%n", result.getProcessingTimeMillis());
            System.out.println("Files written: merge-output.txt, sorted-contract-id.txt");
            System.out.println("Done.");
            
        } catch (IOException e) {
            System.err.println("Error processing files: " + e.getMessage());
            System.exit(1);
        }
    }
}
