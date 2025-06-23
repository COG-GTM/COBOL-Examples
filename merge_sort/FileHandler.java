import java.io.*;
import java.util.*;

public class FileHandler {
    
    public static void writeCustomersToFile(List<Customer> customers, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Customer customer : customers) {
                writer.println(customer.toFixedWidthString());
            }
        }
    }
    
    public static List<Customer> readCustomersFromFile(String filename) throws IOException {
        List<Customer> customers = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() >= 135) {
                    Customer customer = parseCustomerFromLine(line);
                    customers.add(customer);
                }
            }
        }
        
        return customers;
    }
    
    private static Customer parseCustomerFromLine(String line) {
        int customerId = Integer.parseInt(line.substring(0, 5));
        String lastName = line.substring(5, 55).trim();
        String firstName = line.substring(55, 105).trim();
        int contractId = Integer.parseInt(line.substring(105, 110));
        String comment = line.substring(110, 135).trim();
        
        return new Customer(customerId, lastName, firstName, contractId, comment);
    }
    
    public static void displayCustomers(List<Customer> customers, String title) {
        System.out.println(title);
        for (Customer customer : customers) {
            System.out.println(customer.toFixedWidthString());
        }
        System.out.println();
    }
}
