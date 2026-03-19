package com.cobolmigration.service;

import com.cobolmigration.model.CustomerRecord;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for FileMergeService validating SORT/MERGE logic from merge_sort/merge_sort_test.cbl.
 */
class FileMergeServiceTest {

    private final FileMergeService fileMergeService = new FileMergeService();

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("mergeFiles merges two files and sorts by customer ID ascending")
    void testMergeFiles() throws IOException {
        // Create test data matching merge_sort_test.cbl create-test-data paragraph
        Path file1 = tempDir.resolve("test-file-1.txt");
        Path file2 = tempDir.resolve("test-file-2.txt");
        Path outputFile = tempDir.resolve("merge-output.txt");

        // East region data (file 1) - matches lines 185-259
        Files.writeString(file1, buildRecord(1, "last-1", "first-1", 5423, "comment-1") + "\n" +
                buildRecord(5, "last-5", "first-5", 12323, "comment-5") + "\n" +
                buildRecord(10, "last-10", "first-10", 653, "comment-10") + "\n");

        // West region data (file 2) - matches lines 272-333
        Files.writeString(file2, buildRecord(999, "last-999", "first-999", 1610, "comment-99") + "\n" +
                buildRecord(3, "last-03", "first-03", 3331, "comment-03") + "\n");

        List<CustomerRecord> merged = fileMergeService.mergeFiles(file1, file2, outputFile);

        assertThat(merged).hasSize(5);
        // Verify ascending order by customer ID
        assertThat(merged.get(0).getCustomerId()).isEqualTo(1);
        assertThat(merged.get(1).getCustomerId()).isEqualTo(3);
        assertThat(merged.get(2).getCustomerId()).isEqualTo(5);
        assertThat(merged.get(3).getCustomerId()).isEqualTo(10);
        assertThat(merged.get(4).getCustomerId()).isEqualTo(999);

        // Verify output file was written
        assertThat(Files.exists(outputFile)).isTrue();
        assertThat(Files.readAllLines(outputFile)).hasSize(5);
    }

    @Test
    @DisplayName("sortByContractIdDescending sorts by contract ID in descending order")
    void testSortByContractIdDescending() throws IOException {
        Path inputFile = tempDir.resolve("input.txt");
        Path outputFile = tempDir.resolve("sorted-output.txt");

        Files.writeString(inputFile,
                buildRecord(1, "last-1", "first-1", 5423, "comment-1") + "\n" +
                buildRecord(3, "last-03", "first-03", 3331, "comment-03") + "\n" +
                buildRecord(5, "last-5", "first-5", 12323, "comment-5") + "\n" +
                buildRecord(10, "last-10", "first-10", 653, "comment-10") + "\n");

        List<CustomerRecord> sorted = fileMergeService.sortByContractIdDescending(inputFile, outputFile);

        assertThat(sorted).hasSize(4);
        // Verify descending order by contract ID
        assertThat(sorted.get(0).getContractId()).isEqualTo(12323);
        assertThat(sorted.get(1).getContractId()).isEqualTo(5423);
        assertThat(sorted.get(2).getContractId()).isEqualTo(3331);
        assertThat(sorted.get(3).getContractId()).isEqualTo(653);
    }

    private String buildRecord(int customerId, String lastName, String firstName,
                               int contractId, String comment) {
        return String.format("%-5d%-50s%-50s%-5d%-25s",
                customerId, lastName, firstName, contractId, comment);
    }
}
