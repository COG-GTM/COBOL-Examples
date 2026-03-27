package com.example.cobolmigration.service;

import com.example.cobolmigration.model.Account;
import com.example.cobolmigration.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Business logic layer for account operations.
 * Replaces the terminal menu logic from sql/sql_example.cbl (lines 158-185).
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Replaces menu option 1 — display-all-accounts paragraph (sql/sql_example.cbl lines 201-246).
     * Uses ACCOUNT-ALL-CUR cursor to fetch all accounts ordered by ID.
     */
    public List<Account> getAllAccounts() {
        return accountRepository.findAllByOrderByIdAsc();
    }

    /**
     * Replaces menu option 2 — display-disabled-accounts paragraph (sql/sql_example.cbl lines 258-295).
     * Uses ACCOUNT-DISABLED-CUR cursor to fetch accounts where IS_ENABLED = 'N'.
     */
    public List<Account> getDisabledAccounts() {
        return accountRepository.findByIsEnabledOrderByIdAsc("N");
    }

    /**
     * Replaces menu option 3 — query-accounts paragraph (sql/sql_example.cbl lines 318-394).
     * The COBOL code trims the search term and wraps it with '%' wildcards (lines 333-343),
     * then uses LIKE matching across first_name, last_name, phone, and address.
     *
     * @param searchTerm the raw search input from the user
     * @return accounts matching the search across all text fields
     */
    public List<Account> searchAccounts(String searchTerm) {
        String trimmed = searchTerm != null ? searchTerm.trim() : "";
        String wildcardTerm = "%" + trimmed + "%";
        return accountRepository.searchAccounts(wildcardTerm);
    }
}
