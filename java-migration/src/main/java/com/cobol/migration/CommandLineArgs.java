package com.cobol.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandLineArgs {
    private static final Logger logger = LoggerFactory.getLogger(CommandLineArgs.class);

    public static void main(String[] args) {
        int numArgs = args.length;
        
        logger.info("Processing {} command line arguments", numArgs);

        for (int counter = 0; counter < numArgs; counter++) {
            String cmdArg = args[counter];
            System.out.println(cmdArg);
            logger.debug("Argument {}: {}", counter + 1, cmdArg);
        }

        logger.info("Completed processing command line arguments");
    }
}
