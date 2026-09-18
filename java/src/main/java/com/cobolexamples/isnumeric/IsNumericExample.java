package com.cobolexamples.isnumeric;

import com.cobolexamples.support.Console;
import com.cobolexamples.support.Pic;

public class IsNumericExample {

    private String wsUserInput;
    private String wsUserInputJustified;

    public static void main(String[] args) {
        IsNumericExample app = new IsNumericExample();
        app.processPlain();
        app.processZeroFill();
        app.processTrim();
    }

    void processPlain() {
        wsUserInput = Console.acceptField("(plain) Enter a value: ", 10);
        if (Pic.isNumeric(wsUserInput)) {
            Console.display(wsUserInput, " is numeric!");
        } else {
            Console.display(wsUserInput, " is not numeric.");
        }
    }

    void processZeroFill() {
        wsUserInputJustified = Pic.justifiedRight(Console.accept(
                "(right justify, zero fill) Enter another value: "), 10);
        wsUserInputJustified = wsUserInputJustified.replace(' ', '0');
        if (Pic.isNumeric(wsUserInputJustified)) {
            Console.display(wsUserInputJustified, " is numeric!");
        } else {
            Console.display(wsUserInputJustified, " is not numeric.");
        }
    }

    void processTrim() {
        wsUserInput = Console.acceptField("(trim) Enter a third value: ", 10);
        if (Pic.isNumeric(Pic.trim(wsUserInput))) {
            Console.display(Pic.trim(wsUserInput), " is numeric!");
        } else {
            Console.display(Pic.trim(wsUserInput), " is not numeric.");
        }
    }
}
