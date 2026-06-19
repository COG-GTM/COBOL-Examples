package com.cobol.examples.core.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ReportWriterTest {

    @Test
    void rendersHeadingAndColumnPositionedDetail() {
        String report = ReportWriter.generate(List.of(new StudentRecord(100234, "Ada Lovelace", "CSC", 4)));
        String[] lines = report.split("\n");

        // Page heading: title starts at column 44 (index 43).
        assertTrue(lines[0].contains("Customer Order Report"));
        assertEquals(43, lines[0].indexOf("Customer Order Report"));
        assertTrue(lines[0].contains("PAGE"));

        // Detail line: id at column 4 (index 3), name at column 15 (index 14).
        assertEquals("100234", lines[1].substring(3, 9));
        assertEquals("Ada Lovelace", lines[1].substring(14, 26).trim());
    }

    @Test
    void paginatesWhenExceedingPageLimit() {
        List<StudentRecord> many = java.util.stream.IntStream.range(0, 40)
                .mapToObj(i -> new StudentRecord(i, "name" + i, "CSC", 1))
                .toList();
        long pageHeadings = ReportWriter.generate(many).lines()
                .filter(l -> l.contains("Customer Order Report"))
                .count();
        assertEquals(2, pageHeadings);
    }
}
