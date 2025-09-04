/**
 * MergeSortDemo demonstrates the merge sort algorithm implementation
 * with CustomerRecord objects that mirror the COBOL data structure.
 * 
 * This demo creates test data similar to the COBOL merge_sort_test.cbl
 * and shows the merge sort algorithm in action.
 */
public class MergeSortDemo {
    
    public static void main(String[] args) {
        System.out.println("Java Merge Sort Implementation Demo");
        System.out.println("===================================");
        System.out.println();
        
        CustomerRecord[] customers = createTestData();
        
        MergeSort.printArray(customers, "Original Unsorted Data:");
        
        CustomerRecord[] customersByIdCopy = customers.clone();
        System.out.println("Sorting by Customer ID (ascending)...");
        MergeSort.sort(customersByIdCopy);
        MergeSort.printArray(customersByIdCopy, "Sorted by Customer ID:");
        
        System.out.println("Is array sorted by ID? " + MergeSort.isSorted(customersByIdCopy));
        System.out.println();
        
        CustomerRecord[] customersByContractCopy = customers.clone();
        System.out.println("Sorting by Contract ID (ascending)...");
        MergeSort.sortByField(customersByContractCopy, "contractId");
        MergeSort.printArray(customersByContractCopy, "Sorted by Contract ID:");
        
        CustomerRecord[] customersByLastNameCopy = customers.clone();
        System.out.println("Sorting by Last Name (ascending)...");
        MergeSort.sortByField(customersByLastNameCopy, "lastName");
        MergeSort.printArray(customersByLastNameCopy, "Sorted by Last Name:");
        
        demonstratePerformance();
    }
    
    /**
     * Creates test data that mirrors the data from the COBOL merge_sort_test.cbl file.
     * This includes data from both "east" and "west" files that would be merged in COBOL.
     */
    private static CustomerRecord[] createTestData() {
        return new CustomerRecord[] {
            new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"),
            new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"),
            new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"),
            new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"),
            new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"),
            new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75"),
            
            new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"),
            new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"),
            new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"),
            new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"),
            new CustomerRecord(24, "last-24", "first-24", 247, "comment-24")
        };
    }
    
    /**
     * Demonstrates the performance characteristics of the merge sort algorithm
     * with a larger dataset.
     */
    private static void demonstratePerformance() {
        System.out.println("Performance Demonstration");
        System.out.println("========================");
        
        int[] sizes = {100, 1000, 10000};
        
        for (int size : sizes) {
            CustomerRecord[] largeDataset = generateRandomData(size);
            
            long startTime = System.nanoTime();
            MergeSort.sort(largeDataset);
            long endTime = System.nanoTime();
            
            long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds
            
            System.out.printf("Sorted %d records in %d ms%n", size, duration);
            System.out.printf("Is sorted? %b%n", MergeSort.isSorted(largeDataset));
            System.out.println();
        }
    }
    
    /**
     * Generates random CustomerRecord data for performance testing.
     */
    private static CustomerRecord[] generateRandomData(int size) {
        CustomerRecord[] data = new CustomerRecord[size];
        
        for (int i = 0; i < size; i++) {
            int id = (int) (Math.random() * 100000);
            String lastName = "LastName" + (int) (Math.random() * 1000);
            String firstName = "FirstName" + (int) (Math.random() * 1000);
            int contractId = (int) (Math.random() * 100000);
            String comment = "Comment" + (int) (Math.random() * 100);
            
            data[i] = new CustomerRecord(id, lastName, firstName, contractId, comment);
        }
        
        return data;
    }
}
