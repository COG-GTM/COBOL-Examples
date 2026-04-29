package com.example.cobolmigration.service;

import com.example.cobolmigration.entity.Account;
import com.example.cobolmigration.repository.AccountRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service encapsulating business logic from the COBOL procedure division
 * (sql/sql_example.cbl lines 103-194).
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Replaces the display-all-accounts paragraph (lines 201-246).
     */
    public List<Account> getAllAccounts() {
        return accountRepository.findAllByOrderByIdAsc();
    }

    /**
     * Replaces the display-disabled-accounts paragraph.
     * The COBOL cursor ACCOUNT-DISABLED-CUR filters WHERE IS_ENABLED = 'N'.
     */
    public List<Account> getDisabledAccounts() {
        return accountRepository.findByIsEnabledOrderByIdAsc("N");
    }

    /**
     * Replaces the query-accounts paragraph (lines 318-394).
     *
     * The COBOL code trims the search value and wraps it with '%' wildcards
     * for a LIKE query across FIRST_NAME, LAST_NAME, PHONE, and ADDRESS.
     * Here we trim the input; the repository query handles wildcard wrapping.
     */
    public List<Account> searchAccounts(String searchValue) {
        if (searchValue == null) {
            return List.of();
        }
        String trimmed = searchValue.trim();
        if (trimmed.isEmpty()) {
            return List.of();
        }
        return accountRepository.searchAccounts(trimmed);
    }
}
