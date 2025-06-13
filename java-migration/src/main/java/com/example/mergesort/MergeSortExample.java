package com.example.mergesort;

import java.io.*;
import java.util.*;

public class MergeSortExample {
    
    public static void main(String[] args) {
        try {
            createTestData();
            mergeAndDisplayFiles();
            sortAndDisplayFile();
            System.out.println("Done.");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createTestData() throws IOException {
        System.out.println("Creating test data files...");
        
        createTestFile1();
        createTestFile2();
    }

    private static void createTestFile1() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("test-file-1.txt"))) {
            Customer[] customers = {
                new Customer(1, "last-1", "first-1", 5423, "comment-1"),
                new Customer(5, "last-5", "first-5", 12323, "comment-5"),
                new Customer(10, "last-10", "first-10", 653, "comment-10"),
                new Customer(50, "last-50", "first-50", 5050, "comment-50"),
                new Customer(25, "last-25", "first-25", 7725, "comment-25"),
                new Customer(75, "last-75", "first-75", 1175, "comment-75")
            };
            
            for (Customer customer : customers) {
                writer.write(customer.toString());
                writer.newLine();
            }
        }
    }

    private static void createTestFile2() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("test-file-2.txt"))) {
            Customer[] customers = {
                new Customer(999, "last-999", "first-999", 1610, "comment-99"),
                new Customer(3, "last-03", "first-03", 3331, "comment-03"),
                new Customer(30, "last-30", "first-30", 8765, "comment-30"),
                new Customer(85, "last-85", "first-85", 4567, "comment-85"),
                new Customer(24, "last-24", "first-24", 247, "comment-24")
            };
            
            for (Customer customer : customers) {
                writer.write(customer.toString());
                writer.newLine();
            }
        }
    }

    private static void mergeAndDisplayFiles() throws IOException {
        System.out.println("Merging and sorting files...");
        
        List<Customer> allCustomers = new ArrayList<>();
        
        allCustomers.addAll(readCustomersFromFile("test-file-1.txt"));
        allCustomers.addAll(readCustomersFromFile("test-file-2.txt"));
        
        Collections.sort(allCustomers, Comparator.comparingInt(Customer::getCustomerId));
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("merge-output.txt"))) {
            for (Customer customer : allCustomers) {
                writer.write(customer.toString());
                writer.newLine();
                System.out.println(customer.toString());
            }
        }
    }

    private static void sortAndDisplayFile() throws IOException {
        System.out.println("Sorting merged file on descending contract id....");
        
        List<Customer> customers = readCustomersFromFile("merge-output.txt");
        
        Collections.sort(customers, Comparator.comparingInt(Customer::getContractId).reversed());
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("sorted-contract-id.txt"))) {
            for (Customer customer : customers) {
                writer.write(customer.toString());
                writer.newLine();
                System.out.println(customer.toString());
            }
        }
    }

    private static List<Customer> readCustomersFromFile(String filename) throws IOException {
        List<Customer> customers = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    customers.add(Customer.fromString(line));
                }
            }
        }
        
        return customers;
    }
}
