package com.migration.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Phase 5: Database Layer Tests
 *
 * Integration tests using @DataJpaTest with H2 in-memory database.
 * Test cases mirror the COBOL sql/sql_example.cbl operations.
 */
@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();

        // Insert test data matching COBOL create_test_db.sql
        insertAccount("John", "Tester", "15555550100", "123 Fake St, Nowhere", "Y");
        insertAccount("Mike", "Tester1", "15555550121", "122 Real St, Nowhere", "Y");
        insertAccount("Mary", "Tester2", "15555550132", "121 ABC St, Nowhere", "Y");
        insertAccount("Jack", "Tester3", "15555550143", "120 Rock St, Nowhere", "Y");
        insertAccount("Bob", "Tester4", "15555550154", "119 Truck St, Nowhere", "N");
        insertAccount("Paula", "Tester5", "1555550165", "118 Car St, Nowhere", "N");
        insertAccount("James", "Tester6", "1555550176", "117 Land St, Nowhere", "Y");
        insertAccount("Jane", "Tester7", "1555550187", "116 Sea St, Nowhere", "Y");
        insertAccount("Bill", "Tester8", "1555550198", "115 Dock St, Nowhere", "N");
        insertAccount("Lucy", "Tester9", "1555550209", "114 Beach St, Nowhere", "Y");
        insertAccount("Richard", "Tester10", "1555550210", "113 Water St, Nowhere", "Y");
    }

    private void insertAccount(String first, String last, String phone, String address, String enabled) {
        Account account = new Account(first, last, phone, address, enabled);
        account.setCreateDt(LocalDateTime.now());
        account.setModDt(LocalDateTime.now());
        accountRepository.save(account);
    }

    @Test
    @DisplayName("Find all accounts ordered by ID (COBOL: ACCOUNT-ALL-CUR)")
    void findAllOrderByIdAsc() {
        List<Account> accounts = accountRepository.findAllByOrderByIdAsc();

        assertEquals(11, accounts.size());
        // Verify ascending order
        for (int i = 1; i < accounts.size(); i++) {
            assertTrue(accounts.get(i).getId() > accounts.get(i - 1).getId());
        }
    }

    @Test
    @DisplayName("Find disabled accounts (COBOL: ACCOUNT-DISABLED-CUR)")
    void findDisabledAccounts() {
        List<Account> disabled = accountRepository.findByIsEnabledOrderByIdAsc("N");

        assertEquals(3, disabled.size());
        disabled.forEach(a -> assertEquals("N", a.getIsEnabled()));
    }

    @Test
    @DisplayName("Search accounts by term (COBOL: ACCOUNT-QUERY-CUR)")
    void searchAccounts() {
        // COBOL: STRING '%' FUNCTION TRIM(search) '%' INTO ws-search-value-text
        List<Account> results = accountRepository.searchAccounts("%Tester%");

        // All 11 accounts have "Tester" in last name
        assertEquals(11, results.size());
    }

    @Test
    @DisplayName("Search accounts by first name")
    void searchByFirstName() {
        List<Account> results = accountRepository.searchAccounts("%John%");
        assertFalse(results.isEmpty());
        assertEquals("John", results.get(0).getFirstName());
    }

    @Test
    @DisplayName("Search accounts by address")
    void searchByAddress() {
        List<Account> results = accountRepository.searchAccounts("%Fake St%");
        assertEquals(1, results.size());
        assertTrue(results.get(0).getAddress().contains("Fake St"));
    }

    @Test
    @DisplayName("Find account by ID")
    void findById() {
        Account first = accountRepository.findAllByOrderByIdAsc().get(0);
        Optional<Account> found = accountRepository.findById(first.getId());

        assertTrue(found.isPresent());
        assertEquals("John", found.get().getFirstName());
    }

    @Test
    @DisplayName("Create new account (COBOL: INSERT INTO ACCOUNTS)")
    void createAccount() {
        Account newAccount = new Account("New", "User", "1234567890", "999 Test St", "Y");
        newAccount.setCreateDt(LocalDateTime.now());
        newAccount.setModDt(LocalDateTime.now());
        Account saved = accountRepository.save(newAccount);

        assertNotNull(saved.getId());
        assertEquals("New", saved.getFirstName());
    }

    @Test
    @DisplayName("Update account")
    void updateAccount() {
        Account first = accountRepository.findAllByOrderByIdAsc().get(0);
        first.setFirstName("Updated");
        first.setModDt(LocalDateTime.now());
        accountRepository.save(first);

        Account updated = accountRepository.findById(first.getId()).orElseThrow();
        assertEquals("Updated", updated.getFirstName());
    }

    @Test
    @DisplayName("Delete account")
    void deleteAccount() {
        long countBefore = accountRepository.count();
        Account first = accountRepository.findAllByOrderByIdAsc().get(0);
        accountRepository.deleteById(first.getId());

        assertEquals(countBefore - 1, accountRepository.count());
    }

    @Test
    @DisplayName("Account entity trims fields on persist (handles COBOL fixed-width padding)")
    void trimFieldsOnPersist() {
        Account padded = new Account("  John  ", "  Doe  ", " 123 ", " 456 Main St ", "Y");
        padded.setCreateDt(LocalDateTime.now());
        padded.setModDt(LocalDateTime.now());
        Account saved = accountRepository.save(padded);

        Account found = accountRepository.findById(saved.getId()).orElseThrow();
        assertEquals("John", found.getFirstName());
        assertEquals("Doe", found.getLastName());
        assertEquals("123", found.getPhone());
        assertEquals("456 Main St", found.getAddress());
    }

    @Test
    @DisplayName("isEnabled helper method")
    void isEnabledHelper() {
        Account enabled = new Account("A", "B", "1", "addr", "Y");
        Account disabled = new Account("C", "D", "2", "addr", "N");

        assertTrue(enabled.isEnabled());
        assertFalse(disabled.isEnabled());
    }
}
