package com.cobolexamples.comptest;

import com.cobolexamples.support.Console;
import com.cobolexamples.support.Pic;

public class CompConversionTest {

    public static void main(String[] args) {
        int wsCompVal = 12;
        wsCompVal = wsCompVal * 2;
        Console.display("COMP: ", Pic.nine(wsCompVal, 3));

        int wsDispVal = wsCompVal;
        Console.display("DISP:", Pic.nine(wsDispVal, 3));

        Console.display("DYNA:", Pic.z(wsCompVal, 3));

        String in = Console.accept("INPUT: ");
        int wsInput = Pic.isNumeric(Pic.trim(in)) ? Integer.parseInt(Pic.trim(in)) : 0;
        Console.display("INPUT: ", Pic.nine(wsInput, 3));

        wsCompVal = wsInput;
        Console.display("COMP: ", Pic.nine(wsCompVal, 3));
    }
}
