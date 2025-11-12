package com.cobol.examples;

/**
 * Java migration of read_specific_cmd_line_args.cbl
 * 
 * This program demonstrates:
 * - Command-line argument handling
 * - Iterating through all arguments
 * - Displaying each argument value
 * 
 * COBOL-to-Java mappings:
 * - ACCEPT FROM ARGUMENT-NUMBER -> args.length
 * - DISPLAY UPON ARGUMENT-NUMBER -> loop counter (implicit in Java)
 * - ACCEPT FROM ARGUMENT-VALUE -> args[i]
 * - PERFORM VARYING -> Java for loop
 */
public class CommandLineArgsExample {

    public static void main(String[] args) {
        int numArgs = args.length;

        for (int counter = 0; counter < numArgs; counter++) {
            String cmdArg = args[counter];
            System.out.println(cmdArg);
        }
    }
}
