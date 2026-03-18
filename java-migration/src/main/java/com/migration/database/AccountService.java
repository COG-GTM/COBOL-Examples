package com.migration.database;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Phase 5: Database Layer - Account Service
 *
 * Replicates the COBOL SQL operations from sql/sql_example.cbl:
 *
 * - Connect: Handled by Spring auto-configuration (replaces EXEC SQL CONNECT)
 * - Create table: Handled by Flyway migration (replaces manual DDL)
 * - Insert records: createAccount()
 * - Query all via cursor: getAllAccounts() (replaces ACCOUNT-ALL-CUR fetch loop)
 * - Query disabled: getDisabledAccounts() (replaces ACCOUNT-DISABLED-CUR)
 * - Query by search: searchAccounts() (replaces ACCOUNT-QUERY-CUR)
 * - Update: updateAccount()
 * - Delete: deleteAccount()
 *
 * COBOL error handling (check-sql-state paragraph) is replaced by
 * Spring's exception translation (@Repository + DataAccessException).
 */
@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Retrieves all accounts ordered by ID.
     * Replaces COBOL display-all-accounts paragraph with ACCOUNT-ALL-CUR cursor.
     */
    @Transactional(readOnly = true)
    public List<Account> getAllAccounts() {
        return accountRepository.findAllByOrderByIdAsc();
    }

    /**
     * Retrieves all disabled accounts.
     * Replaces COBOL display-disabled-accounts paragraph with ACCOUNT-DISABLED-CUR cursor.
     */
    @Transactional(readOnly = true)
    public List<Account> getDisabledAccounts() {
        return accountRepository.findByIsEnabledOrderByIdAsc("N");
    }

    /**
     * Searches accounts by a search term across multiple fields.
     * Replaces COBOL query-accounts paragraph with ACCOUNT-QUERY-CUR cursor.
     *
     * In COBOL, the search required manual LIKE wildcard handling:
     *   STRING '%' FUNCTION TRIM(ws-search-string) '%' INTO ws-search-value-text
     *
     * In Java, we add the wildcards automatically.
     */
    @Transactional(readOnly = true)
    public List<Account> searchAccounts(String searchTerm) {
        String wildcardSearch = "%" + searchTerm.strip() + "%";
        return accountRepository.searchAccounts(wildcardSearch);
    }

    /**
     * Retrieves a single account by ID.
     */
    @Transactional(readOnly = true)
    public Optional<Account> getAccountById(int id) {
        return accountRepository.findById(id);
    }

    /**
     * Creates a new account.
     * Replaces COBOL INSERT INTO ACCOUNTS operation.
     */
    public Account createAccount(String firstName, String lastName, String phone,
                                 String address, String isEnabled) {
        Account account = new Account(firstName, lastName, phone, address, isEnabled);
        account.setCreateDt(LocalDateTime.now());
        account.setModDt(LocalDateTime.now());
        return accountRepository.save(account);
    }

    /**
     * Updates an existing account.
     */
    public Optional<Account> updateAccount(int id, String firstName, String lastName,
                                           String phone, String address, String isEnabled) {
        return accountRepository.findById(id).map(account -> {
            if (firstName != null) account.setFirstName(firstName);
            if (lastName != null) account.setLastName(lastName);
            if (phone != null) account.setPhone(phone);
            if (address != null) account.setAddress(address);
            if (isEnabled != null) account.setIsEnabled(isEnabled);
            account.setModDt(LocalDateTime.now());
            return accountRepository.save(account);
        });
    }

    /**
     * Deletes an account by ID.
     */
    public boolean deleteAccount(int id) {
        if (accountRepository.existsById(id)) {
            accountRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
