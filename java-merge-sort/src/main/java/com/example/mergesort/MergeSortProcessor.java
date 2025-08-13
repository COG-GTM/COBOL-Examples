package com.example.mergesort;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class MergeSortProcessor {

    public void mergeFiles(String file1Path, String file2Path, String outputPath) {
        try {
            List<CustomerRecord> records1 = readRecordsFromFile(file1Path);
            List<CustomerRecord> records2 = readRecordsFromFile(file2Path);
            
            Collections.sort(records1);
            Collections.sort(records2);
            
            List<CustomerRecord> mergedRecords = mergeSortedLists(records1, records2);
            
            writeRecordsToFile(mergedRecords, outputPath);
            
        } catch (IOException e) {
            throw new RuntimeException("Error merging files: " + e.getMessage(), e);
        }
    }

    public void sortFileByContractId(String inputPath, String outputPath) {
        try {
            List<CustomerRecord> records = readRecordsFromFile(inputPath);
            
            records.sort((r1, r2) -> Integer.compare(r2.getContractId(), r1.getContractId()));
            
            writeRecordsToFile(records, outputPath);
            
        } catch (IOException e) {
            throw new RuntimeException("Error sorting file: " + e.getMessage(), e);
        }
    }

    private List<CustomerRecord> readRecordsFromFile(String filePath) throws IOException {
        List<CustomerRecord> records = new ArrayList<>();
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    records.add(CustomerRecord.fromString(line));
                }
            }
        }
        
        return records;
    }

    private void writeRecordsToFile(List<CustomerRecord> records, String filePath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            for (CustomerRecord record : records) {
                writer.write(record.toString());
                writer.newLine();
            }
        }
    }

    private List<CustomerRecord> mergeSortedLists(List<CustomerRecord> list1, List<CustomerRecord> list2) {
        List<CustomerRecord> merged = new ArrayList<>();
        int i = 0, j = 0;
        
        while (i < list1.size() && j < list2.size()) {
            if (list1.get(i).compareTo(list2.get(j)) <= 0) {
                merged.add(list1.get(i));
                i++;
            } else {
                merged.add(list2.get(j));
                j++;
            }
        }
        
        while (i < list1.size()) {
            merged.add(list1.get(i));
            i++;
        }
        
        while (j < list2.size()) {
            merged.add(list2.get(j));
            j++;
        }
        
        return merged;
    }

    public void displayFile(String filePath, String description) {
        try {
            System.out.println(description);
            List<CustomerRecord> records = readRecordsFromFile(filePath);
            for (CustomerRecord record : records) {
                System.out.println(record);
            }
            System.out.println();
        } catch (IOException e) {
            System.err.println("Error reading file " + filePath + ": " + e.getMessage());
        }
    }
}
