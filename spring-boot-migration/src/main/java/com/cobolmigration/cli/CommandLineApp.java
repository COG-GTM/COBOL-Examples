package com.cobolmigration.cli;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.List;

/**
 * CLI argument parser using Picocli.
 * Replaces the COBOL command-line argument handling from:
 *   - read_command_args/read_cmd_line_args.cbl:
 *       ACCEPT ws-cmd-args FROM COMMAND-LINE
 *       INSPECT FUNCTION LOWER-CASE(ws-cmd-args) TALLYING ws-test-arg-count FOR ALL "--test"
 *   - read_command_args/read_specific_cmd_line_args.cbl:
 *       ACCEPT ws-arg FROM ARGUMENT-VALUE
 *
 * In COBOL, command-line arguments are read via:
 *   ACCEPT var FROM COMMAND-LINE     -> full command line string
 *   ACCEPT var FROM ARGUMENT-NUMBER  -> count of arguments
 *   DISPLAY idx UPON ARGUMENT-NUMBER -> set argument index
 *   ACCEPT var FROM ARGUMENT-VALUE   -> get specific argument
 */
@Component
@Command(name = "cobol-migration",
        mixinStandardHelpOptions = true,
        version = "1.0.0",
        description = "COBOL to Spring Boot Migration CLI")
public class CommandLineApp implements Runnable {

    @Option(names = {"--test"}, description = "Enable test mode (replaces --test arg check from COBOL)")
    private boolean testMode;

    @Option(names = {"--search", "-s"}, description = "Search value for account queries")
    private String searchValue;

    @Option(names = {"--format", "-f"}, description = "Output format: json, xml, text", defaultValue = "text")
    private String format;

    @Parameters(description = "Positional arguments (replaces ACCEPT FROM ARGUMENT-VALUE)")
    private List<String> positionalArgs;

    @Override
    public void run() {
        System.out.println("COBOL Migration CLI");
        System.out.println("-------------------");

        if (testMode) {
            System.out.println("You entered the '--test' cmd arg!");
        }

        if (searchValue != null) {
            System.out.println("Search value: " + searchValue);
        }

        System.out.println("Output format: " + format);

        if (positionalArgs != null && !positionalArgs.isEmpty()) {
            System.out.println("Number of arguments: " + positionalArgs.size());
            for (int i = 0; i < positionalArgs.size(); i++) {
                System.out.println("Argument " + (i + 1) + ": " + positionalArgs.get(i));
            }
        }
    }

    public boolean isTestMode() {
        return testMode;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public String getFormat() {
        return format;
    }

    public List<String> getPositionalArgs() {
        return positionalArgs;
    }
}
