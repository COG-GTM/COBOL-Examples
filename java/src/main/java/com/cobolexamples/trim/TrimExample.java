package com.cobolexamples.trim;

import com.cobolexamples.support.Console;
import com.cobolexamples.support.Pic;

public class TrimExample {

    private static String wsTestString1 = Pic.x("    hello world       ", 30);
    private static String wsTestString2 = Pic.x("", 30);

    public static void main(String[] args) {
        Console.display("--", wsTestString1, "--");
        Console.display("--", Pic.trim(wsTestString1), "--");
        Console.display("--", Pic.trimLeading(wsTestString1), "--");
        Console.display("--", Pic.trimTrailing(wsTestString1), "--");

        wsTestString2 = Pic.x("******************************", 30);
        Console.display(wsTestString2);
        wsTestString2 = wsTestString1;
        Console.display(wsTestString2);

        wsTestString2 = Pic.x("******************************", 30);
        Console.display(wsTestString2);
        wsTestString2 = Pic.x(Pic.trim(wsTestString1), 30);
        Console.display(wsTestString2);

        wsTestString2 = Pic.x("******************************", 30);
        Console.display(wsTestString2);
        wsTestString2 = Pic.x(Pic.trimLeading(wsTestString1), 30);
        Console.display(wsTestString2);

        wsTestString2 = Pic.x("******************************", 30);
        Console.display(wsTestString2);
        wsTestString2 = Pic.x(Pic.trimTrailing(wsTestString1), 30);
        Console.display(wsTestString2);

        Console.display("--", "    String literal    ", "--");
        Console.display("--", Pic.trim("   String literal    "), "--");
        Console.display("--", Pic.trimLeading("     String literal   "), "--");
        Console.display("--", Pic.trimTrailing("   String literal    "), "--");
    }
}
