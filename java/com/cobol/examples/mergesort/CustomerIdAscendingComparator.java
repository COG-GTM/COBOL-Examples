package com.cobol.examples.mergesort;

import java.util.Comparator;

/**
 * Comparator for sorting customers by customer ID in ascending order.
 * This replaces the COBOL "on ascending key f-customer-id" clause.
 */
public class CustomerIdAscendingComparator implements Comparator<Customer> {
    
    @Override
    public int compare(Customer c1, Customer c2) {
        return Integer.compare(c1.getCustomerId(), c2.getCustomerId());
    }
}
