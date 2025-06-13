package com.cognition.cobol.migration.service;

import com.cognition.cobol.migration.model.Customer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MergeSortService {
    private final FileProcessor fileProcessor;
    
    public MergeSortService(FileProcessor fileProcessor) {
        this.fileProcessor = fileProcessor;
    }
    
    public List<Customer> mergeFilesSortedByCustomerId(String file1, String file2) {
        List<Customer> customers1 = fileProcessor.readCustomersFromFile(file1);
        List<Customer> customers2 = fileProcessor.readCustomersFromFile(file2);
        
        List<Customer> mergedCustomers = new ArrayList<>();
        mergedCustomers.addAll(customers1);
        mergedCustomers.addAll(customers2);
        
        return mergedCustomers.stream()
                .sorted(Comparator.comparingInt(Customer::getCustomerId))
                .collect(Collectors.toList());
    }
    
    public List<Customer> sortByContractIdDescending(List<Customer> customers) {
        return customers.stream()
                .sorted(Comparator.comparingInt(Customer::getContractId).reversed())
                .collect(Collectors.toList());
    }
}
