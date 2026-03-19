package com.cobolmigration.service;

import com.cobolmigration.model.Account;
import com.cobolmigration.repository.AccountRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service encapsulating the business logic from sql_example.cbl's procedure division.
 *
 * <p>Operations mapped from COBOL paragraphs:
 * <ul>
 *   <li>display-all-accounts &rarr; {@link #getAllAccounts()}</li>
 *   <li>display-disabled-accounts &rarr; {@link #getDisabledAccounts()}</li>
 *   <li>query-accounts &rarr; {@link #searchAccounts(String)}</li>
 *   <li>(implicit INSERT) &rarr; {@link #addAccount(String, String, String, String)}</li>
 *   <li>(implicit UPDATE) &rarr; {@link #updateAccount(Long, String, String, String, String)}</li>
 *   <li>(implicit DELETE) &rarr; {@link #deleteAccount(Long)}</li>
 *   <li>(toggle IS_ENABLED) &rarr; {@link #toggleAccountEnabled(Long)}</li>
 * </ul>
 *
 * @see sql/sql_example.cbl
 */
@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Display all accounts ordered by ID.
     * Replaces the display-all-accounts paragraph using ACCOUNT-ALL-CUR.
     */
    @Transactional(readOnly = true)
    public List<Account> getAllAccounts() {
        return accountRepository.findAllByOrderByIdAsc();
    }

    /**
     * Display disabled accounts (IS_ENABLED = 'N') ordered by ID.
     * Replaces the display-disabled-accounts paragraph using ACCOUNT-DISABLED-CUR.
     */
    @Transactional(readOnly = true)
    public List<Account> getDisabledAccounts() {
        return accountRepository.findByIsEnabledOrderByIdAsc('N');
    }

    /**
     * Query accounts by search term across firstName, lastName, phone, and address.
     * Replaces the query-accounts paragraph using ACCOUNT-QUERY-CUR.
     * The COBOL version wraps the search term with '%' wildcards (lines 333-337).
     *
     * @param searchTerm the raw search term (wildcards are added automatically)
     */
    @Transactional(readOnly = true)
    public List<Account> searchAccounts(String searchTerm) {
        String wildcardTerm = "%" + searchTerm.trim() + "%";
        return accountRepository.searchAccounts(wildcardTerm);
    }

    /**
     * Add a new account. New accounts default to enabled ('Y').
     */
    public Account addAccount(String firstName, String lastName,
                              String phone, String address) {
        Account account = new Account(
                firstName.trim(), lastName.trim(),
                phone.trim(), address.trim(), 'Y');
        return accountRepository.save(account);
    }

    /**
     * Update an existing account's fields and set mod_dt.
     */
    public Optional<Account> updateAccount(Long id, String firstName, String lastName,
                                           String phone, String address) {
        return accountRepository.findById(id).map(account -> {
            account.setFirstName(firstName.trim());
            account.setLastName(lastName.trim());
            account.setPhone(phone.trim());
            account.setAddress(address.trim());
            account.setModDt(LocalDateTime.now());
            return accountRepository.save(account);
        });
    }

    /**
     * Delete an account by ID.
     *
     * @return true if the account existed and was deleted
     */
    public boolean deleteAccount(Long id) {
        if (accountRepository.existsById(id)) {
            accountRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Toggle the IS_ENABLED field between 'Y' and 'N'.
     * Maps the COBOL 88-level condition values ws-account-enabled/ws-account-disabled.
     */
    public Optional<Account> toggleAccountEnabled(Long id) {
        return accountRepository.findById(id).map(account -> {
            account.setIsEnabled(account.getIsEnabled() == 'Y' ? 'N' : 'Y');
            account.setModDt(LocalDateTime.now());
            return accountRepository.save(account);
        });
    }

    /**
     * Find a single account by ID.
     */
    @Transactional(readOnly = true)
    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }
}
