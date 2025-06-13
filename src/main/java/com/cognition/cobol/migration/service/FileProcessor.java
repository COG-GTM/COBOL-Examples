package com.cognition.cobol.migration.service;

import com.cognition.cobol.migration.model.Customer;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileProcessor {
    
    public void writeCustomersToFile(List<Customer> customers, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Customer customer : customers) {
                writer.println(customer.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to file: " + filename, e);
        }
    }
    
    public List<Customer> readCustomersFromFile(String filename) {
        List<Customer> customers = new ArrayList<>();
        
        try {
            List<String> lines = Files.readAllLines(Paths.get(filename));
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                customers.add(parseCustomerFromLine(line));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read from file: " + filename, e);
        }
        
        return customers;
    }
    
    private Customer parseCustomerFromLine(String line) {
        if (line.length() < 135) {
            line = line + " ".repeat(135 - line.length());
        }
        
        try {
            int customerId = Integer.parseInt(line.substring(0, 5).trim());
            String lastName = line.substring(5, 55).trim();
            String firstName = line.substring(55, 105).trim();
            int contractId = Integer.parseInt(line.substring(105, 110).trim());
            String comment = line.substring(110, 135).trim();
            
            return new Customer(customerId, lastName, firstName, contractId, comment);
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            throw new RuntimeException("Failed to parse customer record from line: " + line, e);
        }
    }
    
    public void displayCustomers(List<Customer> customers, String operation) {
        System.out.println(operation);
        for (Customer customer : customers) {
            System.out.println(customer.toString());
        }
    }
}
