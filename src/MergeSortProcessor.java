import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MergeSortProcessor {
    private FileReader fileReader1;
    private FileReader fileReader2;
    private FileWriter fileWriterMerged;
    private FileWriter fileWriterSorted;

    public MergeSortProcessor(String file1, String file2, String mergedFile, String sortedFile) {
        this.fileReader1 = new FileReader(file1);
        this.fileReader2 = new FileReader(file2);
        this.fileWriterMerged = new FileWriter(mergedFile);
        this.fileWriterSorted = new FileWriter(sortedFile);
    }

    public void process() throws IOException {
    List<CustomerRecord> records1 = fileReader1.readRecords();
    List<CustomerRecord> records2 = fileReader2.readRecords();

    records1.addAll(records2);
    records1.sort(Comparator.comparing(CustomerRecord::getCustomerID));

    fileWriterMerged.writeRecords(records1);

    records1.sort(Comparator.comparing(CustomerRecord::getContractID).reversed());

    fileWriterSorted.writeRecords(records1);
    }
}
