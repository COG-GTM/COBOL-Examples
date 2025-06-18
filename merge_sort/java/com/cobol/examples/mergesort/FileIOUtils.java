package com.cobol.examples.mergesort;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileIOUtils {
    
    private static final int RECORD_LENGTH = 135; // 5 + 50 + 50 + 5 + 25
    
    public static void writeCustomerRecords(String filename, List<CustomerRecord> records) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename))) {
            for (CustomerRecord record : records) {
                writer.write(formatRecord(record));
                writer.newLine();
            }
        }
    }
    
    public static List<CustomerRecord> readCustomerRecords(String filename) throws IOException {
        List<CustomerRecord> records = new ArrayList<>();
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                CustomerRecord record = parseRecord(line);
                if (record != null) {
                    records.add(record);
                }
            }
        }
        
        return records;
    }
    
    private static String formatRecord(CustomerRecord record) {
        return String.format("%05d%-50s%-50s%05d%-25s",
            record.getCustomerId(),
            padRight(record.getLastName(), 50),
            padRight(record.getFirstName(), 50),
            record.getContractId(),
            padRight(record.getComment(), 25));
    }
    
    private static CustomerRecord parseRecord(String line) {
        if (line == null || line.length() < RECORD_LENGTH) {
            return null;
        }
        
        try {
            int customerId = Integer.parseInt(line.substring(0, 5).trim());
            String lastName = line.substring(5, 55).trim();
            String firstName = line.substring(55, 105).trim();
            int contractId = Integer.parseInt(line.substring(105, 110).trim());
            String comment = line.substring(110, 135).trim();
            
            return new CustomerRecord(customerId, lastName, firstName, contractId, comment);
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            System.err.println("Error parsing record: " + line);
            return null;
        }
    }
    
    private static String padRight(String str, int length) {
        if (str == null) str = "";
        if (str.length() >= length) {
            return str.substring(0, length);
        }
        return str + " ".repeat(length - str.length());
    }
    
    public static void displayRecords(List<CustomerRecord> records, String title) {
        System.out.println(title);
        for (CustomerRecord record : records) {
            System.out.println(record.toString());
        }
    }
}
