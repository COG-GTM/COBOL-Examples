package com.example.cobolmigration.cli;

import com.example.cobolmigration.model.CustomerRecord;
import com.example.cobolmigration.model.SerializableRecord;
import com.example.cobolmigration.model.StudentRecord;
import com.example.cobolmigration.service.FileMergeService;
import com.example.cobolmigration.service.ReportService;
import com.example.cobolmigration.service.SerializationService;
import com.example.cobolmigration.service.StringUtilService;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

/**
 * Spring Shell component for demonstrating migrated utilities.
 * Maps various COBOL utility programs to shell commands.
 */
@ShellComponent
public class UtilityCommands {

    private final StringUtilService stringUtilService;
    private final SerializationService serializationService;
    private final FileMergeService fileMergeService;
    private final ReportService reportService;

    public UtilityCommands(StringUtilService stringUtilService,
                           SerializationService serializationService,
                           FileMergeService fileMergeService,
                           ReportService reportService) {
        this.stringUtilService = stringUtilService;
        this.serializationService = serializationService;
        this.fileMergeService = fileMergeService;
        this.reportService = reportService;
    }

    @ShellMethod("Trim demo")
    public String trimDemo(@ShellOption String input) {
        StringBuilder sb = new StringBuilder();
        sb.append("Original:  \"").append(input).append("\"\n");
        sb.append("Trim both: \"").append(stringUtilService.trimBoth(input)).append("\"\n");
        sb.append("Trim lead: \"").append(stringUtilService.trimLeading(input)).append("\"\n");
        sb.append("Trim trail:\"").append(stringUtilService.trimTrailing(input)).append("\"");
        return sb.toString();
    }

    @ShellMethod("Unstring demo")
    public String unstringDemo(@ShellOption String input, @ShellOption String delimiter) {
        List<String> parts = stringUtilService.unstring(input, delimiter);
        StringBuilder sb = new StringBuilder();
        sb.append("Source: \"").append(input).append("\"\n");
        sb.append("Delimiter: \"").append(delimiter).append("\"\n");
        sb.append("Parts:\n");
        for (int i = 0; i < parts.size(); i++) {
            sb.append("  Part ").append(i + 1).append(": \"").append(parts.get(i)).append("\"\n");
        }
        return sb.toString();
    }

    @ShellMethod("Check numeric")
    public String isNumeric(@ShellOption String input) {
        boolean plain = stringUtilService.isNumeric(input);
        boolean trimmed = stringUtilService.isNumericTrimmed(input);
        return String.format("Input: \"%s\"\n  isNumeric: %s\n  isNumericTrimmed: %s",
                input, plain, trimmed);
    }

    @ShellMethod("Generate JSON")
    public String generateJson(@ShellOption String name, @ShellOption int value,
                               @ShellOption String enabled) {
        SerializableRecord record = new SerializableRecord(name, value, enabled);
        return serializationService.generateJson(record);
    }

    @ShellMethod("Generate XML")
    public String generateXml(@ShellOption String name, @ShellOption int value,
                              @ShellOption String enabled) {
        SerializableRecord record = new SerializableRecord(name, value, enabled);
        return serializationService.generateXml(record);
    }

    @ShellMethod("Merge sort demo")
    public String mergeSortDemo() {
        try {
            Path tempDir = Files.createTempDirectory("merge-sort");
            Path file1 = tempDir.resolve("test-file-1.txt");
            Path file2 = tempDir.resolve("test-file-2.txt");

            fileMergeService.createTestFiles(file1, file2);

            List<CustomerRecord> merged = fileMergeService.mergeAndSort(file1, file2);
            StringBuilder sb = new StringBuilder();
            sb.append("Merged and sorted by customer ID (ascending):\n");
            for (CustomerRecord r : merged) {
                sb.append("  ").append(r).append("\n");
            }

            Path mergedFile = tempDir.resolve("merged-output.txt");
            fileMergeService.writeRecords(merged, mergedFile);

            List<CustomerRecord> sorted = fileMergeService.sortDescending(mergedFile);
            sb.append("\nSorted by contract ID (descending):\n");
            for (CustomerRecord r : sorted) {
                sb.append("  ").append(r).append("\n");
            }

            return sb.toString();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @ShellMethod("Report demo")
    public String reportDemo() {
        List<StudentRecord> students = List.of(
                new StudentRecord(1001, "Alice Johnson", "CSC", 5),
                new StudentRecord(1002, "Bob Smith", "MAT", 4),
                new StudentRecord(1003, "Carol White", "PHY", 6),
                new StudentRecord(1004, "Dave Brown", "ENG", 3),
                new StudentRecord(1005, "Eve Davis", "CSC", 7)
        );
        return reportService.generateReport(students);
    }
}
