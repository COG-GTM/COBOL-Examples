import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class FileWriter {
    
    public static void writeCustomerRecords(List<CustomerRecord> records, String filename) throws IOException {
        Path path = Paths.get(filename);
        
        try (BufferedWriter writer = Files.newBufferedWriter(path, 
                StandardOpenOption.CREATE, 
                StandardOpenOption.WRITE, 
                StandardOpenOption.TRUNCATE_EXISTING)) {
            
            for (CustomerRecord record : records) {
                writer.write(record.toFixedWidthString());
                writer.newLine();
            }
        }
    }
    
    public static void appendCustomerRecord(CustomerRecord record, String filename) throws IOException {
        Path path = Paths.get(filename);
        
        try (BufferedWriter writer = Files.newBufferedWriter(path, 
                StandardOpenOption.CREATE, 
                StandardOpenOption.WRITE, 
                StandardOpenOption.APPEND)) {
            
            writer.write(record.toFixedWidthString());
            writer.newLine();
        }
    }
}
