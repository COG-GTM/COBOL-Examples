package com.cobolexamples.args;

public class CommandArgsExample {
    
    public static void main(String[] args) {
        int numArgs = args.length;
        
        for (int counter = 0; counter < numArgs; counter++) {
            System.out.println(args[counter]);
        }
    }
}
