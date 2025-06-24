package com.cobol.examples.mergesort;

import java.util.*;

/**
 * Java implementation of the COBOL merge sort example from merge_sort_test.cbl.
 * This class replicates the exact functionality of the COBOL program:
 * 1. Creates test data equivalent to the two COBOL files (east and west)
 * 2. Merges the data using ascending customer ID sort (MERGE statement equivalent)
 * 3. Sorts the merged data using descending contract ID sort (SORT statement equivalent)
 * 4. Displays the results at each step
 */
public class MergeSortExample {

    public static void main(String[] args) {
        MergeSortExample example = new MergeSortExample();
        
        example.createTestData();
        example.mergeAndDisplayFiles();
        example.sortAndDisplayFile();
        
        System.out.println("Done.");
    }

    private List<Customer> eastCustomers;
    private List<Customer> westCustomers;
    private List<Customer> mergedCustomers;

    /**
     * Creates test data equivalent to the COBOL create-test-data paragraph.
     * Replicates the exact same customer records from both test files.
     */
    private void createTestData() {
        System.out.println("Creating test data files...");
        
        eastCustomers = new ArrayList<>();
        westCustomers = new ArrayList<>();

        eastCustomers.add(new Customer(1, "last-1", "first-1", 5423, "comment-1"));
        eastCustomers.add(new Customer(5, "last-5", "first-5", 12323, "comment-5"));
        eastCustomers.add(new Customer(10, "last-10", "first-10", 653, "comment-10"));
        eastCustomers.add(new Customer(50, "last-50", "first-50", 5050, "comment-50"));
        eastCustomers.add(new Customer(25, "last-25", "first-25", 7725, "comment-25"));
        eastCustomers.add(new Customer(75, "last-75", "first-75", 1175, "comment-75"));

        westCustomers.add(new Customer(999, "last-999", "first-999", 1610, "comment-99"));
        westCustomers.add(new Customer(3, "last-03", "first-03", 3331, "comment-03"));
        westCustomers.add(new Customer(30, "last-30", "first-30", 8765, "comment-30"));
        westCustomers.add(new Customer(85, "last-85", "first-85", 4567, "comment-85"));
        westCustomers.add(new Customer(24, "last-24", "first-24", 247, "comment-24"));
    }

    /**
     * Equivalent to the COBOL merge-and-display-files paragraph.
     * Merges the two customer lists using ascending customer ID order,
     * replicating the COBOL MERGE statement functionality.
     */
    private void mergeAndDisplayFiles() {
        System.out.println("Merging and sorting files...");
        
        MergeSort.mergeSort(eastCustomers, new CustomerIdAscendingComparator());
        MergeSort.mergeSort(westCustomers, new CustomerIdAscendingComparator());
        
        mergedCustomers = MergeSort.mergeTwoSortedLists(
            eastCustomers, 
            westCustomers, 
            new CustomerIdAscendingComparator()
        );
        
        for (Customer customer : mergedCustomers) {
            System.out.println(customer.toString());
        }
    }

    /**
     * Equivalent to the COBOL sort-and-display-file paragraph.
     * Sorts the merged customer list using descending contract ID order,
     * replicating the COBOL SORT statement functionality.
     */
    private void sortAndDisplayFile() {
        System.out.println("Sorting merged file on descending contract id....");
        
        MergeSort.mergeSort(mergedCustomers, new ContractIdDescendingComparator());
        
        for (Customer customer : mergedCustomers) {
            System.out.println(customer.toString());
        }
    }
}
