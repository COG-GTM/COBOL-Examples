package com.cobolmigration.repository;

import com.cobolmigration.entity.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for AccountRepository.
 * Uses H2 in-memory database with Flyway migrations applied.
 * Tests replace verification of COBOL cursor operations from sql_example.cbl.
 */
@DataJpaTest
@ActiveProfiles("test")
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void findAllByOrderByIdAsc_shouldReturnAllSeedAccounts() {
        List<Account> accounts = accountRepository.findAllByOrderByIdAsc();
        assertEquals(11, accounts.size());
        // Verify ordering
        assertTrue(accounts.get(0).getId() < accounts.get(1).getId());
    }

    @Test
    void findByIsEnabledOrderByIdAsc_shouldReturnDisabledAccounts() {
        List<Account> disabled = accountRepository.findByIsEnabledOrderByIdAsc("N");
        assertFalse(disabled.isEmpty());
        disabled.forEach(a -> assertEquals("N", a.getIsEnabled()));
    }

    @Test
    void findByIsEnabledOrderByIdAsc_shouldReturnEnabledAccounts() {
        List<Account> enabled = accountRepository.findByIsEnabledOrderByIdAsc("Y");
        assertFalse(enabled.isEmpty());
        enabled.forEach(a -> assertEquals("Y", a.getIsEnabled()));
    }

    @Test
    void searchAccounts_shouldFindByFirstName() {
        List<Account> results = accountRepository.searchAccounts("John");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(a -> "John".equals(a.getFirstName())));
    }

    @Test
    void searchAccounts_shouldFindByLastName() {
        List<Account> results = accountRepository.searchAccounts("Tester");
        assertFalse(results.isEmpty());
    }

    @Test
    void searchAccounts_shouldFindByAddress() {
        List<Account> results = accountRepository.searchAccounts("Fake St");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(a -> a.getAddress().contains("Fake St")));
    }

    @Test
    void searchAccounts_shouldBeCaseInsensitive() {
        List<Account> results = accountRepository.searchAccounts("john");
        assertFalse(results.isEmpty());
    }

    @Test
    void searchAccounts_shouldReturnEmptyForNoMatch() {
        List<Account> results = accountRepository.searchAccounts("ZZZZNOTEXIST");
        assertTrue(results.isEmpty());
    }
}
