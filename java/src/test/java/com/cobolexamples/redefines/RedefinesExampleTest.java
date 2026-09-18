package com.cobolexamples.redefines;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

class RedefinesExampleTest {

    private String capture() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream old = System.out;
        System.setOut(new PrintStream(bos));
        try {
            new RedefinesExample().run();
        } finally {
            System.setOut(old);
        }
        return bos.toString();
    }

    @Test
    void firstSectionMatches() {
        String out = capture();
        assertTrue(out.contains("Customer Type: PERSON"));
        assertTrue(out.contains("First Name: test-first"));
        assertTrue(out.contains("Last Name: test-last           "));
        assertTrue(out.contains("Customer Type: CORP"));
        assertTrue(out.contains("Company name: no-name corp"));
        assertTrue(out.contains("First Name: SET CORP V"));
        assertTrue(out.contains("Last Name: ALUE                "));
        assertTrue(out.contains("NV, 12345"));
        assertTrue(out.contains("MA, 09345"));
    }

    @Test
    void comp2RedefinesReadsSameBytes() {
        var dt = new RedefinesExample.DataTypes();
        dt.setCompValue(12345.63);
        assertEquals(12345.63, dt.compValue(), 0.0001);
        dt.setDispValue("ABC123");
        assertEquals("ABC123    ", dt.dispValue());
    }
}
