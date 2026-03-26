package com.cobolmigration.service;

import com.cobolmigration.entity.Account;
import com.cobolmigration.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer for Account operations.
 * Replaces the COBOL paragraphs:
 *   - display-all-accounts (getAllAccounts)
 *   - display-disabled-accounts (getDisabledAccounts)
 *   - query-accounts (searchAccounts)
 *   - add-account, update-account, delete-account (CRUD operations)
 * Also replaces check-sql-state error handling with Java exceptions.
 */
@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAllByOrderByIdAsc();
    }

    public List<Account> getDisabledAccounts() {
        return accountRepository.findByIsEnabledOrderByIdAsc("N");
    }

    public List<Account> searchAccounts(String searchValue) {
        if (searchValue == null || searchValue.isBlank()) {
            return getAllAccounts();
        }
        return accountRepository.searchAccounts(searchValue);
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    public Account createAccount(Account account) {
        account.setId(null);
        account.setCreateDt(LocalDateTime.now());
        account.setModDt(LocalDateTime.now());
        if (account.getIsEnabled() == null) {
            account.setIsEnabled("N");
        }
        return accountRepository.save(account);
    }

    public Account updateAccount(Long id, Account accountDetails) {
        Account existing = getAccountById(id);
        existing.setFirstName(accountDetails.getFirstName());
        existing.setLastName(accountDetails.getLastName());
        existing.setPhone(accountDetails.getPhone());
        existing.setAddress(accountDetails.getAddress());
        existing.setIsEnabled(accountDetails.getIsEnabled());
        existing.setModDt(LocalDateTime.now());
        return accountRepository.save(existing);
    }

    public void deleteAccount(Long id) {
        Account existing = getAccountById(id);
        accountRepository.delete(existing);
    }

    public static class AccountNotFoundException extends RuntimeException {
        public AccountNotFoundException(Long id) {
            super("Account not found with id: " + id);
        }
    }
}
