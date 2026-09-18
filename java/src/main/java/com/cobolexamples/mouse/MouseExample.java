package com.cobolexamples.mouse;

import com.cobolexamples.support.Screen;

public class MouseExample {

    private int drawColor = 1;
    private boolean exit;

    public static void main(String[] args) {
        new MouseExample().run();
    }

    private static void rawMode(boolean on) {
        if (System.console() == null) return;
        try {
            new ProcessBuilder("stty", on ? "-echo" : "echo",
                    on ? "-icanon" : "icanon")
                    .redirectInput(new java.io.File("/dev/tty"))
                    .start().waitFor();
        } catch (Exception ignored) {
        }
    }

    void run() {
        rawMode(true);
        try {
            Screen.displayAt(19, 1, "Current Draw color:");
            Screen.displayAt(20, 1,
                    "Esc to exit. Number keys to change cursor draw color."
                            + " Left mouse down to draw current color.",
                    7, 1);

            while (!exit) {
                Screen.displayAt(19, 21, " ", 7, drawColor);

                String in = "";
                try {
                    if (System.in.available() > 0) {
                        int ch = System.in.read();
                        if (ch == 27) { exit = true; break; }
                        if (ch > 0) in = String.valueOf((char) ch).toUpperCase();
                    } else {
                        Thread.sleep(50);
                        continue;
                    }
                } catch (java.io.IOException e) {
                    break;
                }

                if (in.equals("Q")) break;
                if (in.length() == 1 && Character.isDigit(in.charAt(0))) {
                    drawColor = Math.min(Character.digit(in.charAt(0), 10), 7);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            rawMode(false);
        }
        System.out.println();
    }
}
