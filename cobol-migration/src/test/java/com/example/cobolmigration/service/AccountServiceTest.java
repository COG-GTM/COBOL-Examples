package com.example.cobolmigration.service;

import com.example.cobolmigration.model.Account;
import com.example.cobolmigration.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = new Account("John", "Tester", "15555550100", "123 Fake St, Nowhere", "Y");
    }

    @Test
    void getAllAccounts_shouldCallRepositoryAndReturnResults() {
        when(accountRepository.findAllByOrderByIdAsc()).thenReturn(List.of(testAccount));
        List<Account> result = accountService.getAllAccounts();
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        verify(accountRepository).findAllByOrderByIdAsc();
    }

    @Test
    void getDisabledAccounts_shouldCallRepositoryWithN() {
        Account disabled = new Account("Bob", "Tester4", "15555550154", "119 Truck St, Nowhere", "N");
        when(accountRepository.findByIsEnabledOrderByIdAsc("N")).thenReturn(List.of(disabled));
        List<Account> result = accountService.getDisabledAccounts();
        assertEquals(1, result.size());
        assertEquals("N", result.get(0).getIsEnabled());
        verify(accountRepository).findByIsEnabledOrderByIdAsc("N");
    }

    @Test
    void searchAccounts_shouldTrimAndWrapWithWildcards() {
        // Matching COBOL behavior: trim the input and wrap with '%' wildcards
        // (sql/sql_example.cbl lines 333-343)
        when(accountRepository.searchAccounts("%John%")).thenReturn(List.of(testAccount));
        List<Account> result = accountService.searchAccounts("  John  ");
        assertEquals(1, result.size());
        verify(accountRepository).searchAccounts("%John%");
    }

    @Test
    void searchAccounts_shouldHandleNullInput() {
        when(accountRepository.searchAccounts("%%")).thenReturn(List.of());
        List<Account> result = accountService.searchAccounts(null);
        assertEquals(0, result.size());
        verify(accountRepository).searchAccounts("%%");
    }

    @Test
    void searchAccounts_shouldHandleEmptyInput() {
        when(accountRepository.searchAccounts("%%")).thenReturn(List.of());
        List<Account> result = accountService.searchAccounts("");
        assertEquals(0, result.size());
        verify(accountRepository).searchAccounts("%%");
    }
}
