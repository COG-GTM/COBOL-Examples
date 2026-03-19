package com.cobolmigration.repository;

import com.cobolmigration.model.Account;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for AccountRepository validating all three query patterns
 * that replace the COBOL cursors in sql/sql_example.cbl.
 */
@DataJpaTest
@ActiveProfiles("test")
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();

        // Seed test data matching sql/create_test_db.sql
        accountRepository.save(new Account("John", "Tester", "15555550100", "123 Fake St, Nowhere", 'Y'));
        accountRepository.save(new Account("Mike", "Tester1", "15555550121", "122 Real St, Nowhere", 'Y'));
        accountRepository.save(new Account("Mary", "Tester2", "15555550132", "121 ABC St, Nowhere", 'Y'));
        accountRepository.save(new Account("Bob", "Tester4", "15555550154", "119 Truck St, Nowhere", 'N'));
        accountRepository.save(new Account("Paula", "Tester5", "1555550165", "118 Car St, Nowhere", 'N'));
        accountRepository.save(new Account("Bill", "Tester8", "1555550198", "115 Dock St, Nowhere", 'N'));
    }

    @Test
    @DisplayName("findAllByOrderByIdAsc replaces ACCOUNT-ALL-CUR: returns all accounts sorted by ID")
    void testFindAllByOrderByIdAsc() {
        List<Account> accounts = accountRepository.findAllByOrderByIdAsc();

        assertThat(accounts).hasSize(6);
        // Verify ordering by ID ascending
        for (int i = 1; i < accounts.size(); i++) {
            assertThat(accounts.get(i).getId())
                    .isGreaterThan(accounts.get(i - 1).getId());
        }
    }

    @Test
    @DisplayName("findByIsEnabledOrderByIdAsc replaces ACCOUNT-DISABLED-CUR: returns only disabled accounts")
    void testFindByIsEnabledDisabled() {
        List<Account> disabledAccounts = accountRepository.findByIsEnabledOrderByIdAsc('N');

        assertThat(disabledAccounts).hasSize(3);
        assertThat(disabledAccounts).allMatch(a -> a.getIsEnabled() == 'N');
        assertThat(disabledAccounts).extracting(Account::getFirstName)
                .containsExactly("Bob", "Paula", "Bill");
    }

    @Test
    @DisplayName("findByIsEnabledOrderByIdAsc returns enabled accounts when queried with 'Y'")
    void testFindByIsEnabledEnabled() {
        List<Account> enabledAccounts = accountRepository.findByIsEnabledOrderByIdAsc('Y');

        assertThat(enabledAccounts).hasSize(3);
        assertThat(enabledAccounts).allMatch(a -> a.getIsEnabled() == 'Y');
    }

    @Test
    @DisplayName("searchAccounts replaces ACCOUNT-QUERY-CUR: searches across firstName, lastName, phone, address")
    void testSearchAccountsByFirstName() {
        List<Account> results = accountRepository.searchAccounts("%John%");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("searchAccounts finds matches in lastName field")
    void testSearchAccountsByLastName() {
        List<Account> results = accountRepository.searchAccounts("%Tester%");

        // All accounts have "Tester" in their last name
        assertThat(results).hasSize(6);
    }

    @Test
    @DisplayName("searchAccounts finds matches in address field")
    void testSearchAccountsByAddress() {
        List<Account> results = accountRepository.searchAccounts("%Fake%");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getAddress()).contains("Fake");
    }

    @Test
    @DisplayName("searchAccounts returns empty list for no matches")
    void testSearchAccountsNoResults() {
        List<Account> results = accountRepository.searchAccounts("%NONEXISTENT%");

        assertThat(results).isEmpty();
    }
}
