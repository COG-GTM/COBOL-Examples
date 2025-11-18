import java.io.*;
import java.util.*;

public class MergeSortExample {
    private static final String TEST_FILE_1 = "test-file-1.txt";
    private static final String TEST_FILE_2 = "test-file-2.txt";
    private static final String MERGE_OUTPUT = "merge-output.txt";
    private static final String SORTED_OUTPUT = "sorted-contract-id.txt";

    public static void main(String[] args) {
        try {
            createTestData();
            mergeAndDisplayFiles();
            sortAndDisplayFile();
            System.out.println("Done.");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createTestData() throws IOException {
        System.out.println("Creating test data files...");

        try (BufferedWriter writer1 = new BufferedWriter(new FileWriter(TEST_FILE_1))) {
            writer1.write(new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1").toFixedLengthString());
            writer1.newLine();
            writer1.write(new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5").toFixedLengthString());
            writer1.newLine();
            writer1.write(new CustomerRecord(10, "last-10", "first-10", 653, "comment-10").toFixedLengthString());
            writer1.newLine();
            writer1.write(new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50").toFixedLengthString());
            writer1.newLine();
            writer1.write(new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25").toFixedLengthString());
            writer1.newLine();
            writer1.write(new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75").toFixedLengthString());
            writer1.newLine();
        }

        try (BufferedWriter writer2 = new BufferedWriter(new FileWriter(TEST_FILE_2))) {
            writer2.write(new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99").toFixedLengthString());
            writer2.newLine();
            writer2.write(new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03").toFixedLengthString());
            writer2.newLine();
            writer2.write(new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30").toFixedLengthString());
            writer2.newLine();
            writer2.write(new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85").toFixedLengthString());
            writer2.newLine();
            writer2.write(new CustomerRecord(24, "last-24", "first-24", 247, "comment-24").toFixedLengthString());
            writer2.newLine();
        }
    }

    private static void mergeAndDisplayFiles() throws IOException {
        System.out.println("Merging and sorting files...");

        List<CustomerRecord> file1Records = readRecordsFromFile(TEST_FILE_1);
        List<CustomerRecord> file2Records = readRecordsFromFile(TEST_FILE_2);

        List<CustomerRecord> mergedRecords = mergeSortedLists(file1Records, file2Records);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MERGE_OUTPUT))) {
            for (CustomerRecord record : mergedRecords) {
                System.out.println(record);
                writer.write(record.toFixedLengthString());
                writer.newLine();
            }
        }
    }

    private static void sortAndDisplayFile() throws IOException {
        System.out.println("Sorting merged file on descending contract id....");

        List<CustomerRecord> records = readRecordsFromFile(MERGE_OUTPUT);

        records.sort(Comparator.comparingInt(CustomerRecord::getCustomerContractId).reversed());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SORTED_OUTPUT))) {
            for (CustomerRecord record : records) {
                System.out.println(record);
                writer.write(record.toFixedLengthString());
                writer.newLine();
            }
        }
    }

    private static List<CustomerRecord> readRecordsFromFile(String filename) throws IOException {
        List<CustomerRecord> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    records.add(new CustomerRecord(line));
                }
            }
        }
        return records;
    }

    private static List<CustomerRecord> mergeSortedLists(List<CustomerRecord> list1, List<CustomerRecord> list2) {
        list1.sort(Comparator.comparingInt(CustomerRecord::getCustomerId));
        list2.sort(Comparator.comparingInt(CustomerRecord::getCustomerId));

        List<CustomerRecord> merged = new ArrayList<>();
        int i = 0, j = 0;

        while (i < list1.size() && j < list2.size()) {
            if (list1.get(i).getCustomerId() <= list2.get(j).getCustomerId()) {
                merged.add(list1.get(i));
                i++;
            } else {
                merged.add(list2.get(j));
                j++;
            }
        }

        while (i < list1.size()) {
            merged.add(list1.get(i));
            i++;
        }

        while (j < list2.size()) {
            merged.add(list2.get(j));
            j++;
        }

        return merged;
    }
}
