package com.cobol.examples.ui;

import java.io.PrintStream;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

/**
 * Thin, shared presentation layer for the console examples.
 *
 * <p>In the original COBOL programs every example mixed business logic with
 * {@code DISPLAY}/{@code ACCEPT} verbs and raw screen positioning. Centralising
 * the output concerns here keeps the ported logic classes UI-free and gives the
 * whole collection a consistent, coloured look via JAnsi.
 */
public final class ConsoleView {

    private final PrintStream out;
    private final boolean color;

    public ConsoleView() {
        this(AnsiConsole.out(), true);
    }

    public ConsoleView(PrintStream out, boolean color) {
        this.out = out;
        this.color = color;
    }

    /** Prints a boxed section heading, mirroring the COBOL "===" banners. */
    public void banner(String title) {
        String bar = "=".repeat(Math.max(title.length() + 4, 20));
        out.println();
        println(bar, Ansi.Color.CYAN);
        println("  " + title, Ansi.Color.CYAN);
        println(bar, Ansi.Color.CYAN);
    }

    public void heading(String text) {
        println(text, Ansi.Color.YELLOW);
    }

    public void info(String text) {
        out.println(text);
    }

    public void keyValue(String key, Object value) {
        if (color) {
            out.println(Ansi.ansi().fgBright(Ansi.Color.GREEN).a(key).reset().a(": ").a(String.valueOf(value)));
        } else {
            out.println(key + ": " + value);
        }
    }

    public void success(String text) {
        println(text, Ansi.Color.GREEN);
    }

    public void error(String text) {
        println(text, Ansi.Color.RED);
    }

    public void blank() {
        out.println();
    }

    private void println(String text, Ansi.Color c) {
        if (color) {
            out.println(Ansi.ansi().fg(c).a(text).reset());
        } else {
            out.println(text);
        }
    }
}
