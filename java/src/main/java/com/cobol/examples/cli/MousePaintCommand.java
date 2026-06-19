package com.cobol.examples.cli;

import com.cobol.examples.ui.ConsoleView;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.input.MouseAction;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.MouseCaptureMode;
import picocli.CommandLine.Command;

/**
 * Modern replacement for the COBOL mouse paint demo
 * ({@code mouse/mouse_example.cbl}), which relied on GnuCOBOL screen mode plus
 * the {@code COB_MOUSE_FLAGS} environment variable. Lanterna provides a portable
 * full-screen TUI with mouse capture; drag the mouse to paint, press {@code q}
 * to quit.
 */
@Command(name = "mouse-paint", description = "Full-screen mouse paint demo (drag to paint, 'q' to quit).")
public final class MousePaintCommand implements Runnable {

    @Override
    public void run() {
        ConsoleView view = new ConsoleView();
        DefaultTerminalFactory factory = new DefaultTerminalFactory()
                .setForceTextTerminal(true)
                .setMouseCaptureMode(MouseCaptureMode.CLICK_RELEASE_DRAG_MOVE);

        try (Screen screen = new TerminalScreen(factory.createTerminal())) {
            screen.startScreen();
            screen.newTextGraphics().putString(0, 0, "Drag the mouse to paint. Press 'q' to quit.");
            screen.refresh();

            TextCharacter brush = new TextCharacter('*', TextColor.ANSI.RED, TextColor.ANSI.DEFAULT);
            while (true) {
                KeyStroke key = screen.readInput();
                if (key.getKeyType() == KeyType.Character && key.getCharacter() == 'q') {
                    break;
                }
                if (key instanceof MouseAction mouse) {
                    screen.setCharacter(mouse.getPosition().getColumn(), mouse.getPosition().getRow(), brush);
                    screen.refresh();
                }
            }
        } catch (Exception e) {
            view.error("Mouse demo requires an interactive terminal: " + e.getMessage());
        }
    }
}
