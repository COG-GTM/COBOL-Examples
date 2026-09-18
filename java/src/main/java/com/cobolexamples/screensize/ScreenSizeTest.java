package com.cobolexamples.screensize;

import com.cobolexamples.support.Console;
import com.cobolexamples.support.Pic;
import com.cobolexamples.support.Screen;

public class ScreenSizeTest {

    public static void main(String[] args) {
        new ScreenSizeTest().run();
    }

    private static void displayScreenSize(int lines, int cols) {
        Screen.displayAt(2, 1,
                "-------------------------------------------------------------");
        Screen.displayAt(3, 1, "Current screen size: ");
        Screen.displayAt(4, 1, "Columns: " + Pic.z(cols, 3));
        Screen.displayAt(5, 1, "  Lines: " + Pic.z(lines, 3));
        Screen.displayAt(7, 1, "Resize and press enter to continue");
        Console.accept();
    }

    void run() {
        Screen.clear();

        Screen.displayAt(1, 1,
                "Using 'ACCEPT ... FROM LINES' and 'ACCEPT ... FROM COLUMNS' to get screen size:");
        for (int i = 0; i < 3; i++) {
            int[] size = Screen.size();
            displayScreenSize(size[0], size[1]);
        }

        Screen.clear();
        Screen.displayAt(1, 1, "Using 'CBL_GET_SCR_SIZE' to get screen size:");
        for (int i = 0; i < 3; i++) {
            int[] size = Screen.size();
            displayScreenSize(size[0], size[1]);
        }

        Screen.displayAt(9, 1, "Done.");
        System.out.println();
    }
}
