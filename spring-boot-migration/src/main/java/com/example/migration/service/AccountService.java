package com.example.migration.service;

import com.example.migration.model.Account;
import com.example.migration.repository.AccountRepository;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service layer wrapping AccountRepository calls and handling business logic.
 * Replaces the main procedure division logic of sql/sql_example.cbl (lines 103-194).
 *
 * The COBOL program used a menu-driven terminal UI to:
 *   1) Display all accounts
 *   2) Display disabled accounts
 *   3) Query accounts by search string
 * This service provides the same operations as method calls.
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Replaces "display-all-accounts" paragraph (sql_example.cbl lines 201-246).
     * Opens ACCOUNT-ALL-CUR, fetches all records, and returns them.
     */
    public List<Account> getAllAccounts() {
        return accountRepository.findAllByOrderByIdAsc();
    }

    /**
     * Replaces "display-disabled-accounts" paragraph (sql_example.cbl lines 258-295).
     * Opens ACCOUNT-DISABLED-CUR for records where IS_ENABLED = 'N'.
     */
    public List<Account> getDisabledAccounts() {
        return accountRepository.findByIsEnabledOrderByIdAsc("N");
    }

    /**
     * Replaces "query-accounts" paragraph (sql_example.cbl lines 318-394).
     * The COBOL version trims user input, wraps it with '%' wildcards,
     * and uses the ACCOUNT-QUERY-CUR cursor to search across
     * first_name, last_name, phone, and address fields.
     */
    public List<Account> queryAccounts(String searchValue) {
        String wrappedSearchValue = "%" + searchValue.trim() + "%";
        return accountRepository.searchAccounts(wrappedSearchValue);
    }
}
