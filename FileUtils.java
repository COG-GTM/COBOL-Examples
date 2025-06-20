import java.io.*;
import java.util.*;

public class FileUtils {
    
    public static List<Customer> readCustomersFromFile(String filename) throws IOException {
        List<Customer> customers = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    customers.add(Customer.parseFromFixedWidth(line));
                }
            }
        }
        
        return customers;
    }
    
    public static void writeCustomersToFile(List<Customer> customers, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Customer customer : customers) {
                writer.println(customer.toFixedWidthString());
            }
        }
    }
    
    public static void createTestDataFiles() throws IOException {
        System.out.println("Creating test data files...");
        
        createTestFile1();
        createTestFile2();
    }
    
    private static void createTestFile1() throws IOException {
        List<Customer> eastRegionCustomers = Arrays.asList(
            new Customer(1, "last-1", "first-1", 5423, "comment-1"),
            new Customer(5, "last-5", "first-5", 12323, "comment-5"),
            new Customer(10, "last-10", "first-10", 653, "comment-10"),
            new Customer(50, "last-50", "first-50", 5050, "comment-50"),
            new Customer(25, "last-25", "first-25", 7725, "comment-25"),
            new Customer(75, "last-75", "first-75", 1175, "comment-75")
        );
        
        writeCustomersToFile(eastRegionCustomers, "test-file-1.txt");
    }
    
    private static void createTestFile2() throws IOException {
        List<Customer> westRegionCustomers = Arrays.asList(
            new Customer(999, "last-999", "first-999", 1610, "comment-99"),
            new Customer(3, "last-03", "first-03", 3331, "comment-03"),
            new Customer(30, "last-30", "first-30", 8765, "comment-30"),
            new Customer(85, "last-85", "first-85", 4567, "comment-85"),
            new Customer(24, "last-24", "first-24", 247, "comment-24")
        );
        
        writeCustomersToFile(westRegionCustomers, "test-file-2.txt");
    }
    
    public static List<Customer> mergeFilesByCustomerId(String file1, String file2) throws IOException {
        List<Customer> customers1 = readCustomersFromFile(file1);
        List<Customer> customers2 = readCustomersFromFile(file2);
        
        List<Customer> allCustomers = new ArrayList<>();
        allCustomers.addAll(customers1);
        allCustomers.addAll(customers2);
        
        allCustomers.sort(Comparator.comparingInt(Customer::getCustomerId));
        
        return allCustomers;
    }
    
    public static List<Customer> sortByContractIdDescending(List<Customer> customers) {
        List<Customer> sortedCustomers = new ArrayList<>(customers);
        sortedCustomers.sort(Comparator.comparingInt(Customer::getCustomerContractId).reversed());
        return sortedCustomers;
    }
    
    public static void displayCustomers(List<Customer> customers) {
        for (Customer customer : customers) {
            System.out.println(customer.toFixedWidthString());
        }
    }
}
