package com.cobolexamples.accept;

import com.cobolexamples.support.Pic;
import com.cobolexamples.support.Screen;

public class AcceptSecure {

    public static void main(String[] args) {
        Screen.displayAt(1, 1, "Enter password: ");
        String wsPassword = Pic.x(Screen.readSecure(""), 16);
        Screen.displayAt(2, 4, "You entered: ");
        Screen.displayAt(2, 17, wsPassword);
        System.out.println();
    }
}
