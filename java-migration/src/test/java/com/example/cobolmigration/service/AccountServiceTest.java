package com.example.cobolmigration.service;

import com.example.cobolmigration.entity.Account;
import com.example.cobolmigration.repository.AccountRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AccountService}.
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void getAllAccounts_delegatesToRepository() {
        Account account = new Account("John", "Tester", "1555550100",
                "123 Fake St", "Y");
        when(accountRepository.findAllByOrderByIdAsc())
                .thenReturn(List.of(account));

        List<Account> result = accountService.getAllAccounts();

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        verify(accountRepository).findAllByOrderByIdAsc();
    }

    @Test
    void getDisabledAccounts_passesCorrectFilter() {
        Account disabled = new Account("Bob", "Tester4", "1555550154",
                "119 Truck St", "N");
        when(accountRepository.findByIsEnabledOrderByIdAsc("N"))
                .thenReturn(List.of(disabled));

        List<Account> result = accountService.getDisabledAccounts();

        assertEquals(1, result.size());
        assertEquals("N", result.get(0).getIsEnabled());
        verify(accountRepository).findByIsEnabledOrderByIdAsc("N");
    }

    @Test
    void searchAccounts_trimsInput() {
        when(accountRepository.searchAccounts("John"))
                .thenReturn(List.of(new Account("John", "Tester",
                        "1555550100", "123 Fake St", "Y")));

        List<Account> result = accountService.searchAccounts("  John  ");

        assertEquals(1, result.size());
        verify(accountRepository).searchAccounts("John");
    }

    @Test
    void searchAccounts_returnsEmptyForNull() {
        List<Account> result = accountService.searchAccounts(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchAccounts_returnsEmptyForBlank() {
        List<Account> result = accountService.searchAccounts("   ");
        assertTrue(result.isEmpty());
    }
}
