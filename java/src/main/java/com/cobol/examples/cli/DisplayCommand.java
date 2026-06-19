package com.cobol.examples.cli;

import com.cobol.examples.ui.ConsoleView;
import picocli.CommandLine.Command;

@Command(name = "display", description = "DISPLAY options (color, banners) via the shared ConsoleView.")
public final class DisplayCommand implements Runnable {

    @Override
    public void run() {
        ConsoleView view = new ConsoleView();
        view.banner("DISPLAY styles");
        view.heading("This is a heading (was foreground/background-color in COBOL).");
        view.info("Plain text line.");
        view.keyValue("key", "value");
        view.success("Success message.");
        view.error("Error message.");
    }
}
