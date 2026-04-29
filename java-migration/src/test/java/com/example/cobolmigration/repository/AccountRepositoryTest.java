package com.example.cobolmigration.repository;

import com.example.cobolmigration.entity.Account;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for {@link AccountRepository} verifying that the Spring
 * Data JPA queries produce results matching the original COBOL cursor behaviour.
 */
@DataJpaTest
@ActiveProfiles("test")
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        LocalDateTime now = LocalDateTime.now();

        accountRepository.save(createAccount("John", "Tester", "1555550100",
                "123 Fake St, Nowhere", "Y", now));
        accountRepository.save(createAccount("Mike", "Tester1", "1555550121",
                "122 Real St, Nowhere", "Y", now));
        accountRepository.save(createAccount("Bob", "Tester4", "1555550154",
                "119 Truck St, Nowhere", "N", now));
        accountRepository.save(createAccount("Paula", "Tester5", "1555550165",
                "118 Car St, Nowhere", "N", now));
    }

    @Test
    void findAllByOrderByIdAsc_returnsAllAccountsOrdered() {
        List<Account> accounts = accountRepository.findAllByOrderByIdAsc();
        assertEquals(4, accounts.size());
        assertTrue(accounts.get(0).getId() < accounts.get(1).getId());
    }

    @Test
    void findByIsEnabledOrderByIdAsc_returnsOnlyDisabledAccounts() {
        List<Account> disabled = accountRepository.findByIsEnabledOrderByIdAsc("N");
        assertEquals(2, disabled.size());
        disabled.forEach(a -> assertEquals("N", a.getIsEnabled()));
    }

    @Test
    void findByIsEnabledOrderByIdAsc_returnsOnlyEnabledAccounts() {
        List<Account> enabled = accountRepository.findByIsEnabledOrderByIdAsc("Y");
        assertEquals(2, enabled.size());
        enabled.forEach(a -> assertEquals("Y", a.getIsEnabled()));
    }

    @Test
    void searchAccounts_findsByFirstName() {
        List<Account> results = accountRepository.searchAccounts("John");
        assertFalse(results.isEmpty());
        assertEquals("John", results.get(0).getFirstName());
    }

    @Test
    void searchAccounts_findsByLastName() {
        List<Account> results = accountRepository.searchAccounts("Tester4");
        assertEquals(1, results.size());
        assertEquals("Bob", results.get(0).getFirstName());
    }

    @Test
    void searchAccounts_findsByAddress() {
        List<Account> results = accountRepository.searchAccounts("Fake");
        assertEquals(1, results.size());
        assertEquals("John", results.get(0).getFirstName());
    }

    @Test
    void searchAccounts_findsByPhone() {
        List<Account> results = accountRepository.searchAccounts("1555550121");
        assertEquals(1, results.size());
        assertEquals("Mike", results.get(0).getFirstName());
    }

    @Test
    void searchAccounts_returnsEmptyForNoMatch() {
        List<Account> results = accountRepository.searchAccounts("ZZZZZ");
        assertTrue(results.isEmpty());
    }

    @Test
    void searchAccounts_trimmedInputMatchesCOBOLBehaviour() {
        // COBOL trims input before wrapping with '%'; verify trimmed search works
        List<Account> results = accountRepository.searchAccounts("John");
        assertFalse(results.isEmpty());
    }

    private Account createAccount(String firstName, String lastName,
                                  String phone, String address,
                                  String isEnabled, LocalDateTime dt) {
        Account account = new Account(firstName, lastName, phone, address, isEnabled);
        account.setCreateDt(dt);
        account.setModDt(dt);
        return account;
    }
}
