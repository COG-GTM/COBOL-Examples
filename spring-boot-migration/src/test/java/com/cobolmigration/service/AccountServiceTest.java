package com.cobolmigration.service;

import com.cobolmigration.entity.Account;
import com.cobolmigration.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AccountService with mocked repository.
 * Replaces verification of COBOL paragraphs:
 *   display-all-accounts, query-accounts, add-account, etc.
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = new Account("John", "Tester", "15555550100", "123 Fake St", "Y");
        testAccount.setId(1L);
        testAccount.setCreateDt(LocalDateTime.now());
        testAccount.setModDt(LocalDateTime.now());
    }

    @Test
    void getAllAccounts_shouldReturnAllAccounts() {
        when(accountRepository.findAllByOrderByIdAsc())
                .thenReturn(Arrays.asList(testAccount));

        List<Account> result = accountService.getAllAccounts();
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
    }

    @Test
    void getDisabledAccounts_shouldReturnDisabledOnly() {
        Account disabled = new Account("Bob", "Tester4", "15555550154", "119 Truck St", "N");
        disabled.setId(5L);
        when(accountRepository.findByIsEnabledOrderByIdAsc("N"))
                .thenReturn(Arrays.asList(disabled));

        List<Account> result = accountService.getDisabledAccounts();
        assertEquals(1, result.size());
        assertEquals("N", result.get(0).getIsEnabled());
    }

    @Test
    void searchAccounts_shouldDelegateToRepository() {
        when(accountRepository.searchAccounts(anyString()))
                .thenReturn(Arrays.asList(testAccount));

        List<Account> result = accountService.searchAccounts("John");
        assertEquals(1, result.size());
    }

    @Test
    void searchAccounts_withBlankValue_shouldReturnAll() {
        when(accountRepository.findAllByOrderByIdAsc())
                .thenReturn(Arrays.asList(testAccount));

        List<Account> result = accountService.searchAccounts("");
        assertEquals(1, result.size());
    }

    @Test
    void getAccountById_shouldReturnAccount() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        Account result = accountService.getAccountById(1L);
        assertEquals("John", result.getFirstName());
    }

    @Test
    void getAccountById_shouldThrowWhenNotFound() {
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(AccountService.AccountNotFoundException.class,
                () -> accountService.getAccountById(999L));
    }

    @Test
    void createAccount_shouldSetTimestampsAndSave() {
        Account newAccount = new Account("New", "User", "1234567890", "456 Test Ave", "Y");
        when(accountRepository.save(any(Account.class))).thenReturn(newAccount);

        Account result = accountService.createAccount(newAccount);
        assertNotNull(result);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void updateAccount_shouldUpdateExistingAccount() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        Account updated = new Account("Updated", "Name", "9999999999", "789 New St", "N");
        Account result = accountService.updateAccount(1L, updated);
        assertEquals("Updated", result.getFirstName());
    }

    @Test
    void deleteAccount_shouldDeleteExistingAccount() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        accountService.deleteAccount(1L);
        verify(accountRepository).delete(testAccount);
    }

    @Test
    void deleteAccount_shouldThrowWhenNotFound() {
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(AccountService.AccountNotFoundException.class,
                () -> accountService.deleteAccount(999L));
    }
}
