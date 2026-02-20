package com.mergesort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MergeSortExample {

    private final Path workDir;

    public MergeSortExample(Path workDir) {
        this.workDir = workDir;
    }

    public static void main(String[] args) throws IOException {
        Path dir = Path.of(System.getProperty("user.dir"));
        MergeSortExample app = new MergeSortExample(dir);
        app.run();
    }

    public void run() throws IOException {
        createTestData();
        mergeAndDisplayFiles();
        sortAndDisplayFile();
        System.out.println("Done.");
    }

    void createTestData() throws IOException {
        System.out.println("Creating test data files...");

        List<CustomerRecord> eastRecords = List.of(
                new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"),
                new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"),
                new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"),
                new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"),
                new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"),
                new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75")
        );
        writeRecords(workDir.resolve("test-file-1.txt"), eastRecords);

        List<CustomerRecord> westRecords = List.of(
                new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"),
                new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"),
                new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"),
                new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"),
                new CustomerRecord(24, "last-24", "first-24", 247, "comment-24")
        );
        writeRecords(workDir.resolve("test-file-2.txt"), westRecords);
    }

    void mergeAndDisplayFiles() throws IOException {
        System.out.println("Merging and sorting files...");

        List<CustomerRecord> file1Records = readRecords(workDir.resolve("test-file-1.txt"));
        List<CustomerRecord> file2Records = readRecords(workDir.resolve("test-file-2.txt"));

        List<CustomerRecord> merged = new ArrayList<>();
        merged.addAll(file1Records);
        merged.addAll(file2Records);
        merged.sort(CustomerRecord.BY_CUSTOMER_ID_ASC);

        writeRecords(workDir.resolve("merge-output.txt"), merged);

        for (CustomerRecord record : merged) {
            System.out.println(record);
        }
    }

    void sortAndDisplayFile() throws IOException {
        System.out.println("Sorting merged file on descending contract id....");

        List<CustomerRecord> mergedRecords = readRecords(workDir.resolve("merge-output.txt"));
        mergedRecords.sort(CustomerRecord.BY_CONTRACT_ID_DESC);

        writeRecords(workDir.resolve("sorted-contract-id.txt"), mergedRecords);

        for (CustomerRecord record : mergedRecords) {
            System.out.println(record);
        }
    }

    List<CustomerRecord> readRecords(Path file) throws IOException {
        List<CustomerRecord> records = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    records.add(CustomerRecord.fromFileString(line));
                }
            }
        }
        return records;
    }

    void writeRecords(Path file, List<CustomerRecord> records) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            for (CustomerRecord record : records) {
                writer.write(record.toFileString());
                writer.newLine();
            }
        }
    }
}
