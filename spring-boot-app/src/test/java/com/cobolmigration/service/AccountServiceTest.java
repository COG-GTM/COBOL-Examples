package com.cobolmigration.service;

import com.cobolmigration.model.Account;
import com.cobolmigration.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AccountService} with mocked repository.
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account enabledAccount;
    private Account disabledAccount;

    @BeforeEach
    void setUp() {
        enabledAccount = new Account("John", "Tester", "1555550100",
                "123 Fake St", true);
        enabledAccount.setId(1);

        disabledAccount = new Account("Bob", "Tester4", "1555550154",
                "119 Truck St", false);
        disabledAccount.setId(2);
    }

    @Test
    void getAllAccounts_delegatesToRepository() {
        when(accountRepository.findAllByOrderByIdAsc())
                .thenReturn(List.of(enabledAccount, disabledAccount));

        List<Account> result = accountService.getAllAccounts();

        assertThat(result).hasSize(2);
        verify(accountRepository).findAllByOrderByIdAsc();
    }

    @Test
    void getDisabledAccounts_delegatesToRepository() {
        when(accountRepository.findByEnabledFalseOrderByIdAsc())
                .thenReturn(List.of(disabledAccount));

        List<Account> result = accountService.getDisabledAccounts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isEnabled()).isFalse();
        verify(accountRepository).findByEnabledFalseOrderByIdAsc();
    }

    @Test
    void searchAccounts_wrapsQueryWithWildcards() {
        when(accountRepository.searchAccounts("%John%"))
                .thenReturn(List.of(enabledAccount));

        List<Account> result = accountService.searchAccounts("John");

        assertThat(result).hasSize(1);
        verify(accountRepository).searchAccounts("%John%");
    }

    @Test
    void searchAccounts_emptyQuery_wrapsWithWildcards() {
        when(accountRepository.searchAccounts("%%"))
                .thenReturn(List.of(enabledAccount, disabledAccount));

        List<Account> result = accountService.searchAccounts("");

        assertThat(result).hasSize(2);
        verify(accountRepository).searchAccounts("%%");
    }
}
