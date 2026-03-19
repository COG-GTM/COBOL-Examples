package com.cobolmigration.service;

import com.cobolmigration.model.Account;
import com.cobolmigration.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for Account operations.
 *
 * <p>Replaces the PROCEDURE DIVISION paragraphs in {@code sql/sql_example.cbl}
 * (lines 103-185) which implement a menu-driven flow for account queries.</p>
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Retrieves all accounts ordered by ID.
     *
     * <p>Replaces the {@code display-all-accounts} paragraph (lines 201-246)
     * which opens ACCOUNT-ALL-CUR, fetches all rows, and displays them.</p>
     */
    public List<Account> getAllAccounts() {
        return accountRepository.findAllByOrderByIdAsc();
    }

    /**
     * Retrieves all disabled accounts ordered by ID.
     *
     * <p>Replaces the {@code display-disabled-accounts} paragraph (lines 258-295)
     * which opens ACCOUNT-DISABLED-CUR and fetches rows where IS_ENABLED = 'N'.</p>
     */
    public List<Account> getDisabledAccounts() {
        return accountRepository.findByEnabledFalseOrderByIdAsc();
    }

    /**
     * Searches accounts by matching the query against first name, last name,
     * phone, or address.
     *
     * <p>Replaces the {@code query-accounts} paragraph (lines 318-394) which
     * trims the user input, wraps it with '%' wildcards, and opens
     * ACCOUNT-QUERY-CUR.</p>
     *
     * @param query the search term to match against account fields
     * @return list of matching accounts
     */
    public List<Account> searchAccounts(String query) {
        String searchValue = "%" + query + "%";
        return accountRepository.searchAccounts(searchValue);
    }
}
