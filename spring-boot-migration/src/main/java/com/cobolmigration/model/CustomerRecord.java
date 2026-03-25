package com.cobolmigration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Model class replacing the fixed-length COBOL customer record
 * in merge_sort/merge_sort_test.cbl (lines 47-53).
 * Fields map to:
 *   f-customer-id          -> customerId
 *   f-customer-last-name   -> lastName
 *   f-customer-first-name  -> firstName
 *   f-customer-contract-id -> contractId
 *   f-customer-comment     -> comment
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRecord {

    private int customerId;
    private String lastName;
    private String firstName;
    private int contractId;
    private String comment;
}
