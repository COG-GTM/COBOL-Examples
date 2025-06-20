import java.io.*;
import java.util.*;

public class FileReader {
    
    public static List<CustomerRecord> readCustomerRecords(String filename) throws IOException {
        List<CustomerRecord> records = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() > 0) {
                    try {
                        CustomerRecord record = CustomerRecord.fromFixedWidthString(line);
                        records.add(record);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error parsing line: " + line);
                        System.err.println("Error: " + e.getMessage());
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new IOException("File not found: " + filename, e);
        } catch (IOException e) {
            throw new IOException("Error reading file: " + filename, e);
        }
        
        return records;
    }
}
