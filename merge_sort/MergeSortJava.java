import java.io.*;
import java.util.*;

public class MergeSortJava {
    
    public static void main(String[] args) {
        MergeSortJava program = new MergeSortJava();
        
        program.createTestData();
        program.mergeAndDisplayFiles();
        program.sortAndDisplayFile();
        
        System.out.println("Done.");
    }
    
    public void createTestData() {
        System.out.println("Creating test data files...");
        
        createEastRegionFile();
        createWestRegionFile();
    }
    
    private void createEastRegionFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("test-file-1.txt"))) {
            Customer[] eastCustomers = {
                new Customer(1, "last-1", "first-1", 5423, "comment-1"),
                new Customer(5, "last-5", "first-5", 12323, "comment-5"),
                new Customer(10, "last-10", "first-10", 653, "comment-10"),
                new Customer(50, "last-50", "first-50", 5050, "comment-50"),
                new Customer(25, "last-25", "first-25", 7725, "comment-25"),
                new Customer(75, "last-75", "first-75", 1175, "comment-75")
            };
            
            for (Customer customer : eastCustomers) {
                writer.println(customer.toFixedWidthString());
            }
        } catch (IOException e) {
            System.err.println("Failed to create east region file: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private void createWestRegionFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("test-file-2.txt"))) {
            Customer[] westCustomers = {
                new Customer(999, "last-999", "first-999", 1610, "comment-99"),
                new Customer(3, "last-03", "first-03", 3331, "comment-03"),
                new Customer(30, "last-30", "first-30", 8765, "comment-30"),
                new Customer(85, "last-85", "first-85", 4567, "comment-85"),
                new Customer(24, "last-24", "first-24", 247, "comment-24")
            };
            
            for (Customer customer : westCustomers) {
                writer.println(customer.toFixedWidthString());
            }
        } catch (IOException e) {
            System.err.println("Failed to create west region file: " + e.getMessage());
            System.exit(1);
        }
    }
    
    public void mergeAndDisplayFiles() {
        System.out.println("Merging and sorting files...");
        
        List<Customer> allCustomers = new ArrayList<>();
        
        try (BufferedReader reader1 = new BufferedReader(new FileReader("test-file-1.txt"));
             BufferedReader reader2 = new BufferedReader(new FileReader("test-file-2.txt"))) {
            
            String line;
            while ((line = reader1.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    allCustomers.add(Customer.fromFixedWidthString(line));
                }
            }
            
            while ((line = reader2.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    allCustomers.add(Customer.fromFixedWidthString(line));
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error reading input files: " + e.getMessage());
            System.exit(1);
        }
        
        Collections.sort(allCustomers);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter("merge-output.txt"))) {
            for (Customer customer : allCustomers) {
                String output = customer.toFixedWidthString();
                System.out.println(output);
                writer.println(output);
            }
        } catch (IOException e) {
            System.err.println("Error writing merged file: " + e.getMessage());
            System.exit(1);
        }
    }
    
    public void sortAndDisplayFile() {
        System.out.println("Sorting merged file on descending contract id....");
        
        List<Customer> customers = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader("merge-output.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    customers.add(Customer.fromFixedWidthString(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading merged file: " + e.getMessage());
            System.exit(1);
        }
        
        Collections.sort(customers, new Comparator<Customer>() {
            @Override
            public int compare(Customer c1, Customer c2) {
                return Integer.compare(c2.getContractId(), c1.getContractId());
            }
        });
        
        try (PrintWriter writer = new PrintWriter(new FileWriter("sorted-contract-id.txt"))) {
            for (Customer customer : customers) {
                String output = customer.toFixedWidthString();
                System.out.println(output);
                writer.println(output);
            }
        } catch (IOException e) {
            System.err.println("Error writing sorted file: " + e.getMessage());
            System.exit(1);
        }
    }
}
