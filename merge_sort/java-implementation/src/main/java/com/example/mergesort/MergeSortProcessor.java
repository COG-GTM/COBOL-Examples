package com.example.mergesort;

import java.io.*;
import java.util.*;

public class MergeSortProcessor {
    
    public void createTestData() {
        System.out.println("Creating test data files...");
        
        createTestFile1();
        createTestFile2();
    }
    
    private void createTestFile1() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("test-file-1.txt"))) {
            CustomerRecord[] records = {
                new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"),
                new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"),
                new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"),
                new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"),
                new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"),
                new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75")
            };
            
            for (CustomerRecord record : records) {
                writer.write(record.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to create test-file-1.txt: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private void createTestFile2() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("test-file-2.txt"))) {
            CustomerRecord[] records = {
                new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"),
                new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"),
                new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"),
                new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"),
                new CustomerRecord(24, "last-24", "first-24", 247, "comment-24")
            };
            
            for (CustomerRecord record : records) {
                writer.write(record.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to create test-file-2.txt: " + e.getMessage());
            System.exit(1);
        }
    }
    
    public void mergeFiles(String file1, String file2, String outputFile) {
        System.out.println("Merging and sorting files...");
        
        List<CustomerRecord> allRecords = new ArrayList<>();
        
        try (BufferedReader reader1 = new BufferedReader(new FileReader(file1))) {
            String line;
            while ((line = reader1.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    allRecords.add(CustomerRecord.fromString(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading " + file1 + ": " + e.getMessage());
            System.exit(1);
        }
        
        try (BufferedReader reader2 = new BufferedReader(new FileReader(file2))) {
            String line;
            while ((line = reader2.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    allRecords.add(CustomerRecord.fromString(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading " + file2 + ": " + e.getMessage());
            System.exit(1);
        }
        
        allRecords.sort(Comparator.comparingInt(CustomerRecord::getCustomerId));
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (CustomerRecord record : allRecords) {
                writer.write(record.toString());
                writer.newLine();
                System.out.println(record.toString());
            }
        } catch (IOException e) {
            System.err.println("Error writing to " + outputFile + ": " + e.getMessage());
            System.exit(1);
        }
    }
    
    public void sortFile(String inputFile, String outputFile) {
        System.out.println("Sorting merged file on descending contract id....");
        
        List<CustomerRecord> records = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    records.add(CustomerRecord.fromString(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading " + inputFile + ": " + e.getMessage());
            System.exit(1);
        }
        
        records.sort(Comparator.comparingInt(CustomerRecord::getContractId).reversed());
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (CustomerRecord record : records) {
                writer.write(record.toString());
                writer.newLine();
                System.out.println(record.toString());
            }
        } catch (IOException e) {
            System.err.println("Error writing to " + outputFile + ": " + e.getMessage());
            System.exit(1);
        }
    }
    
    public static void main(String[] args) {
        MergeSortProcessor processor = new MergeSortProcessor();
        
        processor.createTestData();
        
        processor.mergeFiles("test-file-1.txt", "test-file-2.txt", "merge-output.txt");
        
        processor.sortFile("merge-output.txt", "sorted-contract-id.txt");
        
        System.out.println("Done.");
    }
}
