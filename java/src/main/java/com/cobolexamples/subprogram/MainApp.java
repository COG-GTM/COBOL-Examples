package com.cobolexamples.subprogram;

import com.cobolexamples.support.Console;
import com.cobolexamples.support.Field;

public class MainApp {

    private final Field wsItem1 = new Field(10);
    private final Field wsItem2 = new Field(10);

    public static void main(String[] args) {
        new MainApp().run();
    }

    void run() {
        Console.display(" ");
        wsItem1.set(Console.accept("Enter value for #1: "));
        wsItem2.set(Console.accept("Enter value for #2: "));

        displayMessage();

        Console.display("Calling sub program by content:");
        SubApp.call(new Field(10, wsItem1.get()), new Field(10, wsItem2.get()));
        displayMessage();

        Console.display("Second call of sub program should retain WS values.");
        Console.display("Calling sub program by reference:");
        SubApp.call(wsItem1, wsItem2);
        displayMessage();

        Console.display("Cancelling sub program");
        SubApp.cancel();
        Console.display("Calling sub program. WS values should be reset:");
        SubApp.call(wsItem1, wsItem2);
        displayMessage();
    }

    void displayMessage() {
        Console.display(" ");
        Console.display("-----------------------------------------------");
        Console.display("Main app: ", wsItem1, wsItem2);
    }
}
