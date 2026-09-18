package com.cobolexamples.accept;

import com.cobolexamples.support.Console;
import com.cobolexamples.support.Pic;
import com.cobolexamples.support.Screen;

public class AcceptExample {

    private String wsInput = " ".repeat(16);

    public static void main(String[] args) {
        new AcceptExample().run();
    }

    void run() {
        wsInput = Console.acceptField("Simple accept. Enter a value: ", 16);
        Console.display("You entered: ", wsInput);

        Console.display("Press any key to enter screen mode.");
        Console.accept();

        Screen.displayAt(1, 1, "Enter value or wait 3 seconds: ");
        wsInput = Pic.x(Screen.acceptWithTimeout(3), 16);
        Screen.displayAt(1, 32, "");
        Screen.displayAt(2, 1, "You entered: ");
        Screen.displayAt(2, 14, wsInput);

        Screen.displayAt(3, 1, "Enter 16 chars to auto skip: ");
        wsInput = Console.acceptField(null, 16);
        Screen.displayAt(4, 1, "You entered: ");
        Screen.displayAt(4, 14, wsInput);

        Screen.displayAt(5, 1, "Enter a value (no echo): ");
        wsInput = Pic.x(Screen.readSecure(""), 16);
        Screen.displayAt(6, 1, "You entered: ");
        Screen.displayAt(6, 14, wsInput);

        Screen.displayAt(7, 1, "Enter a value: ");
        wsInput = Pic.x(Console.accept().toUpperCase(), 16);
        Screen.displayAt(8, 1, "You entered: ");
        Screen.displayAt(8, 14, wsInput);
    }
}
