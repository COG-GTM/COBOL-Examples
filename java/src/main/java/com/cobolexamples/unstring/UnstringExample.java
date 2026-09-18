package com.cobolexamples.unstring;

import com.cobolexamples.support.Console;
import com.cobolexamples.support.Pic;
import com.cobolexamples.support.Unstring;

public class UnstringExample {

    private String wsSourceStr = " ".repeat(30);
    private String wsPart1 = " ".repeat(15);
    private String wsPart2 = " ".repeat(15);
    private final int[] wsPointer = new int[1];
    private final int[] wsSingleFieldsFilled = new int[1];
    private final int[] wsMultiFieldsFilled = new int[1];

    public static void main(String[] args) {
        new UnstringExample().run();
    }

    void run() {
        wsSourceStr = Pic.x("Hello World", 30);
        example1();
        example2();
        example3();
        example4();
        example5();
        example6();
    }

    private static void header(String title) {
        Console.display(" ");
        Console.display("=================================================");
        Console.display(title);
        Console.display(" ");
    }

    void example1() {
        header("EX 1 : SIMPLE UNSTRING");
        Console.display("SOURCE STRING: ", wsSourceStr);

        Unstring u = new Unstring(wsSourceStr).delimit(" ").dest(15).dest(15);
        u.execute();
        wsPart1 = u.destValue(0);
        wsPart2 = u.destValue(1);

        Console.display("PART1: ", wsPart1);
        Console.display("PART2: ", wsPart2);
    }

    void example2() {
        header("EX 2 : UNSTRING MULTIPLE TIMES INTO SAME DEST.");
        wsPointer[0] = 1;
        Console.display("SOURCE STRING: ", wsSourceStr);

        for (int i = 0; i < 2; i++) {
            Unstring u = new Unstring(wsSourceStr).delimitAll(" ").dest(15);
            if (u.execute(wsPointer, null)) {
                Console.display("ERROR: OVERFLOW");
            } else {
                Console.display("Successfully unstrung.");
            }
            wsPart1 = u.destValue(0);
            Console.display("PART VALUE: ", wsPart1);
            Console.display("POINTER: ", Pic.nine(wsPointer[0], 5));
        }
    }

    void example3() {
        header("EX 3 : UNSTRING INTO EXPLICIT FIELDS");
        wsPointer[0] = 1;
        Console.display("SOURCE STRING: ", wsSourceStr);

        Unstring u = new Unstring(wsSourceStr).delimitAll(" ").dest(15).dest(15);
        if (u.execute(wsPointer, null)) {
            Console.display("ERROR: OVERFLOW");
        } else {
            Console.display("Successfully unstrung.");
        }
        wsPart1 = u.destValue(0);
        wsPart2 = u.destValue(1);

        Console.display("PART1: ", wsPart1);
        Console.display("PART2: ", wsPart2);
        Console.display("POINTER: ", Pic.nine(wsPointer[0], 5));
    }

    void example4() {
        header("EX 4 : UNSTRING WITH MULTIPLE DELIMITERS ");
        wsPointer[0] = 1;
        wsSourceStr = Pic.x("A<B<CD>E%FG!HIJ|KL!MN>OP#QR!ST", 30);
        Console.display("SOURCE STRING: ", wsSourceStr);

        while (wsPointer[0] <= wsSourceStr.length()) {
            StringBuilder delim = new StringBuilder();
            int[] count = new int[1];
            Unstring u = new Unstring(wsSourceStr)
                    .delimitAll("<").delimit(">").delimit("!").delimit("|")
                    .dest(5, delim, count);
            u.execute(wsPointer, wsSingleFieldsFilled);

            Console.display(" ");
            Console.display("VALUE: ", u.destValue(0));
            Console.display("DELIMITER: ", delim);
            Console.display("CHAR COUNT:", count[0]);
            Console.display("CURRENT POINTER: ", Pic.nine(wsPointer[0], 5));
            Console.display("TOTAL FIELDS FILLED: ", Pic.nine(wsSingleFieldsFilled[0], 2));
            Console.display("-------------------------------------------");
        }
    }

    void example5() {
        header("EX 5 : UNSTRING WITH MULTIPLE DELIMITERS INTO MULTIPLE DESTINATIONS");
        wsSourceStr = Pic.x("A<B<CD>EFG!HIJ|KLMN>O", 30);
        Console.display("SOURCE STRING: ", wsSourceStr);

        StringBuilder[] delims = new StringBuilder[6];
        int[][] counts = new int[6][1];
        Unstring u = new Unstring(wsSourceStr)
                .delimitAll("<").delimitAll(">").delimit("!").delimit("|");
        for (int i = 0; i < 6; i++) {
            delims[i] = new StringBuilder();
            counts[i] = new int[1];
            u.dest(5, delims[i], counts[i]);
        }
        u.execute(new int[]{1}, wsMultiFieldsFilled);

        for (int i = 0; i < 6; i++) {
            Console.display(" ");
            Console.display("STRING NUMBER: +", Pic.nine(i + 1, 9));
            Console.display("VALUE: ", u.destValue(i));
            Console.display("DELIMITER: ", delims[i]);
            Console.display("CHAR COUNT:", counts[i][0]);
            Console.display("-------------------------------------------");
        }
        Console.display("TOTALS: ");
        Console.display("FIELDS FILLED: ", Pic.nine(wsMultiFieldsFilled[0], 2));
    }

    void example6() {
        header("EX 6 : UNSTRING FORMATTED NUMBER");
        String wsSourceNum = "$123,456.12";
        Console.display("SOURCE VALUE: ", wsSourceNum);

        StringBuilder d1 = new StringBuilder(), d2 = new StringBuilder(), d3 = new StringBuilder();
        int[] c1 = new int[1], c2 = new int[1], c3 = new int[1];
        Unstring u = new Unstring(wsSourceNum.substring(1))
                .delimit(",").delimit(".")
                .dest(3, d1, c1).dest(3, d2, c2).dest(3, d3, c3);
        u.execute();

        Console.display("PART 1: ", numericDest(u.destValue(0)));
        Console.display("PART 2: ", numericDest(u.destValue(1)));
        Console.display("PART 3: ", numericDest(u.destValue(2)));
        Console.display(" ");
    }

    private static String numericDest(String v) {
        String t = Pic.trim(v);
        return Pic.isNumeric(t) ? Pic.nine(Long.parseLong(t), 3) : Pic.x(t, 3);
    }
}
