import java.io.*;
import java.util.*;

public class FileWriter {
    
    public static void writeCustomerRecords(List<CustomerRecord> records, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new java.io.FileWriter(filename))) {
            for (CustomerRecord record : records) {
                writer.println(record.toFixedWidthString());
            }
        } catch (IOException e) {
            throw new IOException("Error writing to file: " + filename, e);
        }
    }
    
    public static void displayRecords(List<CustomerRecord> records, String title) {
        System.out.println(title);
        for (CustomerRecord record : records) {
            System.out.println(record.toFixedWidthString());
        }
    }
}
