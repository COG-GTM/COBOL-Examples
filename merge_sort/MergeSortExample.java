import java.io.IOException;
import java.util.*;

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
        
        FileHandler.writeCustomersToFile(eastCustomers, "test-file-1.txt");
        FileHandler.writeCustomersToFile(westCustomers, "test-file-2.txt");
    }
    
    private static void mergeAndDisplayFiles() throws IOException {
        System.out.println("Merging and sorting files...");
        
        List<Customer> file1Customers = FileHandler.readCustomersFromFile("test-file-1.txt");
        List<Customer> file2Customers = FileHandler.readCustomersFromFile("test-file-2.txt");
        
        List<Customer> mergedCustomers = new ArrayList<>();
        mergedCustomers.addAll(file1Customers);
        mergedCustomers.addAll(file2Customers);
        
        mergedCustomers.sort(Comparator.comparingInt(Customer::getCustomerId));
        
        FileHandler.writeCustomersToFile(mergedCustomers, "merge-output.txt");
        
        FileHandler.displayCustomers(mergedCustomers, "Merged file contents:");
    }
    
    private static void sortAndDisplayFile() throws IOException {
        System.out.println("Sorting merged file on descending contract id....");
        
        List<Customer> customers = FileHandler.readCustomersFromFile("merge-output.txt");
        
        customers.sort(Comparator.comparingInt(Customer::getContractId).reversed());
        
        FileHandler.writeCustomersToFile(customers, "sorted-contract-id.txt");
        
        FileHandler.displayCustomers(customers, "Sorted by contract ID (descending):");
    }
}
