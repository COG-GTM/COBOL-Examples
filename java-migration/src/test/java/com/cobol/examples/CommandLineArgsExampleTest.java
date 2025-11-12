package com.cobol.examples;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CommandLineArgsExample.
 * Tests command-line argument handling to ensure it matches COBOL behavior.
 */
public class CommandLineArgsExampleTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testNoArguments() {
        String[] args = {};
        CommandLineArgsExample.main(args);
        
        String output = outContent.toString();
        assertEquals("", output);
    }

    @Test
    public void testSingleArgument() {
        String[] args = {"test"};
        CommandLineArgsExample.main(args);
        
        String output = outContent.toString().trim();
        assertEquals("test", output);
    }

    @Test
    public void testMultipleArguments() {
        String[] args = {"arg1", "arg2", "arg3"};
        CommandLineArgsExample.main(args);
        
        String output = outContent.toString();
        String[] lines = output.split(System.lineSeparator());
        
        assertEquals(3, lines.length);
        assertEquals("arg1", lines[0]);
        assertEquals("arg2", lines[1]);
        assertEquals("arg3", lines[2]);
    }

    @Test
    public void testArgumentsWithSpaces() {
        String[] args = {"hello world", "test arg"};
        CommandLineArgsExample.main(args);
        
        String output = outContent.toString();
        String[] lines = output.split(System.lineSeparator());
        
        assertEquals(2, lines.length);
        assertEquals("hello world", lines[0]);
        assertEquals("test arg", lines[1]);
    }

    @Test
    public void testArgumentsWithSpecialCharacters() {
        String[] args = {"test@123", "path/to/file", "key=value"};
        CommandLineArgsExample.main(args);
        
        String output = outContent.toString();
        String[] lines = output.split(System.lineSeparator());
        
        assertEquals(3, lines.length);
        assertEquals("test@123", lines[0]);
        assertEquals("path/to/file", lines[1]);
        assertEquals("key=value", lines[2]);
    }

    @Test
    public void testManyArguments() {
        String[] args = new String[10];
        for (int i = 0; i < 10; i++) {
            args[i] = "arg" + i;
        }
        
        CommandLineArgsExample.main(args);
        
        String output = outContent.toString();
        String[] lines = output.split(System.lineSeparator());
        
        assertEquals(10, lines.length);
        for (int i = 0; i < 10; i++) {
            assertEquals("arg" + i, lines[i]);
        }
    }

    @Test
    public void testEmptyStringArgument() {
        String[] args = {""};
        CommandLineArgsExample.main(args);
        
        String output = outContent.toString();
        assertEquals(System.lineSeparator(), output);
    }
}
