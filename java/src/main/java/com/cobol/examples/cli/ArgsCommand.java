package com.cobol.examples.cli;

import com.cobol.examples.core.args.CommandArgsService;
import com.cobol.examples.ui.ConsoleView;
import java.util.List;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "args", description = "Read command-line arguments (Picocli). Pass --test for a message.")
public final class ArgsCommand implements Runnable {

    @Parameters(description = "Arbitrary arguments to echo back.")
    private List<String> args = List.of();

    @Override
    public void run() {
        ConsoleView view = new ConsoleView();
        view.banner("Command-line arguments");
        view.keyValue("full command line", CommandArgsService.fullCommandLine(args));
        for (int i = 0; i < args.size(); i++) {
            view.keyValue("arg[" + (i + 1) + "]", args.get(i));
        }
        if (CommandArgsService.hasTestFlag(args)) {
            view.success("You entered the '--test' cmd arg!");
        }
    }
}
