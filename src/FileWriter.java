import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileWriter {
    private String filePath;

    public FileWriter(String filePath) {
        this.filePath = filePath;
    }

    public void writeRecords(List<CustomerRecord> records) throws IOException {
    try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
        for (CustomerRecord record : records) {
            String line = String.format("%-5.5s%-50.50s%-50.50s%-5.5s%-25.25s",
                    record.getCustomerID(),
                    record.getLastName(),
                    record.getFirstName(),
                    record.getContractID(),
                    record.getComment());
            writer.write(line);
            writer.newLine();
        }
    }
    }
}
