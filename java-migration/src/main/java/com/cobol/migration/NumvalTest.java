package com.cobol.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class NumvalTest {
    private static final Logger logger = LoggerFactory.getLogger(NumvalTest.class);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter first number: ");
            String firstInput = scanner.nextLine();

            System.out.print("Enter second number: ");
            String secondInput = scanner.nextLine();

            double firstNumber = parseNumericValue(firstInput);
            double secondNumber = parseNumericValue(secondInput);

            double total = firstNumber + secondNumber;

            System.out.println("Total: " + total);

            logger.info("Successfully computed total: {}", total);
        } catch (NumberFormatException e) {
            logger.error("Error parsing numeric input: {}", e.getMessage());
            System.err.println("Error: Invalid numeric input - " + e.getMessage());
            System.exit(1);
        } finally {
            scanner.close();
        }
    }

    private static double parseNumericValue(String input) throws NumberFormatException {
        if (input == null || input.trim().isEmpty()) {
            throw new NumberFormatException("Input cannot be empty");
        }

        String trimmedInput = input.trim();

        try {
            return Double.parseDouble(trimmedInput);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Cannot parse '" + trimmedInput + "' as a number");
        }
    }
}
