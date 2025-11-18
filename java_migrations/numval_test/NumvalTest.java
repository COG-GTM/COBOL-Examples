import java.util.Scanner;

public class NumvalTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        try {
            System.out.print("Enter first number: ");
            String firstInput = scanner.nextLine();
            
            System.out.print("Enter second number: ");
            String secondInput = scanner.nextLine();
            
            double firstNumber = parseNumber(firstInput);
            double secondNumber = parseNumber(secondInput);
            
            double total = firstNumber + secondNumber;
            
            System.out.println("Total: " + total);
            
        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid number format - " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
    
    private static double parseNumber(String input) throws NumberFormatException {
        String trimmedInput = input.trim();
        
        if (trimmedInput.isEmpty()) {
            return 0.0;
        }
        
        try {
            return Double.parseDouble(trimmedInput);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Cannot parse '" + input + "' as a number");
        }
    }
}
