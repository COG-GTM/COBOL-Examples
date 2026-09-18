package com.cobolexamples.displaytiming;

import com.cobolexamples.support.Console;
import com.cobolexamples.support.Pic;
import com.cobolexamples.support.Screen;

public class DisplayTiming {

    static final int MAX_ROWS = 20;
    static final int MAX_COLS = 80;
    static final int MAX_TIMES_TO_RUN = 10;
    static final int REFRESHES = 100;

    private final long[] diffs = new long[MAX_TIMES_TO_RUN];

    public static void main(String[] args) {
        new DisplayTiming().run();
    }

    private static String timeStamp(long nanos) {
        long cs = nanos / 10_000_000;
        long hh = (cs / 360_000) % 100;
        long mm = (cs / 6_000) % 60;
        long ss = (cs / 100) % 60;
        long nn = cs % 100;
        return Pic.nine(hh, 2) + Pic.nine(mm, 2) + Pic.nine(ss, 2) + Pic.nine(nn, 2);
    }

    private void computeAndDisplayDiff(int run, long start, long end) {
        diffs[run] = end - start;
        long cs = diffs[run] / 10_000_000;
        String diff = "00" + "00" + Pic.nine(cs / 100, 2) + Pic.nine(cs % 100, 2);
        Screen.displayAt(25, 1, timeStamp(start));
        Screen.displayAt(26, 1, timeStamp(end));
        Screen.displayAt(27, 1, diff);
    }

    private void computeAndDisplayAverage() {
        long totalCs = 0;
        for (int i = 0; i < MAX_TIMES_TO_RUN; i++) {
            long cs = diffs[i] / 10_000_000;
            Screen.displayAt(i + 1, 1,
                    "00" + "00" + Pic.nine(cs / 100, 2) + Pic.nine(cs % 100, 2));
            totalCs += cs;
        }
        long avgCs = totalCs / MAX_TIMES_TO_RUN;
        Screen.displayAt(12, 1,
                "000" + "000" + Pic.nine(avgCs / 100, 3) + Pic.nine(avgCs % 100, 3));
        Screen.displayAt(13, 1,
                "000" + "000" + Pic.nine(avgCs / 100, 3) + Pic.nine(avgCs % 100, 3));
        Screen.displayAt(14, 1,
                Pic.nine((avgCs / 100) % 100, 2) + "." + Pic.nine(avgCs % 100, 2));
        Console.accept();
    }

    void run() {
        Console.display("Press enter to start...");
        Console.accept();
        Screen.clear();

        for (int run = 0; run < MAX_TIMES_TO_RUN; run++) {
            long start = System.nanoTime();
            for (int refresh = 0; refresh < REFRESHES; refresh++) {
                for (int row = 1; row <= MAX_ROWS; row++) {
                    for (int col = 1; col <= MAX_COLS; col++) {
                        Screen.displayAt(row, col, "@");
                    }
                }
            }
            computeAndDisplayDiff(run, start, System.nanoTime());
        }

        Screen.clear();
        computeAndDisplayAverage();
        Screen.clear();

        for (int run = 0; run < MAX_TIMES_TO_RUN; run++) {
            long start = System.nanoTime();
            for (int refresh = 0; refresh < REFRESHES; refresh++) {
                for (int row = 1; row <= MAX_ROWS; row++) {
                    for (int col = 1; col <= MAX_COLS; col++) {
                        Screen.displayAt(row, col, "@");
                    }
                }
            }
            computeAndDisplayDiff(run, start, System.nanoTime());
        }

        Screen.clear();
        computeAndDisplayAverage();
    }
}
