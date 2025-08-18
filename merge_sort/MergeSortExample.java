import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class MergeSortExample {
    
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
        
        writeCustomersToFile(eastCustomers, "test-file-1.txt");
        writeCustomersToFile(westCustomers, "test-file-2.txt");
    }
    
    private static void writeCustomersToFile(List<Customer> customers, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Customer customer : customers) {
                writer.println(customer.toString());
            }
        }
    }
    
    private static List<Customer> readCustomersFromFile(String filename) throws IOException {
        List<Customer> customers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    customers.add(Customer.fromString(line));
                }
            }
        }
        return customers;
    }
    
    private static void mergeAndDisplayFiles() throws IOException {
        System.out.println("Merging and sorting files...");
        
        List<Customer> mergedCustomers = mergeFiles("test-file-1.txt", "test-file-2.txt");
        
        writeCustomersToFile(mergedCustomers, "merge-output.txt");
        
        for (Customer customer : mergedCustomers) {
            System.out.println(customer.toString());
        }
    }
    
    public static List<Customer> mergeFiles(String file1, String file2) throws IOException {
        List<Customer> customers1 = readCustomersFromFile(file1);
        List<Customer> customers2 = readCustomersFromFile(file2);
        
        customers1.sort(Comparator.comparing(Customer::getCustomerId));
        customers2.sort(Comparator.comparing(Customer::getCustomerId));
        
        List<Customer> merged = new ArrayList<>();
        int i = 0, j = 0;
        
        while (i < customers1.size() && j < customers2.size()) {
            Customer c1 = customers1.get(i);
            Customer c2 = customers2.get(j);
            
            if (c1.getCustomerId() <= c2.getCustomerId()) {
                merged.add(c1);
                i++;
            } else {
                merged.add(c2);
                j++;
            }
        }
        
        while (i < customers1.size()) {
            merged.add(customers1.get(i));
            i++;
        }
        
        while (j < customers2.size()) {
            merged.add(customers2.get(j));
            j++;
        }
        
        return merged;
    }
    
    private static void sortAndDisplayFile() throws IOException {
        System.out.println("Sorting merged file on descending contract id....");
        
        List<Customer> customers = readCustomersFromFile("merge-output.txt");
        
        customers.sort(Comparator.comparing(Customer::getContractId).reversed());
        
        writeCustomersToFile(customers, "sorted-contract-id.txt");
        
        for (Customer customer : customers) {
            System.out.println(customer.toString());
        }
    }
}
