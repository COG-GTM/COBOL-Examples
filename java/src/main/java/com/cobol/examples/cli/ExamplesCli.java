package com.cobol.examples.cli;

import org.fusesource.jansi.AnsiConsole;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Unified entry point for the example collection.
 *
 * <p>This is the central UI modernisation: instead of ~20 standalone COBOL
 * programs each compiled and run separately, every example is a discoverable
 * subcommand of one self-documenting CLI ({@code cobol-examples <name>}), with
 * consistent help, colour and argument parsing courtesy of Picocli.
 */
@Command(
        name = "cobol-examples",
        mixinStandardHelpOptions = true,
        version = "cobol-examples 1.0.0",
        header = "Modern Java port of the COBOL example collection.",
        description = "Run any example as a subcommand. Use 'help <command>' for details.",
        subcommands = {
                MergeSortCommand.class,
                SearchCommand.class,
                NumericCommand.class,
                TextCommand.class,
                RedefinesCommand.class,
                ReportCommand.class,
                JsonCommand.class,
                XmlCommand.class,
                SqlCommand.class,
                ArgsCommand.class,
                SubProgramCommand.class,
                DisplayCommand.class,
                ScreenInfoCommand.class,
                SecureInputCommand.class,
                MousePaintCommand.class
        })
public final class ExamplesCli implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        try {
            int exitCode = new CommandLine(new ExamplesCli()).execute(args);
            System.exit(exitCode);
        } finally {
            AnsiConsole.systemUninstall();
        }
    }
}
