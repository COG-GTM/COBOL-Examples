import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileReader {
    
    public static List<CustomerRecord> readCustomerRecords(String filename) throws IOException {
        List<CustomerRecord> records = new ArrayList<>();
        Path path = Paths.get(filename);
        
        if (!Files.exists(path)) {
            throw new IOException("File not found: " + filename);
        }
        
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    CustomerRecord record = CustomerRecord.fromFixedWidthString(line);
                    records.add(record);
                } catch (IllegalArgumentException e) {
                    System.err.println("Warning: Skipping invalid record: " + line);
                    System.err.println("Error: " + e.getMessage());
                }
            }
        }
        
        return records;
    }
    
    public static boolean fileExists(String filename) {
        return Files.exists(Paths.get(filename));
    }
}
