import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileWriter {
    
    public static void writeCustomerRecords(List<CustomerRecord> records, String filename) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename))) {
            for (CustomerRecord record : records) {
                writer.write(record.toFixedWidthString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing file " + filename + ": " + e.getMessage());
        }
    }
}
