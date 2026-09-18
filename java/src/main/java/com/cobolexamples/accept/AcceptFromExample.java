package com.cobolexamples.accept;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.cobolexamples.support.Console;
import com.cobolexamples.support.Pic;
import com.cobolexamples.support.Screen;

public class AcceptFromExample {

    private String wsInput = " ".repeat(50);
    private String lastExceptionStatus = "0000000000";

    public static void main(String[] args) {
        new AcceptFromExample().run(args);
    }

    private String acceptFromEnvironment(String name) {
        String v = Console.env(name);
        if (v.isEmpty()) {
            lastExceptionStatus = "0000001537";
            return "";
        }
        lastExceptionStatus = "0000000000";
        return v;
    }

    void run(String[] args) {
        Console.display(" ");
        Console.display("ACCEPT... FROM... Example Program");
        Console.display("---------------------------------");
        Console.display("Pass command line parameters to demo that feature");
        Console.display(" ");

        wsInput = Pic.x(String.join(" ", args), 50);
        Console.display("accept from command-line: ", wsInput);

        wsInput = Pic.x(Integer.toString(args.length), 50);
        Console.display("accept from argument-number: ", wsInput);

        if (args.length > 0) {
            for (String arg : args) {
                wsInput = Pic.x(arg, 50);
                Console.display("accept from argument-value: ", wsInput);
            }
        }

        Console.display("Before environment setting set:");
        wsInput = Pic.x(acceptFromEnvironment("COB_TEST_ENV_KEY"), 50);
        Console.display("accept from environment: ", wsInput);

        wsInput = Pic.x(lastExceptionStatus, 50);
        Console.display("accept from exception status: ", wsInput);

        Console.setEnv("COB_TEST_ENV_KEY", "NOW SET!");

        Console.display("After environment setting set:");
        wsInput = Pic.x(acceptFromEnvironment("COB_TEST_ENV_KEY"), 50);
        Console.display("accept from environment: ", wsInput);

        LocalDate today = LocalDate.now();
        wsInput = Pic.x(today.format(DateTimeFormatter.ofPattern("yyMMdd")), 50);
        Console.display("accept from date: ", wsInput);

        wsInput = Pic.x(today.format(DateTimeFormatter.ofPattern("yyyyMMdd")), 50);
        Console.display("accept from date yyyymmdd: ", wsInput);

        wsInput = Pic.x(today.format(DateTimeFormatter.ofPattern("yy")) 
                + Pic.nine(today.getDayOfYear(), 3), 50);
        Console.display("accept from day: ", wsInput);

        wsInput = Pic.x(today.format(DateTimeFormatter.ofPattern("yyyy"))
                + Pic.nine(today.getDayOfYear(), 3), 50);
        Console.display("accept from day yyyyddd: ", wsInput);

        LocalTime now = LocalTime.now();
        wsInput = Pic.x(now.format(DateTimeFormatter.ofPattern("HHmmss"))
                + Pic.nine(now.getNano() / 10_000_000, 2), 50);
        Console.display("accept from time: ", wsInput);

        wsInput = Pic.x(Integer.toString(today.getDayOfWeek().getValue()), 50);
        Console.display("accept from day-of-week: ", wsInput);

        wsInput = Pic.x(System.getProperty("user.name"), 50);
        Console.display("accept from user name: ", wsInput);

        wsInput = Pic.x(Console.accept("Enter value: "), 50);
        Console.display("accept from console: ", wsInput);

        Console.display("Press enter to enter screen mode.");
        Console.accept();

        int[] size = Screen.size();
        Screen.displayAt(2, 1, "accept from lines: ");
        Screen.displayAt(2, 20, Pic.x(Integer.toString(size[0]), 50));
        Screen.displayAt(3, 1, "accept from columns: ");
        Screen.displayAt(3, 22, Pic.x(Integer.toString(size[1]), 50));

        Screen.displayAt(4, 1, "Using CBL_GET_SCR_SIZE instead: ");
        Screen.displayAt(5, 1, "Num lines: ");
        Screen.displayAt(5, 14, Pic.nine(size[0], 3));
        Screen.displayAt(6, 1, "Num cols: ");
        Screen.displayAt(6, 14, Pic.nine(size[1], 3));
        System.out.println();
    }
}
