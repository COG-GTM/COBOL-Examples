package com.cobolexamples.readcommandargs;

import com.cobolexamples.support.Console;
import com.cobolexamples.support.Pic;

public class ReadCmdLineArgs {

    public static void main(String[] args) {
        Console.display(" ");
        Console.display("Pass arg '--test' for special message");

        String wsCmdArgs = Pic.x(String.join(" ", args), 256);
        Console.display("Full command line args: ", wsCmdArgs);

        String lower = wsCmdArgs.toLowerCase();
        int count = 0;
        for (int i = lower.indexOf("--test"); i >= 0; i = lower.indexOf("--test", i + 1)) {
            count++;
        }

        if (count > 0) {
            Console.display("You entered the '--test' cmd arg!");
        }
        Console.display(" ");
    }
}
