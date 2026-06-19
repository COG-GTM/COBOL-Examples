package com.cobol.examples.cli;

import com.cobol.examples.ui.ConsoleView;
import java.io.Console;
import picocli.CommandLine.Command;

/**
 * Modern replacement for COBOL's {@code ACCEPT ... WITH SECURE / NO-ECHO}
 * (see {@code accept/accept-secure.cbl} and the {@code no-echo} clause in
 * {@code accept/accept.cbl}). Uses {@link Console#readPassword()} so the entered
 * value is never echoed to the terminal.
 */
@Command(name = "secure-input", description = "Read input without echoing it (was ACCEPT ... SECURE).")
public final class SecureInputCommand implements Runnable {

    @Override
    public void run() {
        ConsoleView view = new ConsoleView();
        Console console = System.console();
        if (console == null) {
            view.error("No interactive console available (run from a real terminal).");
            return;
        }
        char[] secret = console.readPassword("Enter a value (no echo): ");
        view.success("Received " + (secret == null ? 0 : secret.length) + " characters (value not shown).");
        if (secret != null) {
            java.util.Arrays.fill(secret, '\0');
        }
    }
}
