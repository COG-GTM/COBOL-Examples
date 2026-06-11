package com.example.cobolmigration.service;

import com.example.cobolmigration.model.Account;
import com.example.cobolmigration.repository.AccountRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Replaces the COBOL {@code PERFORM} paragraphs {@code display-all-accounts},
 * {@code display-disabled-accounts} and {@code query-accounts} from
 * {@code sql/sql_example.cbl}. Following the modular sub-program pattern in
 * {@code sub_program/main_app.cbl} (where the main app issued
 * {@code CALL "sub-app" USING ...}), business logic lives in this injected
 * {@code @Service} bean rather than inline paragraphs.
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /** Mirrors {@code display-all-accounts}. */
    public List<Account> getAllAccounts() {
        return accountRepository.findAllByOrderByIdAsc();
    }

    /** Mirrors {@code display-disabled-accounts}. */
    public List<Account> getDisabledAccounts() {
        return accountRepository.findByIsEnabledFalseOrderByIdAsc();
    }

    /**
     * Mirrors {@code query-accounts}. The COBOL paragraph wrapped the trimmed
     * user input with '%' wildcards before binding it to the LIKE cursor; the
     * same wrapping happens here.
     */
    public List<Account> searchAccounts(String searchTerm) {
        String term = searchTerm == null ? "" : searchTerm.trim();
        return accountRepository.search("%" + term + "%");
    }
}
