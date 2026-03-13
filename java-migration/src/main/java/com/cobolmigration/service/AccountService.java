package com.cobolmigration.service;

import com.cobolmigration.model.Account;
import com.cobolmigration.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for account operations.
 *
 * Migrated from: sql/sql_example.cbl
 *
 * Replaces the COBOL program's main menu operations:
 *   1) Display all accounts (display-all-accounts paragraph)
 *   2) Display disabled accounts (display-disabled-accounts paragraph)
 *   3) Query/search accounts (query-accounts paragraph)
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Retrieves all accounts ordered by ID.
     * Replaces the display-all-accounts paragraph using ACCOUNT-ALL-CUR cursor.
     */
    public List<Account> listAllAccounts() {
        return accountRepository.findAllByOrderByIdAsc();
    }

    /**
     * Retrieves all disabled accounts (IS_ENABLED = 'N') ordered by ID.
     * Replaces the display-disabled-accounts paragraph using ACCOUNT-DISABLED-CUR cursor.
     */
    public List<Account> listDisabledAccounts() {
        return accountRepository.findByIsEnabledOrderByIdAsc("N");
    }

    /**
     * Searches accounts by a search term across firstName, lastName, phone, and address.
     * Replaces the query-accounts paragraph using ACCOUNT-QUERY-CUR cursor.
     *
     * The search term is automatically wrapped with '%' wildcards to match
     * the COBOL behavior where '%' is prepended and appended to the trimmed
     * search string before the LIKE query.
     *
     * @param searchTerm the raw search term (without wildcards)
     * @return list of matching accounts ordered by ID
     */
    public List<Account> searchAccounts(String searchTerm) {
        String wildcardSearch = "%" + searchTerm.trim() + "%";
        return accountRepository.searchAccounts(wildcardSearch);
    }
}
