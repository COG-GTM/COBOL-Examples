import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String file1 = "test-file-1.txt";
        String file2 = "test-file-2.txt";
        String mergedFile = "merge-output.txt";
        String sortedFile = "sorted-contract-id.txt";

    List<CustomerRecord> records1 = Arrays.asList(
            new CustomerRecord("00001", "last-1", "first-1", "05423", "comment-1"),
            new CustomerRecord("00005", "last-5", "first-5", "12323", "comment-5"),
            new CustomerRecord("00010", "last-10", "first-10", "00653", "comment-10"),
            new CustomerRecord("00050", "last-50", "first-50", "05050", "comment-50"),
            new CustomerRecord("00025", "last-25", "first-25", "07725", "comment-25"),
            new CustomerRecord("00075", "last-75", "first-75", "01175", "comment-75")
    );

    List<CustomerRecord> records2 = Arrays.asList(
            new CustomerRecord("00999", "last-999", "first-999", "01610", "comment-99"),
            new CustomerRecord("00003", "last-03", "first-03", "03331", "comment-03"),
            new CustomerRecord("00030", "last-30", "first-30", "08765", "comment-30"),
            new CustomerRecord("00085", "last-85", "first-85", "04567", "comment-85"),
            new CustomerRecord("00024", "last-24", "first-24", "00247", "comment-24")
    );

    try {
        FileWriter writer1 = new FileWriter(file1);
        writer1.writeRecords(records1);

        FileWriter writer2 = new FileWriter(file2);
        writer2.writeRecords(records2);

        MergeSortProcessor processor = new MergeSortProcessor(file1, file2, mergedFile, sortedFile);
        processor.process();

        System.out.println("Processing complete. Check the output files.");
    } catch (IOException e) {
        e.printStackTrace();
    }
    }
}
