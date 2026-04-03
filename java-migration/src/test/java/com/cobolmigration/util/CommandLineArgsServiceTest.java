package com.cobolmigration.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CommandLineArgsService - validates migration of
 * read_command_args/read_cmd_line_args.cbl and read_specific_cmd_line_args.cbl.
 */
class CommandLineArgsServiceTest {

    @Test
    void getFullCommandLine_returnsJoinedArgs() {
        CommandLineArgsService service = new CommandLineArgsService(
                new String[]{"--test", "value1", "value2"});
        assertEquals("--test value1 value2", service.getFullCommandLine());
    }

    @Test
    void getFullCommandLine_emptyArgs() {
        CommandLineArgsService service = new CommandLineArgsService(new String[]{});
        assertEquals("", service.getFullCommandLine());
    }

    @Test
    void getFullCommandLine_nullArgs() {
        CommandLineArgsService service = new CommandLineArgsService(null);
        assertEquals("", service.getFullCommandLine());
    }

    @Test
    void getArgumentCount_returnsCorrectCount() {
        CommandLineArgsService service = new CommandLineArgsService(
                new String[]{"a", "b", "c"});
        assertEquals(3, service.getArgumentCount());
    }

    @Test
    void getArgument_returnsCorrectArg_oneBased() {
        CommandLineArgsService service = new CommandLineArgsService(
                new String[]{"first", "second", "third"});
        assertEquals("first", service.getArgument(1));
        assertEquals("second", service.getArgument(2));
        assertEquals("third", service.getArgument(3));
    }

    @Test
    void getArgument_outOfBounds_returnsEmpty() {
        CommandLineArgsService service = new CommandLineArgsService(
                new String[]{"only"});
        assertEquals("", service.getArgument(0));
        assertEquals("", service.getArgument(2));
    }

    @Test
    void hasFlag_findsFlag() {
        CommandLineArgsService service = new CommandLineArgsService(
                new String[]{"--test", "value"});
        assertTrue(service.hasFlag("--test"));
    }

    @Test
    void hasFlag_caseInsensitive() {
        CommandLineArgsService service = new CommandLineArgsService(
                new String[]{"--TEST", "value"});
        assertTrue(service.hasFlag("--test"));
    }

    @Test
    void hasFlag_notFound() {
        CommandLineArgsService service = new CommandLineArgsService(
                new String[]{"--other"});
        assertFalse(service.hasFlag("--test"));
    }

    @Test
    void getAllArguments_returnsList() {
        CommandLineArgsService service = new CommandLineArgsService(
                new String[]{"a", "b"});
        assertEquals(2, service.getAllArguments().size());
        assertEquals("a", service.getAllArguments().get(0));
    }
}
