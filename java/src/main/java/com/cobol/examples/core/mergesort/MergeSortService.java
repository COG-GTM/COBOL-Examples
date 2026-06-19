package com.cobol.examples.core.mergesort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Pure-logic port of {@code merge_sort/merge_sort_test.cbl}.
 *
 * <p>The COBOL program used the {@code MERGE} verb to combine two pre-sorted
 * files on ascending customer id and the {@code SORT} verb to re-order the
 * merged result on descending contract id. Both operations are expressed here
 * as ordinary, side-effect-free list transformations.
 */
public final class MergeSortService {

    private MergeSortService() {
    }

    /** Equivalent of {@code MERGE ... ON ASCENDING KEY f-customer-id}. */
    public static List<CustomerRecord> mergeById(List<CustomerRecord> east, List<CustomerRecord> west) {
        List<CustomerRecord> sortedEast = sorted(east, Comparator.comparingInt(CustomerRecord::id));
        List<CustomerRecord> sortedWest = sorted(west, Comparator.comparingInt(CustomerRecord::id));

        List<CustomerRecord> merged = new ArrayList<>(sortedEast.size() + sortedWest.size());
        int i = 0;
        int j = 0;
        while (i < sortedEast.size() && j < sortedWest.size()) {
            if (sortedEast.get(i).id() <= sortedWest.get(j).id()) {
                merged.add(sortedEast.get(i++));
            } else {
                merged.add(sortedWest.get(j++));
            }
        }
        merged.addAll(sortedEast.subList(i, sortedEast.size()));
        merged.addAll(sortedWest.subList(j, sortedWest.size()));
        return merged;
    }

    /** Equivalent of {@code SORT ... ON DESCENDING KEY f-customer-contract-id}. */
    public static List<CustomerRecord> sortByContractIdDescending(List<CustomerRecord> records) {
        return sorted(records, Comparator.comparingInt(CustomerRecord::contractId).reversed());
    }

    private static List<CustomerRecord> sorted(List<CustomerRecord> in, Comparator<CustomerRecord> cmp) {
        List<CustomerRecord> copy = new ArrayList<>(in);
        copy.sort(cmp);
        return copy;
    }
}
