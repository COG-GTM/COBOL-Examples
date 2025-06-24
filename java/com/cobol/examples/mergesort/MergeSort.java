package com.cobol.examples.mergesort;

import java.util.*;

/**
 * Generic merge sort implementation that can sort any type of objects
 * using a provided Comparator. This replaces COBOL's MERGE and SORT statements
 * with an in-memory merge sort algorithm.
 */
public class MergeSort {

    /**
     * Sorts a list using merge sort algorithm with the provided comparator.
     * This is the main entry point that replaces COBOL's SORT statement.
     * 
     * @param list the list to sort
     * @param comparator the comparator to determine sort order
     * @param <T> the type of elements in the list
     */
    public static <T> void mergeSort(List<T> list, Comparator<T> comparator) {
        if (list == null || list.size() <= 1) {
            return;
        }
        mergeSortHelper(list, 0, list.size() - 1, comparator);
    }

    /**
     * Recursive helper method for merge sort.
     * 
     * @param list the list to sort
     * @param left the left boundary index
     * @param right the right boundary index
     * @param comparator the comparator to determine sort order
     * @param <T> the type of elements in the list
     */
    private static <T> void mergeSortHelper(List<T> list, int left, int right, Comparator<T> comparator) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            
            mergeSortHelper(list, left, mid, comparator);
            mergeSortHelper(list, mid + 1, right, comparator);
            
            merge(list, left, mid, right, comparator);
        }
    }

    /**
     * Merges two sorted sublists into one sorted list.
     * This implements the core merge operation similar to COBOL's MERGE statement.
     * 
     * @param list the list containing the sublists to merge
     * @param left the left boundary index
     * @param mid the middle index separating the two sublists
     * @param right the right boundary index
     * @param comparator the comparator to determine merge order
     * @param <T> the type of elements in the list
     */
    private static <T> void merge(List<T> list, int left, int mid, int right, Comparator<T> comparator) {
        List<T> leftList = new ArrayList<>(list.subList(left, mid + 1));
        List<T> rightList = new ArrayList<>(list.subList(mid + 1, right + 1));

        int i = 0, j = 0, k = left;

        while (i < leftList.size() && j < rightList.size()) {
            if (comparator.compare(leftList.get(i), rightList.get(j)) <= 0) {
                list.set(k++, leftList.get(i++));
            } else {
                list.set(k++, rightList.get(j++));
            }
        }

        while (i < leftList.size()) {
            list.set(k++, leftList.get(i++));
        }

        while (j < rightList.size()) {
            list.set(k++, rightList.get(j++));
        }
    }

    /**
     * Merges two already sorted lists into a new sorted list.
     * This directly replaces COBOL's MERGE statement functionality.
     * 
     * @param list1 the first sorted list
     * @param list2 the second sorted list
     * @param comparator the comparator to determine merge order
     * @param <T> the type of elements in the lists
     * @return a new list containing all elements from both input lists in sorted order
     */
    public static <T> List<T> mergeTwoSortedLists(List<T> list1, List<T> list2, Comparator<T> comparator) {
        List<T> merged = new ArrayList<>();
        int i = 0, j = 0;

        while (i < list1.size() && j < list2.size()) {
            if (comparator.compare(list1.get(i), list2.get(j)) <= 0) {
                merged.add(list1.get(i++));
            } else {
                merged.add(list2.get(j++));
            }
        }

        while (i < list1.size()) {
            merged.add(list1.get(i++));
        }

        while (j < list2.size()) {
            merged.add(list2.get(j++));
        }

        return merged;
    }
}
