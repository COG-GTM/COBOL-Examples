package com.example.cobolmigration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.cobolmigration.model.Account;
import com.example.cobolmigration.repository.AccountRepository;
import com.example.cobolmigration.service.AccountService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void searchAccounts_wrapsTrimmedTermWithWildcards() {
        when(accountRepository.search(eq("%john%"))).thenReturn(List.of(new Account()));
        List<Account> result = accountService.searchAccounts("  john  ");
        assertThat(result).hasSize(1);
        verify(accountRepository).search("%john%");
    }
}
