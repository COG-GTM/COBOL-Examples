package com.cobolexamples.reportwriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.cobolexamples.support.Console;

public class ReportTest {

    static final int PAGE_LIMIT = 66;
    static final int FIRST_DETAIL = 6;
    static final int LAST_DETAIL = 42;

    record InputRecord(String id, String name, String major, String courses) {
        static InputRecord parse(String line) {
            String padded = line + " ".repeat(Math.max(0, 31 - line.length()));
            return new InputRecord(padded.substring(0, 6), padded.substring(6, 26),
                    padded.substring(26, 29), padded.substring(29, 31));
        }
    }

    private final Path baseDir;

    public ReportTest(Path baseDir) {
        this.baseDir = baseDir;
    }

    public static void main(String[] args) throws IOException {
        new ReportTest(Path.of(".")).run();
    }

    private static void put(StringBuilder sb, int col, String text) {
        int idx = col - 1;
        while (sb.length() < idx + text.length()) sb.append(' ');
        for (int i = 0; i < text.length(); i++) sb.setCharAt(idx + i, text.charAt(i));
    }

    private static List<String> newPage(int pageCounter) {
        List<String> page = new ArrayList<>();
        StringBuilder h1 = new StringBuilder();
        put(h1, 44, "Customer Order Report");
        page.add(h1.toString());
        StringBuilder h2 = new StringBuilder();
        put(h2, 100, "PAGE");
        put(h2, 105, com.cobolexamples.support.Pic.z(pageCounter, 3));
        page.add(h2.toString());
        while (page.size() < FIRST_DETAIL - 1) page.add("");
        return page;
    }

    private static String detailLine(InputRecord r) {
        StringBuilder sb = new StringBuilder();
        put(sb, 4, r.id());
        put(sb, 15, r.name());
        put(sb, 40, r.major());
        put(sb, 46, r.courses());
        return sb.toString().stripTrailing();
    }

    void run() throws IOException {
        Console.display("Starting test report program.");

        List<String> input = Files.exists(baseDir.resolve("input.txt"))
                ? Files.readAllLines(baseDir.resolve("input.txt")) : List.of();
        List<String> report = new ArrayList<>();

        Console.display("Init test report.");
        int pageCounter = 1;
        List<String> page = newPage(pageCounter);

        boolean eof = false;
        int idx = 0;
        InputRecord current = new InputRecord("      ", " ".repeat(20), "   ", "  ");
        while (!eof) {
            if (idx < input.size()) {
                current = InputRecord.parse(input.get(idx++));
            } else {
                eof = true;
            }
            Console.display("Generate report line.");
            if (page.size() >= LAST_DETAIL) {
                while (page.size() < PAGE_LIMIT) page.add("");
                report.addAll(page);
                page = newPage(++pageCounter);
            }
            page.add(detailLine(current));
        }

        Console.display("Terminate report.");
        while (page.size() < PAGE_LIMIT) page.add("");
        report.addAll(page);
        Files.write(baseDir.resolve("report.txt"), report);

        Console.display("Done.");
    }
}
