package com.cobol.examples.core.args;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class CommandArgsServiceTest {

    @Test
    void detectsTestFlagCaseInsensitively() {
        assertTrue(CommandArgsService.hasTestFlag(List.of("foo", "--TEST")));
        assertFalse(CommandArgsService.hasTestFlag(List.of("foo", "bar")));
    }

    @Test
    void joinsFullCommandLine() {
        assertEquals("a b c", CommandArgsService.fullCommandLine(List.of("a", "b", "c")));
    }
}
