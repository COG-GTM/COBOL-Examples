package com.cobol.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MergeSortTest {
    private static final Logger logger = LoggerFactory.getLogger(MergeSortTest.class);
    
    private static final String TEST_FILE_1 = "test-file-1.txt";
    private static final String TEST_FILE_2 = "test-file-2.txt";
    private static final String MERGED_FILE = "merge-output.txt";
    private static final String SORTED_FILE = "sorted-contract-id.txt";

    public static void main(String[] args) {
        try {
            createTestData();
            mergeAndDisplayFiles();
            sortAndDisplayFile();
            
            System.out.println("Done.");
            logger.info("Merge and sort operations completed successfully");
        } catch (IOException e) {
            logger.error("Error during file operations: {}", e.getMessage(), e);
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void createTestData() throws IOException {
        logger.info("Creating test data files...");
        System.out.println("Creating test data files...");

        List<Customer> eastCustomers = new ArrayList<>();
        eastCustomers.add(new Customer(1, "last-1", "first-1", 5423, "comment-1"));
        eastCustomers.add(new Customer(5, "last-5", "first-5", 12323, "comment-5"));
        eastCustomers.add(new Customer(10, "last-10", "first-10", 653, "comment-10"));
        eastCustomers.add(new Customer(50, "last-50", "first-50", 5050, "comment-50"));
        eastCustomers.add(new Customer(25, "last-25", "first-25", 7725, "comment-25"));
        eastCustomers.add(new Customer(75, "last-75", "first-75", 1175, "comment-75"));

        writeCustomersToFile(TEST_FILE_1, eastCustomers);

        List<Customer> westCustomers = new ArrayList<>();
        westCustomers.add(new Customer(999, "last-999", "first-999", 1610, "comment-99"));
        westCustomers.add(new Customer(3, "last-03", "first-03", 3331, "comment-03"));
        westCustomers.add(new Customer(30, "last-30", "first-30", 8765, "comment-30"));
        westCustomers.add(new Customer(85, "last-85", "first-85", 4567, "comment-85"));
        westCustomers.add(new Customer(24, "last-24", "first-24", 247, "comment-24"));

        writeCustomersToFile(TEST_FILE_2, westCustomers);
        
        logger.info("Test data files created successfully");
    }

    private static void mergeAndDisplayFiles() throws IOException {
        logger.info("Merging and sorting files...");
        System.out.println("Merging and sorting files...");

        List<Customer> allCustomers = new ArrayList<>();
        
        allCustomers.addAll(readCustomersFromFile(TEST_FILE_1));
        allCustomers.addAll(readCustomersFromFile(TEST_FILE_2));

        allCustomers.sort(Comparator.comparingInt(Customer::getCustomerId));

        writeCustomersToFile(MERGED_FILE, allCustomers);

        List<Customer> mergedCustomers = readCustomersFromFile(MERGED_FILE);
        for (Customer customer : mergedCustomers) {
            System.out.println(customer);
        }
        
        logger.info("Files merged and sorted by customer ID (ascending)");
    }

    private static void sortAndDisplayFile() throws IOException {
        logger.info("Sorting merged file on descending contract id....");
        System.out.println("Sorting merged file on descending contract id....");

        List<Customer> customers = readCustomersFromFile(MERGED_FILE);

        customers.sort(Comparator.comparingInt(Customer::getCustomerContractId).reversed());

        writeCustomersToFile(SORTED_FILE, customers);

        List<Customer> sortedCustomers = readCustomersFromFile(SORTED_FILE);
        for (Customer customer : sortedCustomers) {
            System.out.println(customer);
        }
        
        logger.info("File sorted by contract ID (descending)");
    }

    private static void writeCustomersToFile(String filename, List<Customer> customers) throws IOException {
        Path path = Paths.get(filename);
        
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (Customer customer : customers) {
                writer.write(customer.toFileFormat());
                writer.newLine();
            }
        } catch (IOException e) {
            logger.error("Failed to write to file {}: {}", filename, e.getMessage());
            throw new IOException("Failed to open file for output: " + filename, e);
        }
    }

    private static List<Customer> readCustomersFromFile(String filename) throws IOException {
        Path path = Paths.get(filename);
        List<Customer> customers = new ArrayList<>();

        if (!Files.exists(path)) {
            logger.error("File does not exist: {}", filename);
            throw new IOException("File does not exist: " + filename);
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    customers.add(Customer.fromFileFormat(line));
                }
            }
        } catch (IOException e) {
            logger.error("Error reading file {}: {}", filename, e.getMessage());
            throw new IOException("Error opening file: " + filename, e);
        }

        return customers;
    }
}
