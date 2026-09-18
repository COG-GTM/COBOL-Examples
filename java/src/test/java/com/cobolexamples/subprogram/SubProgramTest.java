package com.cobolexamples.subprogram;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

import com.cobolexamples.support.Field;

class SubProgramTest {

    @Test
    void byContentLeavesCallerFieldsUnchanged() {
        Field a = new Field(10, "value1");
        Field b = new Field(10, "value2");
        SubApp.call(new Field(10, a.get()), new Field(10, b.get()));
        assertEquals("value1    ", a.get());
        assertEquals("value2    ", b.get());
    }

    @Test
    void byReferenceModifiesCallerFields() {
        Field a = new Field(10, "value1");
        Field b = new Field(10, "value2");
        SubApp.call(a, b);
        assertEquals("replace1  ", a.get());
        assertEquals("replace2  ", b.get());
    }

    @Test
    void cancelResetsWorkingStorage() {
        Field a = new Field(10, "x");
        Field b = new Field(10, "y");
        SubApp.call(a, b);
        SubApp.cancel();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream old = System.out;
        System.setOut(new PrintStream(bos));
        try {
            SubApp.call(new Field(10, "p"), new Field(10, "q"));
        } finally {
            System.setOut(old);
        }
        String out = bos.toString();
        assertTrue(out.contains("ws-test-item-1:           "));
        assertTrue(out.contains("ws-test-item-2:           "));
    }
}
