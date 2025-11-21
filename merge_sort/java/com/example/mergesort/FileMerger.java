package com.example.mergesort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FileMerger {
    
    public static List<CustomerRecord> merge(List<CustomerRecord> file1, List<CustomerRecord> file2, 
                                              Comparator<CustomerRecord> comparator) {
        List<CustomerRecord> merged = new ArrayList<>();
        
        int i = 0;
        int j = 0;
        
        while (i < file1.size() && j < file2.size()) {
            if (comparator.compare(file1.get(i), file2.get(j)) <= 0) {
                merged.add(file1.get(i));
                i++;
            } else {
                merged.add(file2.get(j));
                j++;
            }
        }
        
        while (i < file1.size()) {
            merged.add(file1.get(i));
            i++;
        }
        
        while (j < file2.size()) {
            merged.add(file2.get(j));
            j++;
        }
        
        return merged;
    }
}
