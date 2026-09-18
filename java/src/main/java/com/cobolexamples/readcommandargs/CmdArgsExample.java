package com.cobolexamples.readcommandargs;

import com.cobolexamples.support.Console;
import com.cobolexamples.support.Pic;

public class CmdArgsExample {

    public static void main(String[] args) {
        for (String arg : args) {
            Console.display(Pic.x(arg, 99));
        }
    }
}
