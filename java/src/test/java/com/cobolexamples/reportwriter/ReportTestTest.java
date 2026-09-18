package com.cobolexamples.reportwriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ReportTestTest {

    @TempDir
    Path dir;

    @Test
    void reportMatchesCobol() throws Exception {
        String input = """
                003345Test Name2          PHY12
                003345Test Name2          PHY12
                003345Test Name2          PHY12
                003345Test Name2          PHY12
                003345Test Name2          PHY12
                003345Test Name2          PHY12
                003345Test Name2          PHY12
                """;
        Files.writeString(dir.resolve("input.txt"), input);
        new ReportTest(dir).run();

        List<String> lines = Files.readAllLines(dir.resolve("report.txt"));
        assertEquals(66, lines.size());
        assertTrue(lines.get(0).endsWith("Customer Order Report"));
        assertTrue(lines.get(0).startsWith(" ".repeat(43)));
        assertTrue(lines.get(1).substring(99).startsWith("PAGE"));
        long details = lines.stream().filter(l -> l.contains("Test Name2")).count();
        assertEquals(8, details);
        assertEquals("   003345     Test Name2               PHY   12",
                lines.get(5));
    }
}
