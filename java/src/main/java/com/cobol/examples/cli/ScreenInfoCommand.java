package com.cobol.examples.cli;

import com.cobol.examples.ui.ConsoleView;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import picocli.CommandLine.Command;

/**
 * Modern replacement for {@code screen_size/get_screen_size.cbl}, which used
 * {@code ACCEPT ... FROM LINES/COLUMNS}. Lanterna reports the terminal size.
 */
@Command(name = "screen-info", description = "Report the terminal size (was ACCEPT FROM LINES/COLUMNS).")
public final class ScreenInfoCommand implements Runnable {

    @Override
    public void run() {
        ConsoleView view = new ConsoleView();
        DefaultTerminalFactory factory = new DefaultTerminalFactory().setForceTextTerminal(true);
        try (Terminal terminal = factory.createTerminal()) {
            TerminalSize size = terminal.getTerminalSize();
            view.banner("Terminal size");
            view.keyValue("columns", size.getColumns());
            view.keyValue("rows", size.getRows());
        } catch (Exception e) {
            view.error("Could not read terminal size: " + e.getMessage());
        }
    }
}
