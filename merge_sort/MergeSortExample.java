import java.io.*;
import java.util.*;

public class MergeSortExample {
    private static final String TEST_FILE_1 = "test-file-1.txt";
    private static final String TEST_FILE_2 = "test-file-2.txt";
    private static final String MERGE_OUTPUT = "merge-output.txt";
    private static final String SORTED_CONTRACT_ID = "sorted-contract-id.txt";
    
    public static void main(String[] args) {
        try {
            createTestData();
            mergeAndDisplayFiles();
            sortAndDisplayFile();
            System.out.println("Done.");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createTestData() throws IOException {
        System.out.println("Creating test data files...");
        
        List<Customer> eastCustomers = Arrays.asList(
            new Customer(1, "last-1", "first-1", 5423, "comment-1"),
            new Customer(5, "last-5", "first-5", 12323, "comment-5"),
            new Customer(10, "last-10", "first-10", 653, "comment-10"),
            new Customer(50, "last-50", "first-50", 5050, "comment-50"),
            new Customer(25, "last-25", "first-25", 7725, "comment-25"),
            new Customer(75, "last-75", "first-75", 1175, "comment-75")
        );
        
        List<Customer> westCustomers = Arrays.asList(
            new Customer(999, "last-999", "first-999", 1610, "comment-99"),
            new Customer(3, "last-03", "first-03", 3331, "comment-03"),
            new Customer(30, "last-30", "first-30", 8765, "comment-30"),
            new Customer(85, "last-85", "first-85", 4567, "comment-85"),
            new Customer(24, "last-24", "first-24", 247, "comment-24")
        );
        
        writeCustomersToFile(eastCustomers, TEST_FILE_1);
        writeCustomersToFile(westCustomers, TEST_FILE_2);
    }
    
    private static void writeCustomersToFile(List<Customer> customers, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Customer customer : customers) {
                writer.println(customer.toString());
            }
        }
    }
    
    private static void mergeAndDisplayFiles() throws IOException {
        System.out.println("Merging and sorting files...");
        
        List<Customer> allCustomers = new ArrayList<>();
        
        allCustomers.addAll(readCustomersFromFile(TEST_FILE_1));
        allCustomers.addAll(readCustomersFromFile(TEST_FILE_2));
        
        allCustomers.sort(Comparator.comparingInt(Customer::getCustomerId));
        
        writeCustomersToFile(allCustomers, MERGE_OUTPUT);
        
        for (Customer customer : allCustomers) {
            System.out.println(customer);
        }
    }
    
    private static List<Customer> readCustomersFromFile(String filename) throws IOException {
        List<Customer> customers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                customers.add(parseCustomerFromLine(line));
            }
        }
        return customers;
    }
    
    private static void sortAndDisplayFile() throws IOException {
        System.out.println("Sorting merged file on descending contract id....");
        
        List<Customer> customers = readCustomersFromFile(MERGE_OUTPUT);
        
        customers.sort(Comparator.comparingInt(Customer::getContractId).reversed());
        
        writeCustomersToFile(customers, SORTED_CONTRACT_ID);
        
        for (Customer customer : customers) {
            System.out.println(customer);
        }
    }
    
    private static Customer parseCustomerFromLine(String line) {
        if (line.length() < 135) {
            line = String.format("%-135s", line);
        }
        
        int customerId = Integer.parseInt(line.substring(0, 5));
        String lastName = line.substring(5, 55).trim();
        String firstName = line.substring(55, 105).trim();
        int contractId = Integer.parseInt(line.substring(105, 110));
        String comment = line.substring(110, 135).trim();
        
        return new Customer(customerId, lastName, firstName, contractId, comment);
    }
}
