package com.cobolmigration.service;

import com.cobolmigration.entity.Account;
import com.cobolmigration.exception.DatabaseException;
import com.cobolmigration.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for Account operations.
 * Replaces the COBOL menu-driven operations in sql_example.cbl (lines 158-185).
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Retrieves all accounts ordered by ID.
     * Replaces menu option 1: display-all-accounts paragraph (sql_example.cbl lines 201-246).
     */
    public List<Account> getAllAccounts() {
        try {
            return accountRepository.findAllByOrderByIdAsc();
        } catch (Exception e) {
            throw new DatabaseException("Error retrieving all accounts", e);
        }
    }

    /**
     * Retrieves all disabled accounts (is_enabled = 'N').
     * Replaces menu option 2: display-disabled-accounts paragraph (sql_example.cbl lines 258-295).
     */
    public List<Account> getDisabledAccounts() {
        try {
            return accountRepository.findByIsEnabled("N");
        } catch (Exception e) {
            throw new DatabaseException("Error retrieving disabled accounts", e);
        }
    }

    /**
     * Searches accounts by a search term across first_name, last_name, phone, and address.
     * Replaces menu option 3: query-accounts paragraph (sql_example.cbl lines 318-394).
     * The COBOL version uses LIKE with '%' wildcards; the JPQL query handles this.
     */
    public List<Account> searchAccounts(String searchTerm) {
        try {
            return accountRepository.searchAccounts(searchTerm);
        } catch (Exception e) {
            throw new DatabaseException("Error searching accounts with term: " + searchTerm, e);
        }
    }
}
