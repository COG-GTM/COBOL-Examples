import java.util.Arrays;

/**
 * MergeSort implements the divide-and-conquer merge sort algorithm.
 * 
 * This implementation provides a true merge sort algorithm using the divide-and-conquer
 * approach, unlike the COBOL version which uses built-in MERGE and SORT statements.
 * 
 * Time Complexity: O(n log n) in all cases (best, average, worst)
 * Space Complexity: O(n) for the temporary arrays used during merging
 * 
 * The algorithm works by:
 * 1. Divide: Split the array into two halves
 * 2. Conquer: Recursively sort both halves
 * 3. Combine: Merge the sorted halves back together
 */
public class MergeSort {
    
    /**
     * Sorts an array of CustomerRecord objects using merge sort algorithm.
     * 
     * @param array The array to be sorted
     * @throws IllegalArgumentException if array is null
     */
    public static void sort(CustomerRecord[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        
        if (array.length <= 1) {
            return; // Already sorted
        }
        
        mergeSort(array, 0, array.length - 1);
    }
    
    /**
     * Recursive merge sort implementation.
     * 
     * @param array The array to sort
     * @param left The starting index of the subarray
     * @param right The ending index of the subarray
     */
    private static void mergeSort(CustomerRecord[] array, int left, int right) {
        if (left < right) {
            int middle = left + (right - left) / 2;
            
            mergeSort(array, left, middle);
            
            mergeSort(array, middle + 1, right);
            
            merge(array, left, middle, right);
        }
    }
    
    /**
     * Merges two sorted subarrays into a single sorted subarray.
     * 
     * The subarrays are array[left...middle] and array[middle+1...right]
     * 
     * @param array The array containing the subarrays
     * @param left The starting index of the first subarray
     * @param middle The ending index of the first subarray
     * @param right The ending index of the second subarray
     */
    private static void merge(CustomerRecord[] array, int left, int middle, int right) {
        int leftSize = middle - left + 1;
        int rightSize = right - middle;
        
        CustomerRecord[] leftArray = new CustomerRecord[leftSize];
        CustomerRecord[] rightArray = new CustomerRecord[rightSize];
        
        System.arraycopy(array, left, leftArray, 0, leftSize);
        System.arraycopy(array, middle + 1, rightArray, 0, rightSize);
        
        int leftIndex = 0;    // Initial index of left subarray
        int rightIndex = 0;   // Initial index of right subarray
        int mergedIndex = left; // Initial index of merged subarray
        
        while (leftIndex < leftSize && rightIndex < rightSize) {
            if (leftArray[leftIndex].compareTo(rightArray[rightIndex]) <= 0) {
                array[mergedIndex] = leftArray[leftIndex];
                leftIndex++;
            } else {
                array[mergedIndex] = rightArray[rightIndex];
                rightIndex++;
            }
            mergedIndex++;
        }
        
        while (leftIndex < leftSize) {
            array[mergedIndex] = leftArray[leftIndex];
            leftIndex++;
            mergedIndex++;
        }
        
        while (rightIndex < rightSize) {
            array[mergedIndex] = rightArray[rightIndex];
            rightIndex++;
            mergedIndex++;
        }
    }
    
    /**
     * Sorts an array by a specific field using a custom comparator approach.
     * This method demonstrates how to sort by different fields like contract ID.
     * 
     * @param array The array to sort
     * @param sortBy The field to sort by ("id", "lastName", "firstName", "contractId")
     */
    public static void sortByField(CustomerRecord[] array, String sortBy) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        
        if (array.length <= 1) {
            return;
        }
        
        mergeSortByField(array, 0, array.length - 1, sortBy);
    }
    
    /**
     * Recursive merge sort with custom field comparison.
     */
    private static void mergeSortByField(CustomerRecord[] array, int left, int right, String sortBy) {
        if (left < right) {
            int middle = left + (right - left) / 2;
            
            mergeSortByField(array, left, middle, sortBy);
            mergeSortByField(array, middle + 1, right, sortBy);
            mergeByField(array, left, middle, right, sortBy);
        }
    }
    
    /**
     * Merge function with custom field comparison.
     */
    private static void mergeByField(CustomerRecord[] array, int left, int middle, int right, String sortBy) {
        int leftSize = middle - left + 1;
        int rightSize = right - middle;
        
        CustomerRecord[] leftArray = new CustomerRecord[leftSize];
        CustomerRecord[] rightArray = new CustomerRecord[rightSize];
        
        System.arraycopy(array, left, leftArray, 0, leftSize);
        System.arraycopy(array, middle + 1, rightArray, 0, rightSize);
        
        int leftIndex = 0, rightIndex = 0, mergedIndex = left;
        
        while (leftIndex < leftSize && rightIndex < rightSize) {
            if (compareByField(leftArray[leftIndex], rightArray[rightIndex], sortBy) <= 0) {
                array[mergedIndex] = leftArray[leftIndex];
                leftIndex++;
            } else {
                array[mergedIndex] = rightArray[rightIndex];
                rightIndex++;
            }
            mergedIndex++;
        }
        
        while (leftIndex < leftSize) {
            array[mergedIndex] = leftArray[leftIndex];
            leftIndex++;
            mergedIndex++;
        }
        
        while (rightIndex < rightSize) {
            array[mergedIndex] = rightArray[rightIndex];
            rightIndex++;
            mergedIndex++;
        }
    }
    
    /**
     * Compares two CustomerRecord objects by the specified field.
     */
    private static int compareByField(CustomerRecord a, CustomerRecord b, String sortBy) {
        switch (sortBy.toLowerCase()) {
            case "id":
                return Integer.compare(a.getCustomerId(), b.getCustomerId());
            case "lastname":
                return a.getCustomerLastName().compareTo(b.getCustomerLastName());
            case "firstname":
                return a.getCustomerFirstName().compareTo(b.getCustomerFirstName());
            case "contractid":
                return Integer.compare(a.getCustomerContractId(), b.getCustomerContractId());
            default:
                return a.compareTo(b); // Default to natural ordering
        }
    }
    
    /**
     * Utility method to check if an array is sorted.
     * 
     * @param array The array to check
     * @return true if the array is sorted in ascending order, false otherwise
     */
    public static boolean isSorted(CustomerRecord[] array) {
        if (array == null || array.length <= 1) {
            return true;
        }
        
        for (int i = 1; i < array.length; i++) {
            if (array[i - 1].compareTo(array[i]) > 0) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Utility method to print an array of CustomerRecord objects.
     * 
     * @param array The array to print
     * @param title Optional title to display before the array
     */
    public static void printArray(CustomerRecord[] array, String title) {
        if (title != null && !title.isEmpty()) {
            System.out.println(title);
            System.out.println("=".repeat(title.length()));
        }
        
        if (array == null) {
            System.out.println("Array is null");
            return;
        }
        
        if (array.length == 0) {
            System.out.println("Array is empty");
            return;
        }
        
        for (CustomerRecord record : array) {
            System.out.println(record);
        }
        System.out.println();
    }
}
