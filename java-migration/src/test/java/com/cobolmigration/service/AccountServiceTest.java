package com.cobolmigration.service;

import com.cobolmigration.model.Account;
import com.cobolmigration.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for AccountService - validates migration of
 * sql/sql_example.cbl database operations using H2 in-memory database.
 */
@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void getAllAccounts_returnsAllRecords() {
        List<Account> accounts = accountService.getAllAccounts();
        assertFalse(accounts.isEmpty());
        assertEquals(11, accounts.size());
    }

    @Test
    void getAllAccounts_orderedByIdAsc() {
        List<Account> accounts = accountService.getAllAccounts();
        for (int i = 1; i < accounts.size(); i++) {
            assertTrue(accounts.get(i).getId() >= accounts.get(i - 1).getId());
        }
    }

    @Test
    void getDisabledAccounts_returnsOnlyDisabled() {
        List<Account> accounts = accountService.getDisabledAccounts();
        assertFalse(accounts.isEmpty());
        for (Account account : accounts) {
            assertEquals("N", account.getIsEnabled());
        }
    }

    @Test
    void getDisabledAccounts_returns3Records() {
        // Bob (Tester4), Paula (Tester5), Bill (Tester8) are disabled
        List<Account> accounts = accountService.getDisabledAccounts();
        assertEquals(3, accounts.size());
    }

    @Test
    void searchAccounts_findsByFirstName() {
        List<Account> accounts = accountService.searchAccounts("John");
        assertFalse(accounts.isEmpty());
        assertTrue(accounts.stream().anyMatch(a -> a.getFirstName().contains("John")));
    }

    @Test
    void searchAccounts_findsByLastName() {
        List<Account> accounts = accountService.searchAccounts("Tester1");
        assertFalse(accounts.isEmpty());
    }

    @Test
    void searchAccounts_findsByAddress() {
        List<Account> accounts = accountService.searchAccounts("Fake");
        assertFalse(accounts.isEmpty());
        assertTrue(accounts.stream().anyMatch(a -> a.getAddress().contains("Fake")));
    }

    @Test
    void searchAccounts_emptySearchReturnsAll() {
        List<Account> accounts = accountService.searchAccounts("");
        assertFalse(accounts.isEmpty());
    }

    @Test
    void searchAccounts_noMatchReturnsEmpty() {
        List<Account> accounts = accountService.searchAccounts("ZZZZNONEXISTENT");
        assertTrue(accounts.isEmpty());
    }
}
