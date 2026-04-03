package com.cobolmigration.util;

import java.util.Arrays;
import java.util.List;

/**
 * Replaces read_command_args/read_cmd_line_args.cbl and
 * read_command_args/read_specific_cmd_line_args.cbl.
 * Wraps command-line argument handling in Java.
 */
public class CommandLineArgsService {

    private final String[] args;

    public CommandLineArgsService(String[] args) {
        this.args = args != null ? args.clone() : new String[0];
    }

    /**
     * Returns the full command line as a single string.
     * Replaces: ACCEPT ws-cmd-args FROM COMMAND-LINE
     */
    public String getFullCommandLine() {
        return String.join(" ", args);
    }

    /**
     * Returns the total number of arguments.
     * Replaces: ACCEPT ws-num-args FROM ARGUMENT-NUMBER
     */
    public int getArgumentCount() {
        return args.length;
    }

    /**
     * Returns a specific argument by index (1-based, matching COBOL convention).
     * Replaces: DISPLAY ws-counter UPON ARGUMENT-NUMBER / ACCEPT ws-cmd-args FROM ARGUMENT-VALUE
     */
    public String getArgument(int index) {
        if (index < 1 || index > args.length) {
            return "";
        }
        return args[index - 1];
    }

    /**
     * Returns all arguments as a list.
     */
    public List<String> getAllArguments() {
        return Arrays.asList(args);
    }

    /**
     * Checks if a specific argument flag is present (case-insensitive).
     * Replaces the INSPECT/TALLYING logic from read_cmd_line_args.cbl lines 25-28.
     */
    public boolean hasFlag(String flag) {
        String fullLine = getFullCommandLine().toLowerCase();
        return fullLine.contains(flag.toLowerCase());
    }
}
