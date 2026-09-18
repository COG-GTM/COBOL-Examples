package com.cobolexamples.displaytest;

import com.cobolexamples.support.Screen;

public class DisplayTest {

    public static void main(String[] args) {
        Screen.displayAt(5, 5, "hello world");
        Screen.displayAt(6, 5, "hello world");
        Screen.displayAt(7, 5, "hello world");
        System.out.println();
        Screen.displayAt(8, 5, "hello world");
        Screen.eraseEol();
        Screen.displayAt(9, 5, "hello world");
        Screen.bell();
        Screen.displayAt(10, 5, "hello world", 6, 3);
        System.out.println();
    }
}
