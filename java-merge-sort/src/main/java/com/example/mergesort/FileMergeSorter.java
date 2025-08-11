package com.example.mergesort;

import java.io.*;
import java.util.*;

public class FileMergeSorter {
    
    public void createTestDataEast(String filename) throws IOException {
        System.out.println("Creating test data files...");
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            CustomerRecord[] records = {
                new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"),
                new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"),
                new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"),
                new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"),
                new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"),
                new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75")
            };
            
            for (CustomerRecord record : records) {
                writer.write(record.toFileString());
                writer.newLine();
            }
        }
    }
    
    public void createTestDataWest(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            CustomerRecord[] records = {
                new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"),
                new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"),
                new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"),
                new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"),
                new CustomerRecord(24, "last-24", "first-24", 247, "comment-24")
            };
            
            for (CustomerRecord record : records) {
                writer.write(record.toFileString());
                writer.newLine();
            }
        }
    }
    
    public void mergeFiles(String inputFile1, String inputFile2, String outputFile) throws IOException {
        System.out.println("Merging and sorting files...");
        
        List<CustomerRecord> allRecords = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile1))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    allRecords.add(CustomerRecord.fromFileString(line));
                }
            }
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile2))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    allRecords.add(CustomerRecord.fromFileString(line));
                }
            }
        }
        
        allRecords.sort(Comparator.comparingInt(CustomerRecord::getCustomerId));
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (CustomerRecord record : allRecords) {
                writer.write(record.toFileString());
                writer.newLine();
            }
        }
        
        for (CustomerRecord record : allRecords) {
            System.out.println(record);
        }
    }
    
    public void sortFile(String inputFile, String outputFile) throws IOException {
        System.out.println("Sorting merged file on descending contract id....");
        
        List<CustomerRecord> records = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    records.add(CustomerRecord.fromFileString(line));
                }
            }
        }
        
        records.sort(Comparator.comparingInt(CustomerRecord::getCustomerContractId).reversed());
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (CustomerRecord record : records) {
                writer.write(record.toFileString());
                writer.newLine();
            }
        }
        
        for (CustomerRecord record : records) {
            System.out.println(record);
        }
    }
    
    public void displayFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    CustomerRecord record = CustomerRecord.fromFileString(line);
                    System.out.println(record);
                }
            }
        }
    }
}
