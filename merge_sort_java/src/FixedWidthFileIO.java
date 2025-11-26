import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles reading and writing fixed-width customer record files.
 * Mirrors the COBOL file handling with line sequential organization.
 */
public class FixedWidthFileIO {
    
    public static void writeRecords(String filename, List<CustomerRecord> records) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (CustomerRecord record : records) {
                writer.write(record.toFixedWidthString());
                writer.newLine();
            }
        }
    }
    
    public static void writeRecord(BufferedWriter writer, CustomerRecord record) throws IOException {
        writer.write(record.toFixedWidthString());
        writer.newLine();
    }
    
    public static List<CustomerRecord> readRecords(String filename) throws IOException {
        List<CustomerRecord> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    CustomerRecord record = CustomerRecord.fromFixedWidthString(line);
                    if (record != null) {
                        records.add(record);
                    }
                }
            }
        }
        return records;
    }
    
    public static void displayRecords(String filename) throws IOException {
        List<CustomerRecord> records = readRecords(filename);
        for (CustomerRecord record : records) {
            System.out.println(record.toFixedWidthString());
        }
    }
}
