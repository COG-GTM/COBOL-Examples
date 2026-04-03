package com.cobolmigration.service;

import com.cobolmigration.model.Account;
import com.cobolmigration.repository.AccountRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Replaces the menu-driven logic from sql/sql_example.cbl lines 158-185.
 * Error handling replaces check-sql-state paragraph (lines 443-472)
 * using Spring's DataAccessException.
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Gets all accounts ordered by ID ascending.
     * Replaces: display-all-accounts paragraph using ACCOUNT-ALL-CUR cursor.
     */
    public List<Account> getAllAccounts() {
        try {
            return accountRepository.findAllByOrderByIdAsc();
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("Error fetching all accounts: " + e.getMessage(), e);
        }
    }

    /**
     * Gets all disabled accounts (IS_ENABLED = 'N').
     * Replaces: display-disabled-accounts paragraph using ACCOUNT-DISABLED-CUR cursor.
     */
    public List<Account> getDisabledAccounts() {
        try {
            return accountRepository.findByIsEnabledOrderByIdAsc("N");
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("Error fetching disabled accounts: " + e.getMessage(), e);
        }
    }

    /**
     * Searches accounts by matching search value against first name, last name,
     * phone, or address fields.
     * Replaces: query-accounts paragraph using ACCOUNT-QUERY-CUR cursor.
     */
    public List<Account> searchAccounts(String searchValue) {
        try {
            String trimmed = searchValue != null ? searchValue.trim() : "";
            return accountRepository.searchAccounts(trimmed);
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("Error searching accounts: " + e.getMessage(), e);
        }
    }

    /**
     * Custom exception replacing COBOL's check-sql-state error handling.
     */
    public static class DatabaseOperationException extends RuntimeException {
        public DatabaseOperationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
