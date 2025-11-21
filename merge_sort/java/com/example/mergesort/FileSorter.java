package com.example.mergesort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileSorter {
    
    public static List<CustomerRecord> sort(List<CustomerRecord> records, Comparator<CustomerRecord> comparator) {
        List<CustomerRecord> sortedRecords = new ArrayList<>(records);
        Collections.sort(sortedRecords, comparator);
        return sortedRecords;
    }
}
