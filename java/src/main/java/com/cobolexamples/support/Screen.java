package com.cobolexamples.support;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public final class Screen {

    private static final int[] FG = {30, 34, 32, 36, 31, 35, 33, 37};
    private static final int[] BG = {40, 44, 42, 46, 41, 45, 43, 47};

    private Screen() {}

    private static void out(String s) {
        System.out.print(s);
        System.out.flush();
    }

    public static void clear() {
        out("\033[2J\033[H");
    }

    public static void eraseEol() {
        out("\033[K");
    }

    public static void bell() {
        out("\007");
    }

    public static void displayAt(int row, int col, String text) {
        out("\033[" + row + ";" + col + "H" + text);
    }

    public static void displayAt(int row, int col, String text, int fg, int bg) {
        out("\033[" + FG[fg & 7] + ";" + BG[bg & 7] + "m"
                + "\033[" + row + ";" + col + "H" + text + "\033[0m");
    }

    public static int[] size() {
        try {
            Process p = new ProcessBuilder("stty", "size")
                    .redirectInput(new File("/dev/tty"))
                    .redirectErrorStream(true)
                    .start();
            String s = new String(p.getInputStream().readAllBytes()).trim();
            p.waitFor(2, TimeUnit.SECONDS);
            String[] parts = s.split("\\s+");
            if (parts.length == 2) {
                return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
            }
        } catch (Exception ignored) {
        }
        return new int[]{24, 80};
    }

    public static String readSecure(String prompt) {
        java.io.Console c = System.console();
        if (c != null) {
            char[] pw = c.readPassword("%s", prompt);
            return pw == null ? "" : new String(pw);
        }
        return Console.accept(prompt);
    }

    public static String acceptWithTimeout(int seconds) {
        long deadline = System.nanoTime() + seconds * 1_000_000_000L;
        StringBuilder sb = new StringBuilder();
        try {
            while (System.nanoTime() < deadline) {
                while (System.in.available() > 0) {
                    int ch = System.in.read();
                    if (ch == -1) return sb.toString();
                    if (ch == '\n') return sb.toString();
                    if (ch != '\r') sb.append((char) ch);
                }
                Thread.sleep(20);
            }
        } catch (IOException | InterruptedException ignored) {
        }
        return "";
    }
}
