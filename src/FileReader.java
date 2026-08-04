import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileReader {
    private String filePath;

    public FileReader(String filePath) {
        this.filePath = filePath;
    }

    public List<CustomerRecord> readRecords() throws IOException {
        List<CustomerRecord> records = new ArrayList<>();
    try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String customerID = line.substring(0, 5).trim();
            String lastName = line.substring(5, 55).trim();
            String firstName = line.substring(55, 105).trim();
            String contractID = line.substring(105, 110).trim();
            String comment = line.substring(110, 135).trim();
            records.add(new CustomerRecord(customerID, lastName, firstName, contractID, comment));
        }
    }
        return records;
    }
}
