package com.cobolexamples.trim;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class TrimExampleTest {

    @Test
    void outputMatchesCobol() throws Exception {
        String expected = new String(
                getClass().getResourceAsStream("/expected/trim.out").readAllBytes(),
                StandardCharsets.UTF_8);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream old = System.out;
        System.setOut(new PrintStream(bos));
        try {
            TrimExample.main(new String[0]);
        } finally {
            System.setOut(old);
        }
        assertEquals(expected, bos.toString());
    }
}
