package com.cobolmigration.cli;

import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Command-line argument handler replacing read_command_args/read_cmd_line_args.cbl.
 *
 * <p>COBOL equivalent (read_cmd_line_args.cbl):
 * <pre>
 *   ACCEPT ws-cmd-args FROM COMMAND-LINE
 *   INSPECT FUNCTION LOWER-CASE(ws-cmd-args)
 *       TALLYING ws-test-arg-count FOR ALL "--test"
 * </pre>
 *
 * <p>Uses Spring Boot's {@link ApplicationArguments} instead of COBOL's
 * ACCEPT FROM COMMAND-LINE.
 *
 * @see read_command_args/read_cmd_line_args.cbl
 */
@Component
@Profile("cli")
public class CommandLineArgsRunner {

    private final ApplicationArguments applicationArguments;

    public CommandLineArgsRunner(ApplicationArguments applicationArguments) {
        this.applicationArguments = applicationArguments;
    }

    /**
     * Checks if the "--test" argument was provided on the command line.
     * Replaces the COBOL INSPECT TALLYING pattern.
     *
     * @return true if --test was passed
     */
    public boolean hasTestArg() {
        return applicationArguments.containsOption("test")
                || applicationArguments.getNonOptionArgs().stream()
                .anyMatch(arg -> arg.equalsIgnoreCase("--test"));
    }

    /**
     * Returns the full command line arguments as a single string,
     * equivalent to COBOL's ACCEPT ws-cmd-args FROM COMMAND-LINE.
     *
     * @return concatenated command line arguments
     */
    public String getFullCommandLine() {
        return String.join(" ", applicationArguments.getSourceArgs());
    }

    /**
     * Returns all non-option arguments.
     *
     * @return array of non-option arguments
     */
    public String[] getNonOptionArgs() {
        return applicationArguments.getNonOptionArgs().toArray(new String[0]);
    }
}
