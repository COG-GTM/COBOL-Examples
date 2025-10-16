package com.cobol.examples;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FixedWidthFileIO {
    
    public static List<CustomerRecord> readRecords(String filename) throws IOException {
        List<CustomerRecord> records = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(filename));
        
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                records.add(CustomerRecord.parseFromFixedWidth(line));
            }
        }
        
        return records;
    }
    
    public static void writeRecords(String filename, List<CustomerRecord> records) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename))) {
            for (CustomerRecord record : records) {
                String line = record.toFixedWidth();
                writer.write(line.stripTrailing());
                writer.newLine();
            }
        }
    }
}
