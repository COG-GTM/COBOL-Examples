package com.cobolexamples.mergesort;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MergeSortExampleTest {

    @TempDir
    Path dir;

    @Test
    void outputMatchesCobol() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream old = System.out;
        System.setOut(new PrintStream(bos));
        try {
            new MergeSortExample(dir).run();
        } finally {
            System.setOut(old);
        }
        String expected = new String(
                getClass().getResourceAsStream("/expected/merge.out").readAllBytes(),
                StandardCharsets.UTF_8);
        assertEquals(expected, bos.toString());
        assertEquals(11, Files.readAllLines(dir.resolve("merge-output.txt")).size());
        assertEquals(11, Files.readAllLines(dir.resolve("sorted-contract-id.txt")).size());
    }
}
