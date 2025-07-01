package com.example.mergesort;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class FileBasedMergeSort {
    
    public static void mergeFiles(String inputFile1, String inputFile2, String outputFile) throws IOException {
        System.out.println("Merging files " + inputFile1 + " and " + inputFile2 + " into " + outputFile + "...");
        
        List<CustomerRecord> allRecords = new ArrayList<>();
        
        try (BufferedReader reader1 = Files.newBufferedReader(Paths.get(inputFile1));
             BufferedReader reader2 = Files.newBufferedReader(Paths.get(inputFile2))) {
            
            String line;
            while ((line = reader1.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    allRecords.add(CustomerRecord.fromFileFormat(line));
                }
            }
            
            while ((line = reader2.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    allRecords.add(CustomerRecord.fromFileFormat(line));
                }
            }
        }
        
        allRecords.sort(Comparator.comparingInt(CustomerRecord::getCustomerId));
        
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFile))) {
            for (CustomerRecord record : allRecords) {
                writer.write(record.toFileFormat());
                writer.newLine();
            }
        }
        
        System.out.println("Merge completed. Records sorted by ascending customer ID.");
    }
    
    public static void sortFileByContractId(String inputFile, String outputFile) throws IOException {
        System.out.println("Sorting file " + inputFile + " by descending contract ID into " + outputFile + "...");
        
        List<CustomerRecord> records = new ArrayList<>();
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    records.add(CustomerRecord.fromFileFormat(line));
                }
            }
        }
        
        records.sort(Comparator.comparingInt(CustomerRecord::getContractId).reversed());
        
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFile))) {
            for (CustomerRecord record : records) {
                writer.write(record.toFileFormat());
                writer.newLine();
            }
        }
        
        System.out.println("Sort completed. Records sorted by descending contract ID.");
    }
    
    public static void displayFileContents(String filename, String description) throws IOException {
        System.out.println("\n" + description + ":");
        System.out.println("=" + "=".repeat(description.length()) + "=");
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    CustomerRecord record = CustomerRecord.fromFileFormat(line);
                    System.out.println(record);
                }
            }
        }
        System.out.println();
    }
    
    public static void createTestFile(String filename, List<CustomerRecord> records) throws IOException {
        System.out.println("Creating test file: " + filename);
        
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename))) {
            for (CustomerRecord record : records) {
                writer.write(record.toFileFormat());
                writer.newLine();
            }
        }
    }
}
