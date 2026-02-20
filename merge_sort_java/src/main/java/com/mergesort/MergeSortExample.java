package com.mergesort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MergeSortExample {

    private final Path workDir;

    public MergeSortExample(Path workDir) {
        this.workDir = workDir;
    }

    public static void main(String[] args) throws IOException {
        Path workDir = Path.of(System.getProperty("user.dir"));
        MergeSortExample program = new MergeSortExample(workDir);
        program.run();
    }

    public void run() throws IOException {
        createTestData();
        mergeAndDisplayFiles();
        sortAndDisplayFile();
        System.out.println("Done.");
    }

    void createTestData() throws IOException {
        System.out.println("Creating test data files...");

        List<CustomerRecord> eastRecords = new ArrayList<>();
        eastRecords.add(new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"));
        eastRecords.add(new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"));
        eastRecords.add(new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"));
        eastRecords.add(new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"));
        eastRecords.add(new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"));
        eastRecords.add(new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75"));

        writeRecordsToFile(workDir.resolve("test-file-1.txt"), eastRecords);

        List<CustomerRecord> westRecords = new ArrayList<>();
        westRecords.add(new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"));
        westRecords.add(new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"));
        westRecords.add(new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"));
        westRecords.add(new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"));
        westRecords.add(new CustomerRecord(24, "last-24", "first-24", 247, "comment-24"));

        writeRecordsToFile(workDir.resolve("test-file-2.txt"), westRecords);
    }

    void mergeAndDisplayFiles() throws IOException {
        System.out.println("Merging and sorting files...");

        List<CustomerRecord> allRecords = new ArrayList<>();
        allRecords.addAll(readRecordsFromFile(workDir.resolve("test-file-1.txt")));
        allRecords.addAll(readRecordsFromFile(workDir.resolve("test-file-2.txt")));

        allRecords.sort(Comparator.comparingInt(CustomerRecord::getCustomerId));

        writeRecordsToFile(workDir.resolve("merge-output.txt"), allRecords);

        List<CustomerRecord> mergedRecords = readRecordsFromFile(workDir.resolve("merge-output.txt"));
        for (CustomerRecord record : mergedRecords) {
            System.out.println(record);
        }
    }

    void sortAndDisplayFile() throws IOException {
        System.out.println("Sorting merged file on descending contract id....");

        List<CustomerRecord> records = readRecordsFromFile(workDir.resolve("merge-output.txt"));

        records.sort(Comparator.comparingInt(CustomerRecord::getCustomerContractId).reversed());

        writeRecordsToFile(workDir.resolve("sorted-contract-id.txt"), records);

        List<CustomerRecord> sortedRecords = readRecordsFromFile(workDir.resolve("sorted-contract-id.txt"));
        for (CustomerRecord record : sortedRecords) {
            System.out.println(record);
        }
    }

    List<CustomerRecord> readRecordsFromFile(Path filePath) throws IOException {
        List<CustomerRecord> records = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    records.add(CustomerRecord.fromFixedWidthString(line));
                }
            }
        }
        return records;
    }

    void writeRecordsToFile(Path filePath, List<CustomerRecord> records) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (CustomerRecord record : records) {
                writer.write(record.toFixedWidthString());
                writer.newLine();
            }
        }
    }
}
