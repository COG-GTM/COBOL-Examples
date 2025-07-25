package com.cognition.cobol.mergesort;

import java.io.*;
import java.util.*;

/**
 * Java implementation of the COBOL merge sort program.
 * Replicates the functionality of merge_sort_test.cbl
 */
public class MergeSortExample {
    
    public static void main(String[] args) {
        MergeSortExample program = new MergeSortExample();
        
        try {
            program.createTestData();
            program.mergeAndDisplayFiles();
            program.sortAndDisplayFile();
            System.out.println("Done.");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Creates test data files matching the COBOL program's test data
     */
    private void createTestData() throws IOException {
        System.out.println("Creating test data files...");
        
        try (PrintWriter writer = new PrintWriter(new FileWriter("test-file-1.txt"))) {
            writer.println(new Customer(1, "last-1", "first-1", 5423, "comment-1"));
            writer.println(new Customer(5, "last-5", "first-5", 12323, "comment-5"));
            writer.println(new Customer(10, "last-10", "first-10", 653, "comment-10"));
            writer.println(new Customer(50, "last-50", "first-50", 5050, "comment-50"));
            writer.println(new Customer(25, "last-25", "first-25", 7725, "comment-25"));
            writer.println(new Customer(75, "last-75", "first-75", 1175, "comment-75"));
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter("test-file-2.txt"))) {
            writer.println(new Customer(999, "last-999", "first-999", 1610, "comment-99"));
            writer.println(new Customer(3, "last-03", "first-03", 3331, "comment-03"));
            writer.println(new Customer(30, "last-30", "first-30", 8765, "comment-30"));
            writer.println(new Customer(85, "last-85", "first-85", 4567, "comment-85"));
            writer.println(new Customer(24, "last-24", "first-24", 247, "comment-24"));
        }
    }
    
    /**
     * Merges two input files and sorts by customer ID (ascending)
     * Equivalent to COBOL: merge fd-sorting-file on ascending key f-customer-id
     */
    private void mergeAndDisplayFiles() throws IOException {
        System.out.println("Merging and sorting files...");
        
        List<Customer> allCustomers = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader("test-file-1.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                allCustomers.add(Customer.fromString(line));
            }
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader("test-file-2.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                allCustomers.add(Customer.fromString(line));
            }
        }
        
        allCustomers.sort(Comparator.comparingInt(Customer::getCustomerId));
        
        try (PrintWriter writer = new PrintWriter(new FileWriter("merge-output.txt"))) {
            for (Customer customer : allCustomers) {
                writer.println(customer);
                System.out.println(customer);
            }
        }
    }
    
    /**
     * Sorts the merged file by contract ID (descending)
     * Equivalent to COBOL: sort fd-sorting-file on descending key f-customer-contract-id
     */
    private void sortAndDisplayFile() throws IOException {
        System.out.println("Sorting merged file on descending contract id....");
        
        List<Customer> customers = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader("merge-output.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                customers.add(Customer.fromString(line));
            }
        }
        
        customers.sort(Comparator.comparingInt(Customer::getContractId).reversed());
        
        try (PrintWriter writer = new PrintWriter(new FileWriter("sorted-contract-id.txt"))) {
            for (Customer customer : customers) {
                writer.println(customer);
                System.out.println(customer);
            }
        }
    }
}
