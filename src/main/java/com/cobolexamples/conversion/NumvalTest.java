package com.cobolexamples.conversion;

import java.util.Scanner;

public class NumvalTest {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter first number: ");
        String firstInput = scanner.nextLine();
        
        System.out.print("Enter second number: ");
        String secondInput = scanner.nextLine();
        
        try {
            double firstNumber = Double.parseDouble(firstInput.trim());
            double secondNumber = Double.parseDouble(secondInput.trim());
            
            double total = firstNumber + secondNumber;
            
            System.out.println("Total: " + total);
        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid number format");
        } finally {
            scanner.close();
        }
    }
}
