package com.example.mergesort;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Implements the merge sort functionality matching the COBOL implementation.
 * Performs a two-step process: merge by ascending customer ID, then sort by descending contract ID.
 */
public class MergeSortProcessor {
    
    /**
     * Merges two lists of customer records by ascending customer ID.
     * Implements the merge operation from COBOL lines 107-110.
     */
    public List<CustomerRecord> mergeByCustomerId(List<CustomerRecord> eastRegion, List<CustomerRecord> westRegion) {
        if (eastRegion == null) eastRegion = new ArrayList<>();
        if (westRegion == null) westRegion = new ArrayList<>();
        
        List<CustomerRecord> merged = new ArrayList<>();
        merged.addAll(eastRegion);
        merged.addAll(westRegion);
        
        merged.sort(Comparator.comparingInt(CustomerRecord::getCustomerId));
        
        return merged;
    }
    
    /**
     * Sorts a list of customer records by descending contract ID.
     * Implements the sort operation from COBOL lines 142-145.
     */
    public List<CustomerRecord> sortByContractIdDescending(List<CustomerRecord> records) {
        if (records == null) return new ArrayList<>();
        
        List<CustomerRecord> sorted = new ArrayList<>(records);
        sorted.sort((a, b) -> Integer.compare(b.getCustomerContractId(), a.getCustomerContractId()));
        
        return sorted;
    }
    
    /**
     * Performs the complete merge and sort process matching the COBOL implementation.
     */
    public ProcessingResult processRecords(List<CustomerRecord> eastRegion, List<CustomerRecord> westRegion) {
        long startTime = System.nanoTime();
        
        List<CustomerRecord> merged = mergeByCustomerId(eastRegion, westRegion);
        List<CustomerRecord> sorted = sortByContractIdDescending(merged);
        
        long endTime = System.nanoTime();
        long processingTimeNanos = endTime - startTime;
        
        return new ProcessingResult(merged, sorted, processingTimeNanos);
    }
    
    /**
     * Reads customer records from a file.
     */
    public List<CustomerRecord> readFromFile(String filename) throws IOException {
        List<CustomerRecord> records = new ArrayList<>();
        
        if (!Files.exists(Paths.get(filename))) {
            return records;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() >= 135) {
                    CustomerRecord record = parseRecordFromLine(line);
                    records.add(record);
                }
            }
        }
        
        return records;
    }
    
    /**
     * Writes customer records to a file.
     */
    public void writeToFile(List<CustomerRecord> records, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (CustomerRecord record : records) {
                writer.write(record.toString());
                writer.newLine();
            }
        }
    }
    
    private CustomerRecord parseRecordFromLine(String line) {
        int customerId = Integer.parseInt(line.substring(0, 5));
        String lastName = line.substring(5, 55).trim();
        String firstName = line.substring(55, 105).trim();
        int contractId = Integer.parseInt(line.substring(105, 110));
        String comment = line.substring(110, 135).trim();
        
        return new CustomerRecord(customerId, lastName, firstName, contractId, comment);
    }
    
    /**
     * Result class containing the processing results and timing information.
     */
    public static class ProcessingResult {
        private final List<CustomerRecord> mergedRecords;
        private final List<CustomerRecord> sortedRecords;
        private final long processingTimeNanos;
        
        public ProcessingResult(List<CustomerRecord> mergedRecords, List<CustomerRecord> sortedRecords, long processingTimeNanos) {
            this.mergedRecords = mergedRecords;
            this.sortedRecords = sortedRecords;
            this.processingTimeNanos = processingTimeNanos;
        }
        
        public List<CustomerRecord> getMergedRecords() {
            return mergedRecords;
        }
        
        public List<CustomerRecord> getSortedRecords() {
            return sortedRecords;
        }
        
        public long getProcessingTimeNanos() {
            return processingTimeNanos;
        }
        
        public double getProcessingTimeMillis() {
            return processingTimeNanos / 1_000_000.0;
        }
        
        public double getProcessingTimeSeconds() {
            return processingTimeNanos / 1_000_000_000.0;
        }
    }
}
