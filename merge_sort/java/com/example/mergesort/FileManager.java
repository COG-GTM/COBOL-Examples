package com.example.mergesort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    
    public static class FileStatus {
        private boolean success;
        private String statusCode;
        private String message;
        
        public FileStatus(boolean success, String statusCode, String message) {
            this.success = success;
            this.statusCode = statusCode;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getStatusCode() {
            return statusCode;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    public static FileStatus writeRecords(List<CustomerRecord> records, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (CustomerRecord record : records) {
                writer.write(record.toString());
                writer.newLine();
            }
            return new FileStatus(true, "00", "Success");
        } catch (IOException e) {
            return new FileStatus(false, "35", "Failed to open file for output: " + e.getMessage());
        }
    }
    
    public static List<CustomerRecord> readRecords(String filename) throws IOException {
        return readRecords(filename, null);
    }
    
    public static List<CustomerRecord> readRecords(String filename, FileStatus status) throws IOException {
        List<CustomerRecord> records = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                try {
                    CustomerRecord record = CustomerRecord.fromString(line);
                    records.add(record);
                } catch (Exception e) {
                    System.err.println("Warning: Skipping invalid record: " + line);
                }
            }
            if (status != null) {
                status.success = true;
                status.statusCode = "00";
                status.message = "Success";
            }
        } catch (IOException e) {
            if (status != null) {
                status.success = false;
                status.statusCode = "35";
                status.message = "Error opening file: " + e.getMessage();
            }
            throw e;
        }
        
        return records;
    }
    
    public static boolean checkFileStatus(FileStatus status) {
        if (!status.isSuccess()) {
            System.out.println(status.getMessage() + " " + status.getStatusCode());
            return false;
        }
        return true;
    }
}
