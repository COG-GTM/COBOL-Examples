package com.cobol.examples.mergesort;

import java.util.Comparator;

/**
 * Comparator for sorting customers by contract ID in descending order.
 * This replaces the COBOL "on descending key f-customer-contract-id" clause.
 */
public class ContractIdDescendingComparator implements Comparator<Customer> {
    
    @Override
    public int compare(Customer c1, Customer c2) {
        return Integer.compare(c2.getContractId(), c1.getContractId());
    }
}
