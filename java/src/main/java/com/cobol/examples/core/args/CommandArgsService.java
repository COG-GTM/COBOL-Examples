package com.cobol.examples.core.args;

import java.util.List;

/**
 * Pure-logic port of {@code read_command_args/*.cbl}.
 *
 * <p>The COBOL programs read the raw command line ({@code ACCEPT ... FROM
 * COMMAND-LINE}), looked for a {@code --test} flag, and iterated arguments one
 * by one ({@code ARGUMENT-NUMBER} / {@code ARGUMENT-VALUE}). The CLI front end
 * uses Picocli for real argument parsing; this helper keeps the two small
 * behaviours that are worth unit-testing.
 */
public final class CommandArgsService {

    public static final String TEST_FLAG = "--test";

    private CommandArgsService() {
    }

    /** True when {@code --test} appears (case-insensitively) among the args. */
    public static boolean hasTestFlag(List<String> args) {
        return args.stream().anyMatch(a -> a.equalsIgnoreCase(TEST_FLAG));
    }

    /** Joins arguments into the single string the COBOL code displayed. */
    public static String fullCommandLine(List<String> args) {
        return String.join(" ", args);
    }
}
