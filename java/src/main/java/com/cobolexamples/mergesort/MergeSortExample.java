package com.cobolexamples.mergesort;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.cobolexamples.support.Console;
import com.cobolexamples.support.Pic;

public class MergeSortExample {

    record CustomerRecord(int customerId, String lastName, String firstName,
                          int contractId, String comment) {
        String format() {
            return Pic.nine(customerId, 5) + Pic.x(lastName, 50) + Pic.x(firstName, 50)
                    + Pic.nine(contractId, 5) + Pic.x(comment, 25);
        }

        static CustomerRecord parse(String line) {
            return new CustomerRecord(
                    Integer.parseInt(line.substring(0, 5).trim()),
                    line.substring(5, 55),
                    line.substring(55, 105),
                    Integer.parseInt(line.substring(105, 110).trim()),
                    line.substring(110));
        }
    }

    private final Path baseDir;

    public MergeSortExample(Path baseDir) {
        this.baseDir = baseDir;
    }

    public static void main(String[] args) throws IOException {
        new MergeSortExample(Path.of(".")).run();
    }

    void run() throws IOException {
        createTestData();
        mergeAndDisplayFiles();
        sortAndDisplayFile();
        Console.display("Done.");
    }

    void mergeAndDisplayFiles() throws IOException {
        Console.display("Merging and sorting files...");

        List<CustomerRecord> all = new ArrayList<>();
        all.addAll(readRecords(baseDir.resolve("test-file-1.txt")));
        all.addAll(readRecords(baseDir.resolve("test-file-2.txt")));
        all.sort(Comparator.comparingInt(CustomerRecord::customerId));
        writeRecords(baseDir.resolve("merge-output.txt"), all);

        for (CustomerRecord r : all) {
            Console.display(r.format());
        }
    }

    void sortAndDisplayFile() throws IOException {
        Console.display("Sorting merged file on descending contract id....");

        List<CustomerRecord> all = readRecords(baseDir.resolve("merge-output.txt"));
        all.sort(Comparator.comparingInt(CustomerRecord::contractId).reversed());
        writeRecords(baseDir.resolve("sorted-contract-id.txt"), all);

        for (CustomerRecord r : all) {
            Console.display(r.format());
        }
    }

    void createTestData() throws IOException {
        Console.display("Creating test data files...");

        List<CustomerRecord> east = List.of(
                new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"),
                new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"),
                new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"),
                new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"),
                new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"),
                new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75"));
        writeRecords(baseDir.resolve("test-file-1.txt"), east);

        List<CustomerRecord> west = List.of(
                new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"),
                new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"),
                new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"),
                new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"),
                new CustomerRecord(24, "last-24", "first-24", 247, "comment-24"));
        writeRecords(baseDir.resolve("test-file-2.txt"), west);
    }

    private static List<CustomerRecord> readRecords(Path p) throws IOException {
        List<CustomerRecord> out = new ArrayList<>();
        for (String line : Files.readAllLines(p)) {
            if (!line.isBlank()) out.add(CustomerRecord.parse(line));
        }
        return out;
    }

    private static void writeRecords(Path p, List<CustomerRecord> records) throws IOException {
        List<String> lines = new ArrayList<>();
        for (CustomerRecord r : records) lines.add(r.format().stripTrailing());
        Files.write(p, lines);
    }
}
