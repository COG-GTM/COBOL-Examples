package com.cobolmigration.service;

import com.cobolmigration.model.Account;
import com.cobolmigration.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for AccountService using H2 in-memory database.
 * Verifies behavior matches the original COBOL sql_example.cbl program.
 */
@DataJpaTest
@Import(AccountService.class)
@ActiveProfiles("test")
class AccountServiceTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();

        LocalDateTime now = LocalDateTime.now();

        // Populate with test data matching sql/create_test_db.sql
        accountRepository.save(new Account("John", "Tester", "15555550100", "123 Fake St, Nowhere", "Y", now, now));
        accountRepository.save(new Account("Mike", "Tester1", "15555550121", "122 Real St, Nowhere", "Y", now, now));
        accountRepository.save(new Account("Mary", "Tester2", "15555550132", "121 ABC St, Nowhere", "Y", now, now));
        accountRepository.save(new Account("Jack", "Tester3", "15555550143", "120 Rock St, Nowhere", "Y", now, now));
        accountRepository.save(new Account("Bob", "Tester4", "15555550154", "119 Truck St, Nowhere", "N", now, now));
        accountRepository.save(new Account("Paula", "Tester5", "1555550165", "118 Car St, Nowhere", "N", now, now));
        accountRepository.save(new Account("James", "Tester6", "1555550176", "117 Land St, Nowhere", "Y", now, now));
        accountRepository.save(new Account("Jane", "Tester7", "1555550187", "116 Sea St, Nowhere", "Y", now, now));
        accountRepository.save(new Account("Bill", "Tester8", "1555550198", "115 Dock St, Nowhere", "N", now, now));
        accountRepository.save(new Account("Lucy", "Tester9", "1555550209", "114 Beach St, Nowhere", "Y", now, now));
        accountRepository.save(new Account("Richard", "Tester10", "1555550210", "113 Water St, Nowhere", "Y", now, now));
    }

    @Test
    void listAllAccounts_shouldReturnAllOrderedById() {
        // Replaces ACCOUNT-ALL-CUR cursor
        List<Account> accounts = accountService.listAllAccounts();
        assertEquals(11, accounts.size());
        // Verify ordering by ID ascending
        for (int i = 1; i < accounts.size(); i++) {
            assertTrue(accounts.get(i).getId() > accounts.get(i - 1).getId());
        }
    }

    @Test
    void listDisabledAccounts_shouldReturnOnlyDisabled() {
        // Replaces ACCOUNT-DISABLED-CUR cursor (WHERE IS_ENABLED = 'N')
        List<Account> disabled = accountService.listDisabledAccounts();
        assertEquals(3, disabled.size());
        for (Account account : disabled) {
            assertEquals("N", account.getIsEnabled());
        }
        // Verify ordering by ID ascending
        for (int i = 1; i < disabled.size(); i++) {
            assertTrue(disabled.get(i).getId() > disabled.get(i - 1).getId());
        }
    }

    @Test
    void searchAccounts_shouldFindByFirstName() {
        // Replaces ACCOUNT-QUERY-CUR (LIKE search on FIRST_NAME)
        List<Account> results = accountService.searchAccounts("John");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(a -> a.getFirstName().equals("John")));
    }

    @Test
    void searchAccounts_shouldFindByLastName() {
        // LIKE search on LAST_NAME
        List<Account> results = accountService.searchAccounts("Tester5");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(a -> a.getLastName().equals("Tester5")));
    }

    @Test
    void searchAccounts_shouldFindByAddress() {
        // LIKE search on ADDRESS
        List<Account> results = accountService.searchAccounts("Fake");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(a -> a.getAddress().contains("Fake")));
    }

    @Test
    void searchAccounts_shouldFindByPhone() {
        // LIKE search on PHONE
        List<Account> results = accountService.searchAccounts("15555550100");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(a -> a.getPhone().equals("15555550100")));
    }

    @Test
    void searchAccounts_shouldReturnEmptyForNoMatch() {
        List<Account> results = accountService.searchAccounts("ZZZZZZZ");
        assertTrue(results.isEmpty());
    }

    @Test
    void searchAccounts_shouldHandlePartialMatch() {
        // Partial match like the COBOL '%search_term%' behavior
        List<Account> results = accountService.searchAccounts("Test");
        // Should match multiple records (Tester, Tester1, etc. in last names)
        assertTrue(results.size() > 1);
    }
}
