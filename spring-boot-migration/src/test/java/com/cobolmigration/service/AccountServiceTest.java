package com.cobolmigration.service;

import com.cobolmigration.entity.Account;
import com.cobolmigration.exception.DatabaseException;
import com.cobolmigration.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AccountService.
 * Tests all account queries with mock repository.
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private List<Account> allAccounts;
    private List<Account> disabledAccounts;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        allAccounts = Arrays.asList(
                Account.builder().id(1L).firstName("John").lastName("Tester")
                        .phone("15555550100").address("123 Fake St, Nowhere")
                        .isEnabled("Y").createDt(now).modDt(now).build(),
                Account.builder().id(2L).firstName("Mike").lastName("Tester1")
                        .phone("15555550121").address("122 Real St, Nowhere")
                        .isEnabled("Y").createDt(now).modDt(now).build(),
                Account.builder().id(5L).firstName("Bob").lastName("Tester4")
                        .phone("15555550154").address("119 Truck St, Nowhere")
                        .isEnabled("N").createDt(now).modDt(now).build()
        );

        disabledAccounts = Collections.singletonList(
                Account.builder().id(5L).firstName("Bob").lastName("Tester4")
                        .phone("15555550154").address("119 Truck St, Nowhere")
                        .isEnabled("N").createDt(now).modDt(now).build()
        );
    }

    @Test
    void getAllAccounts_returnsAllAccounts() {
        when(accountRepository.findAllByOrderByIdAsc()).thenReturn(allAccounts);

        List<Account> result = accountService.getAllAccounts();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Mike", result.get(1).getFirstName());
        assertEquals("Bob", result.get(2).getFirstName());
    }

    @Test
    void getAllAccounts_returnsEmptyList() {
        when(accountRepository.findAllByOrderByIdAsc()).thenReturn(Collections.emptyList());

        List<Account> result = accountService.getAllAccounts();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getDisabledAccounts_returnsOnlyDisabled() {
        when(accountRepository.findByIsEnabled("N")).thenReturn(disabledAccounts);

        List<Account> result = accountService.getDisabledAccounts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Bob", result.get(0).getFirstName());
        assertEquals("N", result.get(0).getIsEnabled());
    }

    @Test
    void searchAccounts_returnMatchingAccounts() {
        List<Account> searchResults = Collections.singletonList(allAccounts.get(0));
        when(accountRepository.searchAccounts("John")).thenReturn(searchResults);

        List<Account> result = accountService.searchAccounts("John");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
    }

    @Test
    void searchAccounts_returnsEmptyForNoMatch() {
        when(accountRepository.searchAccounts("NonExistent")).thenReturn(Collections.emptyList());

        List<Account> result = accountService.searchAccounts("NonExistent");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllAccounts_throwsDatabaseExceptionOnError() {
        when(accountRepository.findAllByOrderByIdAsc())
                .thenThrow(new RuntimeException("DB connection failed"));

        assertThrows(DatabaseException.class, () -> accountService.getAllAccounts());
    }

    @Test
    void getDisabledAccounts_throwsDatabaseExceptionOnError() {
        when(accountRepository.findByIsEnabled("N"))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(DatabaseException.class, () -> accountService.getDisabledAccounts());
    }

    @Test
    void searchAccounts_throwsDatabaseExceptionOnError() {
        when(accountRepository.searchAccounts("test"))
                .thenThrow(new RuntimeException("Query failed"));

        assertThrows(DatabaseException.class, () -> accountService.searchAccounts("test"));
    }
}
