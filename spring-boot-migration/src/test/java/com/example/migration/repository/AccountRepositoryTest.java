package com.example.migration.repository;

import com.example.migration.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for AccountRepository.
 * Verifies all three query patterns that replace COBOL SQL cursors:
 *   ACCOUNT-ALL-CUR, ACCOUNT-DISABLED-CUR, ACCOUNT-QUERY-CUR
 */
@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        entityManager.persist(new Account("John", "Tester", "15555550100",
                "123 Fake St, Nowhere", "Y", now, now));
        entityManager.persist(new Account("Bob", "Tester4", "15555550154",
                "119 Truck St, Nowhere", "N", now, now));
        entityManager.persist(new Account("Paula", "Tester5", "1555550165",
                "118 Car St, Nowhere", "N", now, now));
        entityManager.persist(new Account("Mary", "Tester2", "15555550132",
                "121 ABC St, Nowhere", "Y", now, now));
        entityManager.flush();
    }

    @Test
    void findAllByOrderByIdAsc_returnsAllAccountsOrderedById() {
        List<Account> accounts = accountRepository.findAllByOrderByIdAsc();

        assertEquals(4, accounts.size());
        assertTrue(accounts.get(0).getId() < accounts.get(1).getId());
        assertTrue(accounts.get(1).getId() < accounts.get(2).getId());
        assertTrue(accounts.get(2).getId() < accounts.get(3).getId());
    }

    @Test
    void findByIsEnabledOrderByIdAsc_returnsOnlyDisabledAccounts() {
        List<Account> disabledAccounts = accountRepository.findByIsEnabledOrderByIdAsc("N");

        assertEquals(2, disabledAccounts.size());
        for (Account account : disabledAccounts) {
            assertEquals("N", account.getIsEnabled());
        }
        assertEquals("Bob", disabledAccounts.get(0).getFirstName());
        assertEquals("Paula", disabledAccounts.get(1).getFirstName());
    }

    @Test
    void findByIsEnabledOrderByIdAsc_returnsOnlyEnabledAccounts() {
        List<Account> enabledAccounts = accountRepository.findByIsEnabledOrderByIdAsc("Y");

        assertEquals(2, enabledAccounts.size());
        for (Account account : enabledAccounts) {
            assertEquals("Y", account.getIsEnabled());
        }
    }

    @Test
    void searchAccounts_findsMatchByFirstName() {
        List<Account> results = accountRepository.searchAccounts("%John%");

        assertEquals(1, results.size());
        assertEquals("John", results.get(0).getFirstName());
    }

    @Test
    void searchAccounts_findsMatchByLastName() {
        List<Account> results = accountRepository.searchAccounts("%Tester4%");

        assertEquals(1, results.size());
        assertEquals("Bob", results.get(0).getFirstName());
    }

    @Test
    void searchAccounts_findsMatchByAddress() {
        List<Account> results = accountRepository.searchAccounts("%Fake%");

        assertEquals(1, results.size());
        assertEquals("123 Fake St, Nowhere", results.get(0).getAddress());
    }

    @Test
    void searchAccounts_findsMultipleMatches() {
        List<Account> results = accountRepository.searchAccounts("%Tester%");

        assertEquals(4, results.size());
    }

    @Test
    void searchAccounts_returnsEmptyForNoMatch() {
        List<Account> results = accountRepository.searchAccounts("%NONEXISTENT%");

        assertTrue(results.isEmpty());
    }
}
